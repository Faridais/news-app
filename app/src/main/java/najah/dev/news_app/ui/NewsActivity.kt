package najah.dev.news_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.fragment.app.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import androidx.navigation.ui.setupWithNavController

import najah.dev.news_app.R
import najah.dev.news_app.db.ArticleDatabase
import najah.dev.news_app.repository.NewsRepository

import najah.dev.news_app.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private var _binding: ActivityNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val newsRepository = NewsRepository(ArticleDatabase(this))
//        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
//        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        binding.bottomBar.setupWithNavController(this.findViewById<FrameLayout>(R.id.newNavHostFragment).findNavController())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}