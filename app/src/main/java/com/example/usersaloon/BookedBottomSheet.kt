package com.example.usersaloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookedBottomSheet : BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booked_bottom_sheet, container, false)
        val styleItem = arguments?.getParcelable<StyleItem>("styleItem")!!
        val location = arguments?.getBoolean("location")!!
        val accountItem = styleItem.accountItem
        val addressItem = accountItem.addressItem!!
        val tvStyle = rootView.findViewById<TextView>(R.id.tvStyle)
        val tvSaloon = rootView.findViewById<TextView>(R.id.tvSaloon)
        val tvCost = rootView.findViewById<TextView>(R.id.tvCost)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvDate = rootView.findViewById<TextView>(R.id.tvDate)
        val btnReview = rootView.findViewById<AppCompatButton>(R.id.btnReview)
        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)

        tvStyle.text = styleItem.name
        tvSaloon.text = getString(R.string.colon,accountItem.name)
        tvCost.text = getString(R.string.money,styleItem.price)
        tvAddress.text = addressItem.address
        tvDate.text = styleItem.date

        btnReview.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            if (location){
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_oldBookingFragment_to_reviewFragment,bundle) }
            else{activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_bookingFragment_to_reviewFragment,bundle)}
            dismiss()}
        btnGoToStyle.setOnClickListener {
            val bundle = bundleOf(Pair("styleItem",styleItem))
            if (location){
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_oldBookingFragment_to_styleFragment,bundle)}
            else{ activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_bookingFragment_to_styleFragment,bundle) }
            dismiss() }

        return rootView
    }

}
