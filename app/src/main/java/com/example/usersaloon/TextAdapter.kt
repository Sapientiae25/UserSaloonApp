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
import org.json.JSONArray

class TextAdapter (private val textList: MutableList<String>,val activity: DefaultActivity)
    : RecyclerView.Adapter<TextAdapter.TextViewHolder>() {

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val text: TextView = itemView.findViewById(R.id.text)

        fun bind(index: Int){
            val currentItem = textList[index]
            text.text = currentItem
            itemView.setOnClickListener { view ->
                val styleList = mutableListOf<StyleItem>()
                val url = itemView.context.getString(R.string.url,"filter_word_search.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->

                        val arr = JSONArray(response)
                        for (i in 0 until arr.length()){
                            val obj = arr.getJSONObject(i)
                            val name = obj.getString("name")
                            val price = obj.getString("price").toFloat()
                            val time = obj.getString("time")
                            val styleId = obj.getString("style_id")
                            val maxTime = obj.getString("max_time")
                            val info = obj.getString("info")
                            val accountId = obj.getString("account_fk")
                            val rating = obj.getString("rating").toFloatOrNull()
                            val accountName = obj.getString("account_name")
                            val address = obj.getString("address")
                            val imageId = obj.getString("image_id")
                            val accountItem = AccountItem(accountId,accountName,addressItem=AddressItem(address=address))
                            styleList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId))}
                        activity.closeSearch()
                        val bundle = bundleOf(Pair("styleList",styleList))
                        activity.findNavController(R.id.activityFragment).navigate(R.id.action_global_resultFragment,bundle)
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["text"] = currentItem
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }

        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.text_layout,
            parent, false)
        return TextViewHolder(itemView) }
    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = textList.size
}