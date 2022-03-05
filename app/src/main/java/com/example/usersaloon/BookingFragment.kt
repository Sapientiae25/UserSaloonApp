package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class BookingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking, container, false)
        val userItem = (activity as DefaultActivity).userItem
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val rvBooking = rootView.findViewById<RecyclerView>(R.id.rvBooking)
        val bookingList = mutableListOf<BookingItem>()
        rvBooking.adapter = BookingAdapter(bookingList,this)
        rvBooking.layoutManager = LinearLayoutManager(context)
        (activity as DefaultActivity).clearNotification()

        val url = getString(R.string.url,"get_booked.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"BOOKED",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
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
                    val sDate = obj.getString("s_date")
                    val sTime = obj.getString("s_time")
                    val bookingId = obj.getString("booking_id")
                    val accountId = obj.getString("account_id")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val timeItem = TimeItem(time,maxTime)
                    val accountItem = AccountItem(accountId,accountName, addressItem=AddressItem(address=address))
                    val styleItem =  StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem,rating=rating)
                    bookingList.add(BookingItem(bookingId,sTime,sDate,"",styleItem)) }
                rvBooking.adapter?.notifyItemRangeInserted(0,bookingList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }

}
