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
import androidx.navigation.fragment.findNavController
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
        val tvTime = rootView.findViewById<TextView>(R.id.tvTime)
        val tvCode = rootView.findViewById<TextView>(R.id.tvCode)
        val styleItem = bookingItem.styleItem

        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)
        tvStyle.text = styleItem.name
        tvCost.text = getString(R.string.money,styleItem.price)
        tvTime.text = getString(R.string.separate,bookingItem.time,bookingItem.date)
        tvCode.text = getString(R.string.your_code,"69420")
        btnGoToStyle.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_bookingFragment_to_styleFragment,bundle)
        }
        return rootView
    }

}
