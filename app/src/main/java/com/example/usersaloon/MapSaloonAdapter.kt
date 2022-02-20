package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

class MapSaloonAdapter (private val saloonList: MutableList<AccountItem>,val parentFragment: MapFragment)
    : RecyclerView.Adapter<MapSaloonAdapter.MapSaloonViewHolder>() {

    inner class MapSaloonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvOpenHours: TextView = itemView.findViewById(R.id.tvOpenHours)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        private val btnGoToSaloon: AppCompatButton = itemView.findViewById(R.id.btnGoToSaloon)

        fun bind(index: Int){
            val currentItem = saloonList[index]
            val addressItem = currentItem.addressItem!!
            tvName.text = currentItem.name
            tvAddress.text = addressItem.address
            tvOpenHours.text = itemView.context.getString(R.string.separate,currentItem.open,currentItem.close)
            rating.rating = currentItem.rating.toFloat()
            tvDistance.text = itemView.context.getString(R.string.km, addressItem.distance)
            itemView.setOnClickListener {
                for (i in 0 until saloonList.size){
                    if (saloonList[i].clicked){ saloonList[i].clicked=false;notifyItemChanged(i); break } }
                if (btnGoToSaloon.visibility == View.GONE) { currentItem.clicked = true; btnGoToSaloon.visibility = View.VISIBLE
                } else { currentItem.clicked = false; btnGoToSaloon.visibility = View.GONE }
                parentFragment.move(LatLng(addressItem.latitude,addressItem.longitude))
            }
            btnGoToSaloon.visibility = if (currentItem.clicked) View.VISIBLE else View.GONE
            btnGoToSaloon.setOnClickListener {view ->
                val bundle = bundleOf(Pair("accountItem",currentItem))
                view.findNavController().navigate(R.id.action_mapFragment_to_saloonFragment,bundle)  }

         }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapSaloonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.map_saloon_layout,
            parent, false)
        return MapSaloonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MapSaloonViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = saloonList.size
}