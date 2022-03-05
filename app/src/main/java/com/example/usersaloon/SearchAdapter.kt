package com.example.usersaloon

import android.util.Log
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

class SearchAdapter (private val searchList: MutableList<StyleItem>, val activity: DefaultActivity)
    : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)

        fun bind(index: Int){
            val currentItem = searchList[index]
            name.text = currentItem.name
            itemView.setOnClickListener { view ->
                val url = itemView.context.getString(R.string.url,"popular_styles.php")
                val stringRequest: StringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        Log.println(Log.ASSERT,"STM",response)
                        val obj = JSONObject(response)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val accountId = obj.getString("account_id")
                        val accountName = obj.getString("account_name")
                        val accountItem = AccountItem(accountId,accountName)
                        val timeItem = TimeItem(time,maxTime)
                        val styleItem = StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                        params["style_id"] = currentItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_searchFragment_to_styleFragment,bundle)
            }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saloon_item_layout,
            parent, false)
        return SearchViewHolder(itemView) }
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = searchList.size
}