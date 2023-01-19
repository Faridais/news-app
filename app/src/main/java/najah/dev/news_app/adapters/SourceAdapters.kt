package com.example.newsapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import najah.dev.news_app.R
import najah.dev.news_app.models.Source

class SourceAdapters(var context: Context, var arrayList: List<Source>) :
    RecyclerView.Adapter<SourceAdapters.ItemHolder>() {
    var onSourceClick : ((String) -> (Unit))? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_view_layout_item_source, parent, false)
        return ItemHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        val charItem: Source = arrayList.get(position)

        holder.subtitle?.text = charItem.category
        holder.titles?.text = charItem.name

        holder.titles?.setOnClickListener {
            Toast.makeText(context, charItem.name, Toast.LENGTH_LONG).show()
        }
        holder.subtitle?.setOnClickListener {
            Toast.makeText(context, charItem.category, Toast.LENGTH_LONG).show()
        }
        holder.itemView.apply {
            setOnClickListener {
                onSourceClick?.invoke(charItem.name)
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Source>){
        this.arrayList = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var subtitle: TextView? = itemView.findViewById<TextView>(R.id.subtitle_text_view_source)
        var titles: TextView? = itemView.findViewById<TextView>(R.id.title_text_view_source)

    }
}