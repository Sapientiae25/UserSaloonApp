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

class SaloonStyleAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<SaloonStyleAdapter.SaloonStyleViewHolder>() {

    inner class SaloonStyleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val card: CardView = itemView.findViewById(R.id.card)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.time_mins,currentItem.time)
            if (currentItem.imageId.isNotEmpty() && currentItem.imageId != "null"){
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)}
            else{ card.visibility = View.GONE }
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_saloonFragment_to_styleFragment,bundle) }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaloonStyleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return SaloonStyleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SaloonStyleViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}