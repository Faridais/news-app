package najah.dev.news_app.models

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)

data class SourceResponse(
    val status: String,
    val sources: MutableList<Source>
)