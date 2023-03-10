package najah.dev.news_app.repository

import najah.dev.news_app.api.RetrofitInstance
import najah.dev.news_app.db.ArticleDatabase
import najah.dev.news_app.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int, category: String) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber, category )

    suspend fun getBreakingNewsSource(countryCode: String, pageNumber: Int, source: String) =
        RetrofitInstance.api.getBreakingNewsSource(countryCode, pageNumber, source)

    suspend fun getSources(countryCode: String) =
        RetrofitInstance.api.getSources(countryCode)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}