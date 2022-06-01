package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BookedAdapter (private val styleItemList: MutableList<BookingItem>,val fragment: OldBookingFragment)
    : RecyclerView.Adapter<BookedAdapter.BookedViewHolder>() {

    inner class BookedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val card: CardView = itemView.findViewById(R.id.card)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val styleItem = currentItem.styleItem
            name.text = styleItem.name
            price.text = itemView.context.getString(R.string.money,styleItem.price)
            tvAddress.text = currentItem.accountItem.addressItem?.address
            if (styleItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = styleItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.separate,currentItem.time,currentItem.date)
            itemView.setOnClickListener {
                val bookedBottomSheet = BookedBottomSheet()
                val bundle = bundleOf(Pair("styleItem",currentItem),Pair("location",true))
                bookedBottomSheet.arguments = bundle
                bookedBottomSheet.show(fragment.childFragmentManager,"bookedBottomSheet")
            }
            if (styleItem.imageId.isNotEmpty() && styleItem.imageId != "null"){
            Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${styleItem.imageId}.jpeg")).fit().centerCrop().into(image)}
            else{ card.visibility = View.GONE }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_layout, parent, false)
        return BookedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookedViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}