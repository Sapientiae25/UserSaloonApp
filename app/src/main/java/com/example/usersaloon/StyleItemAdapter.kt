package com.example.usersaloon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class StyleItemAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<StyleItemAdapter.StyleItemViewHolder>() {

    inner class StyleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem?.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.time_mins,currentItem.time)
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_styleFragment,bundle) }
            Picasso.get().load(itemView.context.getString(
                R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)

        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return StyleItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StyleItemViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}