package com.example.usersaloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookingBottomSheet : BottomSheetDialogFragment(){
    private lateinit var bookingItem: BookingItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking_bottom_sheet, container, false)
        bookingItem = arguments?.getParcelable("bookingItem")!!
        val tvStyle = rootView.findViewById<TextView>(R.id.tvStyle)
        val tvCost = rootView.findViewById<TextView>(R.id.tvCost)
        val tvTime = rootView.findViewById<TextView>(R.id.tvTime)
        val btnCancel = rootView.findViewById<AppCompatButton>(R.id.btnCancel)
        val tvCode = rootView.findViewById<TextView>(R.id.tvCode)
        val styleItem = bookingItem.styleItem
        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)

        tvStyle.text = styleItem.name
        tvCost.text = getString(R.string.money,styleItem.price)
        tvTime.text = getString(R.string.separate,bookingItem.time,bookingItem.date)
        tvCode.text = getString(R.string.your_code,"69420")
        btnGoToStyle.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_bookingFragment_to_styleFragment,bundle) }
        btnCancel.setOnClickListener { cancelDialog() }

        return rootView
    }
    private fun cancelDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_popup)
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val close = dialog.findViewById<AppCompatButton>(R.id.close)

        close.setOnClickListener {dialog.dismiss() }
        cancel.setOnClickListener {
            val date = getString(R.string.separate,bookingItem.date,bookingItem.time)
            val url = getString(R.string.url,"cancel_check.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val result = response.toInt()
                    if (result > 24){dialog.dismiss();delete() }
                    else { dialog.dismiss();chargeDialog(result) } },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["date"] = date
                    params["booking_id"] = bookingItem.bookingId
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        dialog.show()
    }
    private fun chargeDialog(charge: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.charge_layout)
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val close = dialog.findViewById<AppCompatButton>(R.id.close)
        val tvCharge = dialog.findViewById<TextView>(R.id.tvCharge)
        val total = if (charge > 3) 1f else bookingItem.styleItem.price
        tvCharge.text = getString(R.string.charge,total)

        close.setOnClickListener {dialog.dismiss() }
        cancel.setOnClickListener {dialog.dismiss();delete() }
        dialog.show() }
    private fun delete(){
        Toast.makeText(context,"Appointment Deleted!",Toast.LENGTH_SHORT).show()
        val url = getString(R.string.url,"delete_booking.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener {},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["booking_id"] = bookingItem.bookingId
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
