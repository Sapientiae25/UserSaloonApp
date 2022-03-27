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

class PopularAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            name.text = currentItem.name
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_styleFragment,bundle) }
            if (currentItem.imageId.isNotEmpty()){
            Picasso.get().load(itemView.context.getString(
                R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)}
        } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.popular_layout,
            parent, false)
        return PopularViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(position)

    }
    override fun getItemCount() = styleItemList.size
}