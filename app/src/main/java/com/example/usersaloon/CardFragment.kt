package com.example.usersaloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CardFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.card_fragment, container, false)
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        val years = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        for (i in 1 until 8){years.add((year+i).toString())}
        (activity as DefaultActivity).supportActionBar?.title = "Card"
        val userItem = (activity as DefaultActivity).userItem
        val etCVV = rootView.findViewById<TextInputEditText>(R.id.etCVV)
        val etNumber = rootView.findViewById<TextInputEditText>(R.id.etNumber)
        val btnSave = rootView.findViewById<AppCompatButton>(R.id.btnSave)
        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etExpiry = rootView.findViewById<AutoCompleteTextView>(R.id.etExpiry)
        val etYear = rootView.findViewById<AutoCompleteTextView>(R.id.etYear)

        etExpiry.setAdapter(ArrayAdapter(requireContext(),R.layout.text_layout,months))
        etYear.setAdapter(ArrayAdapter(requireContext(),R.layout.text_layout,years.toList()))

        btnSave.setOnClickListener {view ->
            var filled = true
            val expire = etExpiry.text!!
            if (etCVV.text?.length != 3) {filled = false; etCVV.error = "Please Fill This Field Out Correctly"}
            if (etNumber.text?.length != 16) {filled = false; etNumber.error = "Please Fill This Field Out Correctly"}
            if (etYear.text.isEmpty()) {filled = false; etYear.error = "Please Fill This Field Out Correctly"}
            if (etExpiry.text.isEmpty()) {filled = false; etExpiry.error = "Please Fill This Field Out Correctly"}
            if (etName.text.isNullOrEmpty()) {filled = false; etName.error = "Please Fill This Field Out Correctly"}

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
                        val params = HashMap<String, String>()
                        params["number"] = etNumber.text.toString()
                        params["cvv"] = etCVV.text.toString()
                        params["expiry"] = getString(R.string.expire_date,etExpiry.text.toString(),etYear.text.toString())
                        params["user_id"] = userItem.id
                        params["name"] = etName.text.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                view.findNavController().navigate(R.id.action_cardFragment_to_paymentFragment) } }
        return rootView
    }
}
