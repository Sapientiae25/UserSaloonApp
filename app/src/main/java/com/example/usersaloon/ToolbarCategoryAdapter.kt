package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class ToolbarCategoryAdapter (private val categoryList: MutableList<CategoryItem>)
    : RecyclerView.Adapter<ToolbarCategoryAdapter.ToolbarCategoryViewHolder>() {

    inner class ToolbarCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        fun bind(index: Int){
            val currentItem = categoryList[index]
            name.text = currentItem.category
            itemView.setOnClickListener { view -> val bundle = bundleOf(Pair("categoryItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_categoryFragment,bundle) } } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolbarCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.toolbar_text_layout,
            parent, false)
        return ToolbarCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToolbarCategoryViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = categoryList.size
}