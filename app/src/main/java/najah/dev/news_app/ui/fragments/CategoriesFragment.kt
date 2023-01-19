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
import com.example.newsapp.DataSource.CharItemCategory
import kotlinx.coroutines.*
import najah.dev.news_app.adapters.NewsAdapter
import najah.dev.news_app.ui.NewsActivity
import najah.dev.news_app.ui.NewsViewModel
import najah.dev.news_app.utils.Constants
import najah.dev.news_app.utils.Resource
import najah.dev.news_app.R
import najah.dev.news_app.adapters.AlphaAdapters
import najah.dev.news_app.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter

    private var recyclerView: RecyclerView? = null
    private var charItem: ArrayList<CharItemCategory>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var alphaAdapters: AlphaAdapters? = null

    val TAG = "Categories Fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
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
                R.id.action_categoriesFragment_to_articleFragment,
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
                        //Log.d("TAG_test5", newsResponse.articles.toList().get(1).toString())
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
        binding.recyclerViewCategory.visibility = View.INVISIBLE
    }
    private fun showSources(){
        binding.recyclerViewCategory.visibility = View.VISIBLE
    }

    private fun showRecyclerView(){
        binding.rvSearchNews.visibility = View.VISIBLE
    }
    private fun hideRecyclerView(){
        binding.rvSearchNews.visibility = View.INVISIBLE
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
            addOnScrollListener(this@CategoriesFragment.scrollListener)
        }
    }

    private fun initViews() {
        recyclerView = requireView().findViewById(R.id.recyclerViewCategory)
        gridLayoutManager =
            GridLayoutManager(
                requireContext().applicationContext,
                2,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        charItem = ArrayList()
        charItem = setAlphas()
        alphaAdapters = AlphaAdapters(requireContext().applicationContext, charItem!!)
        alphaAdapters?.onCategoryClick = {
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToBreakingNewsFragment(it,null)
            findNavController().navigate(action)
        }
        recyclerView?.adapter = alphaAdapters
    }

    private fun setAlphas(): ArrayList<CharItemCategory> {
        var arrayList: ArrayList<CharItemCategory> = ArrayList()

        arrayList.add(CharItemCategory(R.color.general, "General"))
        arrayList.add(CharItemCategory(R.color.business, "Business"))
        arrayList.add(CharItemCategory(R.color.science, "Science"))
        arrayList.add(CharItemCategory(R.color.technology, "Technology"))
        arrayList.add(CharItemCategory(R.color.health, "Health"))
        arrayList.add(CharItemCategory(R.color.entertainment, "Entertainment"))
        arrayList.add(CharItemCategory(R.color.art, "Sport"))

        return arrayList
    }

}