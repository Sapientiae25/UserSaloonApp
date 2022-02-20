package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter (private val styleItemList: MutableList<BookingItem>)
    : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvStyle: TextView = itemView.findViewById(R.id.tvStyle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            tvStyle.text = currentItem.name
            tvDate.text = itemView.context.getString(R.string.separate,currentItem.start,currentItem.end)
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_styleFragment,bundle) } } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.popular_layout,
            parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(position)

    }
    override fun getItemCount() = styleItemList.size
}