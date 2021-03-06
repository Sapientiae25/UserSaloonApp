package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class BookingFragment : Fragment() {

    private lateinit var userItem: UserItem
    private lateinit var tvNoStyles: TextView
    private lateinit var tvNoBooking: TextView
    val bookingList = mutableListOf<BookingItem>()
    val cancelList = mutableListOf<BookingItem>()
    private lateinit var rvBooking: RecyclerView
    private lateinit var rvCancel: RecyclerView
    private lateinit var llCancel: LinearLayout
    private lateinit var llBooking: LinearLayout
    private lateinit var llOldCancels: LinearLayout
    private lateinit var llOldBookings: LinearLayout
    private lateinit var fabHistory: FloatingActionButton
    private lateinit var fabOldStyles: FloatingActionButton
    private lateinit var fabOldCancels: FloatingActionButton
    private var clicked = false
//    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim)}
//    private val rotateClose: Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.to_botton_anim)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Notifications"
        userItem = (activity as DefaultActivity).userItem
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        llOldCancels = rootView.findViewById(R.id.llOldCancels)
        llOldBookings = rootView.findViewById(R.id.llOldBookings)
        fabHistory = rootView.findViewById(R.id.fabHistory)
        fabOldCancels = rootView.findViewById(R.id.fabOldCancels)
        fabOldStyles = rootView.findViewById(R.id.fabOldStyles)
        rvBooking = rootView.findViewById(R.id.rvBooking)
        rvCancel = rootView.findViewById(R.id.rvCancel)
        tvNoBooking = rootView.findViewById(R.id.tvNoBooking)
        llCancel = rootView.findViewById(R.id.llCancel)
        llBooking = rootView.findViewById(R.id.llBooking)
        rvCancel.layoutManager = LinearLayoutManager(context)
        rvCancel.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        rvCancel.adapter = CancelAdapter(cancelList,this)
        rvBooking.adapter = BookingAdapter(bookingList,this)
        rvBooking.layoutManager = LinearLayoutManager(context)
        rvBooking.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        (activity as DefaultActivity).clearNotification()
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        fabHistory.setOnClickListener{ onAddButtonClicked() }
        fabOldCancels.setOnClickListener{ view -> view.findNavController().navigate(R.id.action_bookingFragment_to_cancelFragment)}
        fabOldStyles.setOnClickListener{ view -> view.findNavController().navigate(R.id.action_bookingFragment_to_oldBookingFragment)}

        return rootView
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
//        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked){ llOldBookings.visibility = View.VISIBLE; llOldCancels.visibility = View.VISIBLE
        }else{ llOldBookings.visibility = View.GONE; llOldCancels.visibility = View.GONE } }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked){ llOldBookings.startAnimation(fromBottom); llOldCancels.startAnimation(fromBottom)
            fabHistory.setImageResource(R.drawable.ic_baseline_cancel_24)
        }else{ llOldBookings.startAnimation(toBottom); llOldCancels.startAnimation(toBottom)
            fabHistory.setImageResource(R.drawable.ic_baseline_history_24) }
    }

    private fun loadData(){
        rvCancel.adapter?.notifyItemRangeRemoved(0,cancelList.size)
        cancelList.clear()
        rvBooking.adapter?.notifyItemRangeRemoved(0,bookingList.size)
        bookingList.clear()
        val url = getString(R.string.url,"get_booked.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"BOOKED",response)
                val jObj = JSONObject(response)
                val booked = jObj.getJSONArray("bookings")
                val cancel = jObj.getJSONArray("cancel")
                llCancel.visibility = if (cancel.length() == 0) View.GONE else View.VISIBLE
                if (booked.length() == 0){rvBooking.visibility = View.GONE; tvNoBooking.visibility = View.VISIBLE}
                else {rvBooking.visibility = View.VISIBLE; tvNoBooking.visibility = View.GONE}
                tvNoStyles.visibility = if (booked.length() == 0 && cancel.length() == 0) View.VISIBLE else View.GONE
                for (x in 0 until cancel.length()){
                    val obj = cancel.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val styleId = obj.getString("style_id")
                    val accountName = obj.getString("account_name")
                    val address = obj.getString("address")
                    val sDate = obj.getString("s_date")
                    val sTime = obj.getString("s_time")
                    val accountId = obj.getString("account_id")
                    val reason = obj.getString("reason")
                    val info = obj.getString("info")
                    val time = obj.getString("time")
                    val imageId = obj.getString("image_id")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val accountItem = AccountItem(accountId,accountName, addressItem=AddressItem(address=address))
                    val styleItem =  StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId)
                    cancelList.add(BookingItem("",sTime,sDate,styleItem,reason=reason)) }
                rvCancel.adapter?.notifyItemRangeInserted(0,cancelList.size)

                for (x in 0 until booked.length()){
                    val obj = booked.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val imageId = obj.getString("image_id")
                    val accountName = obj.getString("account_name")
                    val address = obj.getString("address")
                    val sDate = obj.getString("s_date")
                    val sTime = obj.getString("s_time")
                    val bookingId = obj.getString("booking_id")
                    val accountId = obj.getString("account_id")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val accountItem = AccountItem(accountId,accountName, addressItem=AddressItem(address=address))
                    val styleItem =  StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId)
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