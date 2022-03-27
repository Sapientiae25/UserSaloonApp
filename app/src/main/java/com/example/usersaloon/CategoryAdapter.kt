package com.example.usersaloon

import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter (private val groupList: List<Pair<String,String>>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var currentGroup = 0
        var filter =  FilterItem()
        private val name: TextView = itemView.findViewById(R.id.name)
        private val filters = listOf("Male","Female","Long","Medium","Short")

        fun bind(index: Int){
            name.text = filters[index]
            currentGroup = if (index > 2) 1 else 0
            itemView.setOnClickListener { view ->
                when (currentGroup) {
                    0 -> {filter.gender = index - (currentGroup * 2)}
                    1 -> {filter.length.add(index - (currentGroup * 2))} }
                val bundle = bundleOf(Pair("filterItem",filter))
                view.findNavController().navigate(R.id.action_userFragment_to_filterStyleFragment,bundle)}
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