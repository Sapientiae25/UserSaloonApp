package com.example.usersaloon

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class DayAdapter (private val dayList: MutableList<Triple<Int,Int,Int>>,val clickListener: (Triple<Int,Int,Int>) -> Unit)
    : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(index: Int){
            val currentItem = dayList[index]
            tvDate.text= currentItem.third.toString()
            itemView.setOnClickListener { clickListener(currentItem) } }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_layout, parent, false)
        return DayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = dayList.size
}