package com.example.usersaloon

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class AppointmentAdapter (private val bookingList: MutableList<AppointmentItem>,val styleItem: StyleItem)
    : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvStyle: TextView = itemView.findViewById(R.id.tvStyle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        private val btnBook: TextView = itemView.findViewById(R.id.btnBook)

        fun bind(index: Int){
            val currentItem = bookingList[index]
            tvStyle.text = styleItem.name
            tvTime.text = itemView.context.getString(R.string.separate,currentItem.start,currentItem.end)
            tvDuration.text = styleItem.time
            tvAddress.text = styleItem.accountItem?.addressItem?.address
            tvCost.text = String.format("%.1f", styleItem.price)
            btnBook.isEnabled = currentItem.available

        }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_layout, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = bookingList.size
}