package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DayAdapter (private val dayList: MutableList<DayItem>,val clickListener: (DayItem) -> Unit)
    : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(index: Int){
            val currentItem = dayList[index]
            if (currentItem.chosen) { tvDate.setBackgroundResource(R.drawable.chosen_circle) }
            else {tvDate.setBackgroundResource(R.drawable.circle)}
            tvDate.text = currentItem.date.first.toString()
            itemView.setOnClickListener {
                tvDate.setBackgroundResource(R.drawable.chosen_circle)
                currentItem.chosen = true
                for (x in 0 until dayList.size){ val item = dayList[x]
                    if (item.chosen && index != x) { item.chosen = false; notifyItemChanged (x);break }}
                clickListener(currentItem) } }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_layout, parent, false)
        return DayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = dayList.size
}