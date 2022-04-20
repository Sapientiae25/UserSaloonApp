package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class BookingFragment : Fragment() {

    private lateinit var userItem: UserItem
    private lateinit var tvNoStyles: TextView
    val bookingList = mutableListOf<BookingItem>()
    private lateinit var rvBooking: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Bookings"
        userItem = (activity as DefaultActivity).userItem
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        rvBooking = rootView.findViewById(R.id.rvBooking)
        rvBooking.adapter = BookingAdapter(bookingList,this)
        rvBooking.layoutManager = LinearLayoutManager(context)
        rvBooking.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        (activity as DefaultActivity).clearNotification()
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }

        return rootView
    }
    private fun loadData(){
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
                    val accountItem = AccountItem(accountId,accountName, addressItem=AddressItem(address=address))
                    val styleItem =  StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating)
                    bookingList.add(BookingItem(bookingId,sTime,sDate,styleItem)) }
                rvBooking.adapter?.notifyItemRangeInserted(0,bookingList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}