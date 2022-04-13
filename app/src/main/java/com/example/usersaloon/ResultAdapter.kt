package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ResultAdapter (private val resultList: MutableList<StyleItem>)
    : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val card: CardView = itemView.findViewById(R.id.card)

        fun bind(index: Int){
            val currentItem = resultList[index]
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem?.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.time_mins,currentItem.time)
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_resultFragment_to_styleFragment,bundle) }
            if (currentItem.imageId.isNotEmpty() && currentItem.imageId != "null"){
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)}
            else{ card.visibility = View.GONE }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return ResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = resultList.size
}