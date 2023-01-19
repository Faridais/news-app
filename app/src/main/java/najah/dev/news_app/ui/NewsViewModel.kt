package najah.dev.news_app.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import najah.dev.news_app.db.ArticleDatabase
import najah.dev.news_app.models.Article
import najah.dev.news_app.models.NewsResponse
import najah.dev.news_app.models.Source
import najah.dev.news_app.models.SourceResponse
import najah.dev.news_app.repository.NewsRepository
import najah.dev.news_app.utils.Resource
import retrofit2.Response

class NewsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val newsRepository: NewsRepository = NewsRepository(ArticleDatabase(application))

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val sourcesPage: MutableLiveData<List<Source>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {

    }

    fun getBreakingNews(countryCode: String, category: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage, category)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getBreakingNewsSource(countryCode: String, source: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNewsSource(countryCode, breakingNewsPage, source)
        Log.d("source_tag", "$source" + response.body().toString())
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getSources(countryCode: String) = viewModelScope.launch {
        val response = newsRepository.getSources(countryCode)
        sourcesPage.postValue(handleSourcesResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val olArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    olArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSourcesResponse(response: Response<SourceResponse>): List<Source> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return resultResponse.sources
            }
        }
        return listOf()
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


}