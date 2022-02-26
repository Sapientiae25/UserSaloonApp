package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView

class BookedAdapter (private val styleItemList: MutableList<StyleItem>,val fragment: OldBookingFragment)
    : RecyclerView.Adapter<BookedAdapter.BookedViewHolder>() {

    inner class BookedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem?.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            val timeValue = if (timeItem.maxTime.isNullOrEmpty()) timeItem.time
            else itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            itemView.setOnClickListener {
                val bookedBottomSheet = BookedBottomSheet()
                val bundle = bundleOf(Pair("styleItem",currentItem))
                bookedBottomSheet.arguments = bundle
                bookedBottomSheet.show(fragment.childFragmentManager,"bookedBottomSheet")
            } } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return BookedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookedViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}