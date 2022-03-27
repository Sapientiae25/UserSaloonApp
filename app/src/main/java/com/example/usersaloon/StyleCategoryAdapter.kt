package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class StyleCategoryAdapter (private val categories: MutableList<CategoryItem>)
    : RecyclerView.Adapter<StyleCategoryAdapter.StyleCategoryViewHolder>(){

    inner class StyleCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){val currentItem = categories[index]
            tvCategory.text = currentItem.category
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("categoryItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_categoryFragment,bundle) }
            if (currentItem.imageId.isNotEmpty()){
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)}
        }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_category_layout,
            parent, false)
        return StyleCategoryViewHolder(itemView) }
    override fun onBindViewHolder(holder: StyleCategoryViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = categories.size
}