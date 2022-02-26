package com.example.usersaloon

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
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

class BookedBottomSheet(): BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booked_bottom_sheet, container, false)
        val styleItem = arguments?.getParcelable<StyleItem>("styleItem")!!
        val accountItem = styleItem.accountItem!!
        val addressItem = accountItem.addressItem!!
        val tvStyle = rootView.findViewById<TextView>(R.id.tvStyle)
        val tvSaloon = rootView.findViewById<TextView>(R.id.tvSaloon)
        val tvCost = rootView.findViewById<TextView>(R.id.tvCost)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvDate = rootView.findViewById<TextView>(R.id.tvDate)
        val btnReview = rootView.findViewById<AppCompatButton>(R.id.btnReview)
        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)

        tvStyle.text = styleItem.name
        tvSaloon.text = accountItem.name
        tvCost.text = getString(R.string.money,styleItem.price)
        tvAddress.text = addressItem.address
        tvDate.text = styleItem.date

        btnReview.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_oldBookingFragment_to_reviewFragment,bundle) }
        btnGoToStyle.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_oldBookingFragment_to_styleFragment,bundle)}


        return rootView
    }

}
