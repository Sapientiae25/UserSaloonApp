package com.example.usersaloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray

class PaymentBottomSheet(val clickListener: () -> Unit) : BottomSheetDialogFragment(){

    private lateinit var bookingItem: BookingItem
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.payment_bottom_sheet, container, false)
        val rbCards = rootView.findViewById<RadioGroup>(R.id.rbCards)
        val btnAddCard = rootView.findViewById<AppCompatButton>(R.id.btnAddCard)
        bookingItem = arguments?.getParcelable("bookingItem")!!
        val cardList = mutableListOf<CardItem>()
        val url = getString(R.string.url,"get_cards.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val cardNum = obj.getString("card_number")
                    val cvv = obj.getString("cvv")
                    val expiry = obj.getString("expiry")
                    val cardId = obj.getString("card_id")
                    val btn = RadioButton(context)
                    btn.text = getString(R.string.card_ending, cardNum.takeLast(4))
                    btn.setOnClickListener{book() }
                    rbCards.addView(btn)
                    cardList.add(CardItem(cardId, cardNum, expiry, cvv)) } },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_fk"] = (activity as DefaultActivity).userItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnAddCard.setOnClickListener { dismiss()
            val bundle = bundleOf(Pair("bookingItem",bookingItem))
            activity?.findNavController(R.id.activityFragment)?.navigate(R.id.action_styleFragment_to_addCardFragment,bundle)

        }

        return rootView
    }
    private fun book(){
        val url = getString(R.string.url,"book.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response -> dismiss()
                if (response == "0"){Toast.makeText(context,"Style Booked",Toast.LENGTH_SHORT).show();clickListener()
                    (activity as DefaultActivity).addNotification()}
                else{Toast.makeText(context,"Invalid Time",Toast.LENGTH_SHORT).show()} },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = bookingItem.accountItem.id
                params["start"] = getString(R.string.make_datetime,bookingItem.date,bookingItem.time)
                params["diff"] = bookingItem.styleItem.time
                params["style_id"] = bookingItem.styleItem.id
                params["user_id"] = (activity as DefaultActivity).userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply { setCanceledOnTouchOutside(false) } }

}