package com.example.usersaloon

import android.util.Log
import android.view.*
import android.widget.ImageView
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

class StyleImageAdapter (private val imageList: MutableList<StyleItem>)
    : RecyclerView.Adapter<StyleImageAdapter.StyleImageViewHolder>() {

    inner class StyleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ivStyle: ImageView = itemView.findViewById(R.id.ivStyle)

        fun bind(index: Int){
            val currentItem = imageList[index]
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_exploreFragment_to_styleFragment,bundle)
            }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return StyleImageViewHolder(itemView) }
    override fun onBindViewHolder(holder: StyleImageViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = imageList.size
}