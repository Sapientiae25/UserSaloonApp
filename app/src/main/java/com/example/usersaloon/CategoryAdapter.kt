package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CategoryAdapter (private val groupList: List<Pair<String,String>>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var currentGroup = 0
        private var filter =  FilterItem()
        private val name: TextView = itemView.findViewById(R.id.name)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = groupList[index]
            name.text = currentItem.first
            currentGroup = if (index > 2) 1 else 0
            itemView.setOnClickListener { view ->
                when (currentGroup) {
                    0 -> {filter.gender = index - (currentGroup * 2)}
                    1 -> {filter.length.add(index - (currentGroup * 2))} }
                val bundle = bundleOf(Pair("filterItem",filter))
                view.findNavController().navigate(R.id.action_userFragment_to_filterStyleFragment,bundle)}
            if (currentItem.second.isNotEmpty()){
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.second}.jpeg")).fit().centerCrop().into(image)}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.popular_layout,
            parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = groupList.size
}