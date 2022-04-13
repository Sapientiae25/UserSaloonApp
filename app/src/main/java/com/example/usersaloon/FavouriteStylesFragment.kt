package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class FavouriteStylesFragment : Fragment(){

    lateinit var userItem: UserItem
    private lateinit var rvStyles: RecyclerView
    private lateinit var llNoFavourites: LinearLayout
    val styleList = mutableListOf<StyleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_favourite_styles, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Favourite Styles"
        userItem = (activity as DefaultActivity).userItem
        rvStyles = rootView.findViewById(R.id.rvStyles)
        llNoFavourites = rootView.findViewById(R.id.llNoFavourites)
        rvStyles.layoutManager = LinearLayoutManager(context)
        rvStyles.adapter = FavouriteStylesAdapter(styleList)
        rvStyles.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun loadData(){
        val url = getString(R.string.url,"get_liked_styles.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"FAV",response)
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
                    val imageId = obj.getString("image_id")
                    styleList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId)) }
                rvStyles.adapter?.notifyItemRangeInserted(0,styleList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
