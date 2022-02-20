package com.example.usersaloon

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class BookingBottomSheet(): BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking_bottom_sheet, container, false)
        val bookingItem = arguments?.getParcelable<BookingItem>("bookingItem")!!
        val tvStyle = rootView.findViewById<TextView>(R.id.tvStyle)
        val tvCost = rootView.findViewById<TextView>(R.id.tvCost)
        val tvDuration = rootView.findViewById<TextView>(R.id.tvDuration)
        val tvCode = rootView.findViewById<TextView>(R.id.tvCode)

        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)
        tvStyle.text = bookingItem.name
        tvCost.text = bookingItem.cost
        tvDuration.text = getString(R.string.time_distance,bookingItem.start,bookingItem.end)
        tvCode.text = getString(R.string.your_code,"69420")
        btnGoToStyle.setOnClickListener { view ->
            val url = "http://192.168.1.102:8012/saloon/get_recently_viewed.php"
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val obj = JSONObject(response)
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
                    val styleItem = StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)
                    val bundle = bundleOf(Pair("styleItem",styleItem))
                    view.findNavController().navigate(R.id.action_bookingFragment_to_styleFragment,bundle)},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["style_id"] = bookingItem.styleId
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        }
        return rootView
    }

}
