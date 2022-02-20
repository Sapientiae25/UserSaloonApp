package com.example.usersaloon

import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class SaloonAdapter (private val saloonItemList: MutableList<AccountItem>)
    : RecyclerView.Adapter<SaloonAdapter.SaloonViewHolder>() {

    inner class SaloonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)

        fun bind(index: Int){
            val currentItem = saloonItemList[index]
            val addressItem = currentItem.addressItem
            name.text = currentItem.name
            val address  = if (addressItem?.distance != null) itemView.context.getString(R.string.separate,addressItem.address,
                addressItem.distance) else addressItem?.address
            tvDistance.text = address
            rating.rating = currentItem.rating.toFloat()
            itemView.setOnClickListener {view ->
                val bundle = bundleOf(Pair("accountItem",currentItem))
                view.findNavController().navigate(R.id.action_userFragment_to_saloonFragment,bundle) } } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaloonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saloon_item_layout,
            parent, false)
        return SaloonViewHolder(itemView) }
    override fun onBindViewHolder(holder: SaloonViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = saloonItemList.size
}