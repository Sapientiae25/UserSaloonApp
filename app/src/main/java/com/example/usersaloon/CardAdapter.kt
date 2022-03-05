package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class CardAdapter (private val cardList: MutableList<CardItem>)
    : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        private val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

        fun bind(index: Int){
            val currentItem = cardList[index]
            tvCardNumber.text = itemView.context.getString(R.string.card_ending,currentItem.number.takeLast(4))
            ivDelete.setOnClickListener {
                val url = itemView.context.getString(R.string.url,"delete_card.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = java.util.HashMap<String, String>()
                        params["card_id"] = currentItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_layout,
            parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = cardList.size
}