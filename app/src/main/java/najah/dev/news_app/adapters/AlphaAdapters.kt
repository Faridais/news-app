package najah.dev.news_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.DataSource.CharItemCategory
import najah.dev.news_app.R
import najah.dev.news_app.databinding.GridViewLayoutItemCategoryBinding

class AlphaAdapters(var context: Context, var arrayList: ArrayList<CharItemCategory>) :
    RecyclerView.Adapter<AlphaAdapters.ItemHolder>() {

    var onCategoryClick : ((String) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_view_layout_item_category, parent, false)
        return ItemHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        val charItem: CharItemCategory = arrayList.get(position)

        holder.binding.iconImageView.setImageResource(charItem.icons!!)
        holder.binding.titleTextView.text = charItem.alpha

        holder.binding.apply {
            categoryItem.setOnClickListener {
                onCategoryClick?.invoke(charItem.alpha)
            }
        }

    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = GridViewLayoutItemCategoryBinding.bind(itemView)

    }
}