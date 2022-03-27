package com.example.usersaloon

import android.view.*
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ExploreImageAdapter (private val imageList: MutableList<StyleItem>)
    : RecyclerView.Adapter<ExploreImageAdapter.StyleImageViewHolder>() {

    inner class StyleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = imageList[index]
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_exploreFragment_to_styleFragment,bundle) }
            Picasso.get().load(itemView.context.getString(
                R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.explore_layout, parent, false)
        return StyleImageViewHolder(itemView) }
    override fun onBindViewHolder(holder: StyleImageViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = imageList.size
}