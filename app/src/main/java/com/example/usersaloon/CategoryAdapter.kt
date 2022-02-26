package com.example.usersaloon

import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter (private val groupList: List<List<String>>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var currentId = 0
        private var currentGroup = 0
        var filter =  FilterItem()

        fun bind(index: Int){
            val name: TextView = itemView.findViewById(R.id.name)
            var i = 0
            loop@ for (x in groupList.indices){ for (y in groupList[x].indices) { if (index == i) { name.text = groupList[x][y]
                currentGroup=x;currentId=y;break@loop };i+=1 } }
            itemView.setOnClickListener { view ->
                when (currentGroup) {
                    0 -> {filter.gender = currentId}
                    1 -> {filter.length.add(currentId)} }
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
    override fun getItemCount(): Int {
        var x=0
        for (i in groupList){x += i.size}
        return x
    }
}