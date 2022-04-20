package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONObject

class FilterImageAdapter(private var images: MutableList<Triple<String,String,AccountItem>>)
    : RecyclerView.Adapter<FilterImageAdapter.FilterImageViewHolder>(){

    inner class FilterImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        fun bind(index: Int){
            val currentItem = images[index]
            Picasso.get().load(itemView.context.getString(
                R.string.url,"style_images/${currentItem.second}.jpeg")).fit().centerCrop().into(image)
            image.setOnClickListener { view ->
                val styleId = currentItem.first
                val url = itemView.context.getString(R.string.url,"find_style.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val info = obj.getString("info")
                        val rating = obj.getString("rating").toFloatOrNull()
                        val styleItem = StyleItem(name,price,time,info,styleId,rating=rating,accountItem=currentItem.third)
                        val bundle = bundleOf(Pair("styleItem",styleItem))
                        view.findNavController().navigate(R.id.action_filterStyleFragment_to_styleFragment,bundle) },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(
                    AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["style_id"] = styleId
                    return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return FilterImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilterImageViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = images.size
}