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

class FavouriteSaloonsFragment : Fragment(){

    lateinit var userItem: UserItem
    private lateinit var rvSaloons: RecyclerView
    private lateinit var llNoFavourites: LinearLayout
    val saloonList = mutableListOf<AccountItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_favourite_saloons, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Favourite Saloons"
        userItem = (activity as DefaultActivity).userItem
        rvSaloons = rootView.findViewById(R.id.rvSaloons)
        llNoFavourites = rootView.findViewById(R.id.llNoFavourites)
        rvSaloons.layoutManager = LinearLayoutManager(context)
        rvSaloons.adapter = FavouriteSaloonAdapter(saloonList)
        rvSaloons.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun loadData(){
        rvSaloons.adapter?.notifyItemRangeRemoved(0,saloonList.size)
        saloonList.clear()

        val url = getString(R.string.url,"get_liked_saloons.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"FAV",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){llNoFavourites.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val accountId = obj.getString("account_id")
                    val addressId = obj.getString("address_id")
                    val address = obj.getString("address")
                    val postcode = obj.getString("postcode")
                    val rating = obj.getString("rating")
                    val latitude = obj.getDouble("latitude")
                    val longitude = obj.getDouble("longitude")
                    val open = obj.getString("open")
                    val close = obj.getString("close")
                    val imageId = obj.getString("image_id")
                    val addressItem = AddressItem(addressId,postcode=postcode,address=address,latitude=latitude,longitude=longitude)
                    saloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating,
                        imageId=imageId)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
