package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class SaloonSearchAdapter (private val saloonList: MutableList<AccountItem>)
    : RecyclerView.Adapter<SaloonSearchAdapter.SaloonSearchViewHolder>() {

    inner class SaloonSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val text: TextView = itemView.findViewById(R.id.text)

        fun bind(index: Int){
            val currentItem = saloonList[index]
            text.text = currentItem.name
            itemView.setOnClickListener { view ->
                val url = itemView.context.getString(R.string.url,"get_saloon.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        Log.println(Log.ASSERT,"ARR",response)
                        val obj = JSONObject(response)
                        val name = obj.getString("name")
                        val accountId = obj.getString("account_id")
                        val addressId = obj.getString("address_id")
                        val address = obj.getString("address")
                        val postcode = obj.getString("postcode")
                        val rating = obj.getString("rating")
                        val open = obj.getString("open")
                        val close = obj.getString("close")
                        val addressItem = AddressItem(addressId,"",postcode,"",address)
                        val accountItem = AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating)
                        val bundle = bundleOf(Pair("accountItem",accountItem))
                        view.findNavController().navigate(R.id.action_searchFragment_to_saloonFragment,bundle) },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> { return HashMap() }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaloonSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.text_layout,
            parent, false)
        return SaloonSearchViewHolder(itemView) }
    override fun onBindViewHolder(holder: SaloonSearchViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = saloonList.size
}