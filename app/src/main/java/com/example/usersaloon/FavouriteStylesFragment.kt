package com.example.usersaloon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.*

class FavouriteStylesFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_favourite_styles, container, false)
        requireActivity().title = "Favourite Styles"
        val userItem = (activity as DefaultActivity).userItem
        val styleList = mutableListOf<StyleItem>()
        val rvStyles = rootView.findViewById<RecyclerView>(R.id.rvStyles)
        val llNoFavourites = rootView.findViewById<LinearLayout>(R.id.llNoFavourites)
        rvStyles.layoutManager = LinearLayoutManager(context)
        rvStyles.adapter = FavouriteStylesAdapter(styleList)
        val url = getString(R.string.url,"get_style_likes.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                if (arr.length() == 0){llNoFavourites.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
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
                    styleList.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)) }
                rvStyles.adapter?.notifyItemRangeInserted(0,styleList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        return rootView
    }
}
