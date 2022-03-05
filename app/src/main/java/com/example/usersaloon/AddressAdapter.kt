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

class AddressAdapter (private val addressList: MutableList<AddressItem>)
    : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvPostcode: TextView = itemView.findViewById(R.id.tvPostcode)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvCity: TextView = itemView.findViewById(R.id.tvCity)
        private val tvCountry: TextView = itemView.findViewById(R.id.tvCountry)

        fun bind(index: Int){
            val currentItem = addressList[index]
            tvAddress.text = currentItem.address
            tvPostcode.text = currentItem.postcode
            tvCity.text = currentItem.city
            tvCountry.text = currentItem.country
            itemView.setOnLongClickListener {
                val dialog = Dialog(itemView.context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.delete_layout)
                val delete = dialog.findViewById<TextView>(R.id.delete)
                delete.setOnClickListener { dialog.dismiss()
                    val url = itemView.context.getString(R.string.url,"delete_location.php")
                    val stringRequest: StringRequest = object : StringRequest(
                        Method.POST, url, Response.Listener {},
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["location_id"] = currentItem.id
                            return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest)
                }
                true
            }
            itemView.setOnClickListener {view ->
                val bundle = bundleOf(Pair("update",true),Pair("addressItem",currentItem))
                view.findNavController().navigate(R.id.action_detailsFragment_to_addressFragment,bundle)  }
        }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_layout,
            parent, false)
        return AddressViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = addressList.size
}