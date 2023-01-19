package najah.dev.news_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Adapters.SourceAdapters


import kotlinx.coroutines.*
import najah.dev.news_app.adapters.NewsAdapter
import najah.dev.news_app.ui.NewsActivity
import najah.dev.news_app.ui.NewsViewModel
import najah.dev.news_app.utils.Constants
import najah.dev.news_app.utils.Resource
import najah.dev.news_app.R
import najah.dev.news_app.databinding.FragmentSearchNewsBinding

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var sourceAdapters: SourceAdapters? = null

    val TAG = "SearchNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()
        initViews()

        // navigate to the clicked article
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )

        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                    if (editable.toString().isEmpty()){
                        hideRecyclerView()
                        showSources()

                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideSources()
                    showRecyclerView()
                    hideProgressBar()
                    response.data?.let { newsResponse ->

                        newsAdapter.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error accrued: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun hideSources(){
        binding.recyclerViewSource.visibility = View.INVISIBLE
    }

    private fun showSources(){
        binding.recyclerViewSource.visibility = View.VISIBLE
    }
    private fun hideRecyclerView(){
        binding.rvSearchNews.visibility = View.INVISIBLE
    }

    private fun showRecyclerView(){
        binding.rvSearchNews.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
    private fun clear(){

    }

    private fun initViews() {
//        sourceAdapters = SourceAdapters()
//        recycler_view_source.apply {
//            adapter = sourceAdapters
//            gridLayoutManager = GridLayoutManager(
//                requireContext().applicationContext,
//                2,
//                LinearLayoutManager.VERTICAL,
//                false)
////            addOnScrollListener(this@SearchNewsFragment.scrollListener)
//        }
        recyclerView = requireView().findViewById(R.id.recycler_view_source)
        gridLayoutManager =
            GridLayoutManager(
                requireContext().applicationContext,
                2,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
//        charItem = ArrayList()
//        charItem = setSources()
        sourceAdapters = SourceAdapters(requireContext().applicationContext, listOf())
        viewModel.getSources("us")
        viewModel.sourcesPage.observe(viewLifecycleOwner){
            sourceAdapters?.submitList(it)
        }
        sourceAdapters?.onSourceClick = {
            val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToBreakingNewsFragment(null, it)
            findNavController().navigate(action)
        }
        recyclerView?.adapter = sourceAdapters
    }

//    private fun setSources(): ArrayList<CharItemSource> {
//        var arrayList: ArrayList<CharItemSource> = ArrayList()
//
//        arrayList.add(CharItemSource("General", "ABC News"))
//        arrayList.add(CharItemSource("General", "Al Jazeera English"))
//        arrayList.add(CharItemSource("Technology", "Ars Technica"))
//        arrayList.add(CharItemSource("General", "Associated Press"))
//        arrayList.add(CharItemSource("General", "Axios"))
//        arrayList.add(CharItemSource("Sports", "Bleacher Report"))
//        arrayList.add(CharItemSource("General", "New York Times"))
//        arrayList.add(CharItemSource("General", "Fox News"))
//
//
//
//
//        return arrayList
//    }

}