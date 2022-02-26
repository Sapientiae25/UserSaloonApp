package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter (private val styleItemList: MutableList<BookingItem>,val fragment: BookingFragment)
    : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val styleItem = currentItem.styleItem
            name.text = styleItem.name
            price.text = itemView.context.getString(R.string.money,styleItem.price)
            tvAddress.text = styleItem.accountItem?.addressItem?.address
            if (styleItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = styleItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.separate,currentItem.time,currentItem.date)
            itemView.setOnClickListener {
                val bookingBottomSheet = BookingBottomSheet()
                val bundle = bundleOf(Pair("bookingItem",currentItem))
                bookingBottomSheet.arguments = bundle
                bookingBottomSheet.show(fragment.childFragmentManager,"bookingBottomSheet") }
        } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_layout,
            parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(position)

    }
    override fun getItemCount() = styleItemList.size
}