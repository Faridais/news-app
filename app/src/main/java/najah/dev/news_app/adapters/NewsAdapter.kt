package najah.dev.news_app.adapters

import android.text.method.TextKeyListener.clear
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import kotlinx.android.synthetic.main.item_article_preview.view.*
import najah.dev.news_app.R
import najah.dev.news_app.models.Article

class NewsAdapter : ListAdapter<Article, NewsAdapter.ArticleViewHolder>( object : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
){

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)




//    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(findViewById(R.id.article_imageView))
            findViewById<TextView>(R.id.source).text = article.source?.name
            findViewById<TextView>(R.id.headline).text = article.title
//            tvDescription.text = article.description
//            tvPublishedAt.text = article.publishedAt
            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }



//    override fun getItemCount(): Int {
////        return differ.currentList.size
//    }

    fun setOnItemClickListener(listen: (Article) -> Unit){
        onItemClickListener = listen
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

}