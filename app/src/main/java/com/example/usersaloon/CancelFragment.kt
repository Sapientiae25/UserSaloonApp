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

class CancelFragment : Fragment(){

    private lateinit var tvNoCancels: TextView
    private lateinit var rvCancels: RecyclerView
    val cancelList = mutableListOf<BookingItem>()
    private lateinit var userItem: UserItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_cancel, container, false)
        userItem = (activity as DefaultActivity).userItem
        (activity as DefaultActivity).supportActionBar?.title = "Cancellations"
        rvCancels = rootView.findViewById(R.id.rvCancels)
        tvNoCancels = rootView.findViewById(R.id.tvNoCancels)
        rvCancels.adapter = CancelAdapter(cancelList,this)
        rvCancels.layoutManager = LinearLayoutManager(context)
        rvCancels.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun loadData(){
        val url = getString(R.string.url,"get_cancels.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"CAN",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){
                    rvCancels.visibility = View.GONE; tvNoCancels.visibility = View.VISIBLE}
                else {rvCancels.visibility = View.VISIBLE; tvNoCancels.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
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
                rvCancels.adapter?.notifyItemRangeInserted(0,cancelList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
