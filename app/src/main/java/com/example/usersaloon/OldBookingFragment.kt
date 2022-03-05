package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class OldBookingFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_old_booking, container, false)
        val userItem = (activity as DefaultActivity).userItem
        requireActivity().title = "Bookings"
        val bookedList = mutableListOf<StyleItem>()
        val rvBooking = rootView.findViewById<RecyclerView>(R.id.rvBooking)
        val tvNoBooking = rootView.findViewById<TextView>(R.id.tvNoBooking)
        rvBooking.adapter = BookedAdapter(bookedList,this)
        rvBooking.layoutManager = LinearLayoutManager(context)
        val url = getString(R.string.url,"booked.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"BKED",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoBooking.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val accountName = obj.getString("account_name")
                    val address = obj.getString("address")
                    val start = obj.getString("start")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val accountFk = obj.getString("account_fk")
                    val accountItem = AccountItem(accountFk,accountName,addressItem=AddressItem(address=address))
                    val timeItem = TimeItem(time,maxTime)
                    bookedList.add(StyleItem(name,price,timeItem,info,id=styleId,date=start,accountItem=accountItem,rating=rating)) }
                rvBooking.adapter?.notifyItemRangeInserted(0,bookedList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        return rootView
    }
}
