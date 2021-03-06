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

class FavouriteSaloonAdapter (private val saloonList: MutableList<AccountItem>)
    : RecyclerView.Adapter<FavouriteSaloonAdapter.FavouriteSaloonViewHolder>() {

    inner class FavouriteSaloonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvOpenHours: TextView = itemView.findViewById(R.id.tvOpenHours)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val card: CardView = itemView.findViewById(R.id.card)

        fun bind(index: Int){
            val currentItem = saloonList[index]
            val addressItem = currentItem.addressItem!!
            tvName.text = currentItem.name
            tvAddress.text = addressItem.address
            tvOpenHours.text = itemView.context.getString(R.string.separate,currentItem.open,currentItem.close)
            rating.rating = currentItem.rating.toFloat()
            itemView.setOnClickListener {view ->
                val bundle = bundleOf(Pair("accountItem",currentItem))
                view.findNavController().navigate(R.id.action_favouriteSaloonsFragment_to_saloonFragment,bundle)  }
            if (currentItem.imageId.isNotEmpty() && currentItem.imageId != "null"){
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"saloon_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)}
            else{ card.visibility = View.GONE }
        }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteSaloonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favourite_saloon_layout,
            parent, false)
        return FavouriteSaloonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouriteSaloonViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = saloonList.size
}