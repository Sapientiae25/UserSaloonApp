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

class FavouriteSaloonsFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_favourite_saloons, container, false)
        requireActivity().title = "Favourite Saloons"
        val userItem = (activity as DefaultActivity).userItem
        val saloonList = mutableListOf<AccountItem>()
        val rvSaloons = rootView.findViewById<RecyclerView>(R.id.rvSaloons)
        val llNoFavourites = rootView.findViewById<LinearLayout>(R.id.llNoFavourites)
        rvSaloons.layoutManager = LinearLayoutManager(context)
        rvSaloons.adapter = FavouriteSaloonAdapter(saloonList)
        val url = getString(R.string.url,"get_liked_saloons.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
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
                    val addressItem = AddressItem(addressId,postcode,address,latitude=latitude,longitude=longitude)
                    saloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        return rootView
    }
}
