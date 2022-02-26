package com.example.usersaloon

import android.util.Log
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
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)

        fun bind(index: Int){
            val currentItem = saloonItemList[index]
            val addressItem = currentItem.addressItem
            name.text = currentItem.name
            tvAddress.text = addressItem?.address
            Log.println(Log.ASSERT,"SAA",addressItem?.distance.toString())
            if (addressItem?.distance != null){tvDistance.text = itemView.context.getString(R.string.km,addressItem.distance) }
            else {tvDistance.visibility = View.GONE}
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