package com.example.usersaloon

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText

class CardFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.card_fragment, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Card"
        val userItem = (activity as DefaultActivity).userItem
        val etCVV = rootView.findViewById<TextInputEditText>(R.id.etCVV)
        val etExpiry = rootView.findViewById<TextInputEditText>(R.id.etExpiry)
        val etNumber = rootView.findViewById<TextInputEditText>(R.id.etNumber)
        val btnSave = rootView.findViewById<AppCompatButton>(R.id.btnSave)

        etExpiry.setOnKeyListener(View.OnKeyListener { v,keyCode,event ->
            if (keyCode != KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP &&
                etExpiry.text?.length == 2 && !etExpiry.text?.contains("/")!!) {
                    etExpiry.setText(getString(R.string.expiry_format,etExpiry.text))
                    etExpiry.setSelection(etExpiry.length())
                return@OnKeyListener true  }
            false}
        )
        etNumber.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode != KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP &&
                etExpiry.text!!.length % 4 == 0) {
                etExpiry.text = etExpiry.text!!.append(" ")
                etExpiry.setSelection(etExpiry.length())
                return@OnKeyListener true  }
            false}
        )

        btnSave.setOnClickListener {view ->
            var filled = true
            val expire = etExpiry.text!!
            if (etCVV.text?.length != 3) {filled = false; etCVV.error = "Please Fill This Field Out Correctly"}
            if (etNumber.text?.length != 16) {filled = false; etNumber.error = "Please Fill This Field Out Correctly"}
            if (expire.contains("/")){
                val month = expire.split("/")[0].toIntOrNull()
                val day = expire.split("/")[0].toIntOrNull()
                if (month == null || day == null) {filled = false; etExpiry.error = "Please Fill This Field Out Correctly"}
                else if (month > 13 || day > 31) {filled = false; etExpiry.error = "Please Fill This Field Out Correctly"}}
            if (filled){
                val url = getString(R.string.url,"add_card.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = java.util.HashMap<String, String>()
                        params["number"] = etNumber.text.toString()
                        params["cvv"] = etCVV.text.toString()
                        params["expiry"] = etExpiry.text.toString()
                        params["user_id"] = userItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                view.findNavController().navigate(R.id.action_cardFragment_to_paymentFragment)
            }
        }

        return rootView
    }
}
