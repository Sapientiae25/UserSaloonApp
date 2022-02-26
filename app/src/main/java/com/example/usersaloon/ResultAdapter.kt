package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class ResultAdapter (private val resultList: MutableList<StyleItem>)
    : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)

        fun bind(index: Int){
            val currentItem = resultList[index]
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem?.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            val timeValue = if (timeItem.maxTime.isNullOrEmpty()) timeItem.time
            else itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_resultFragment_to_styleFragment,bundle) }
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