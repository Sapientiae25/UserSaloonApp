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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

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
        rvBooking.adapter = BookingAdapter(bookingList)
        rvBooking.layoutManager = LinearLayoutManager(context)

        val url = "http://192.168.1.102:8012/saloon/get_recently_viewed.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val styleId = obj.getString("style_fk")
                    val bookingId = obj.getString("booking_fk")
                    val start = obj.getString("start")
                    val end = obj.getString("end")
                    val cost = obj.getString("cost")
                    bookingList.add(BookingItem(bookingId,start,end,name,cost,styleId=styleId)) }
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
