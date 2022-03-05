package com.example.usersaloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray

class DetailsFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_setting, container, false)
        requireActivity().title = "Details"
        val userItem = (activity as DefaultActivity).userItem
        val name = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etNumber = rootView.findViewById<TextInputEditText>(R.id.etNumber)
        val rvAddress = rootView.findViewById<RecyclerView>(R.id.rvAddress)
        val btnSave = rootView.findViewById<AppCompatButton>(R.id.btnSave)
        val tvAddAddress = rootView.findViewById<TextView>(R.id.tvAddAddress)
        val addressList = mutableListOf<AddressItem>()
        rvAddress.layoutManager = LinearLayoutManager(context)
        rvAddress.adapter = AddressAdapter(addressList)
        etNumber.setText(userItem.number)
        name.setText(userItem.name)
        var url = getString(R.string.url,"get_locations.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for ( i in 0 until arr.length()){
                    val obj = arr.getJSONObject(i)
                    val address = obj.getString("address")
                    val city = obj.getString("city")
                    val town = obj.getString("town")
                    val country = obj.getString("country")
                    val postcode = obj.getString("postcode")
                    val id = obj.getString("id")
                    addressList.add(AddressItem(id,city, postcode, country, address,town=town)) }
                rvAddress.adapter?.notifyItemRangeInserted(0,arr.length()) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        tvAddAddress.setOnClickListener {view ->
            val bundle = bundleOf(Pair("update",false))
            view.findNavController().navigate(R.id.action_detailsFragment_to_addressFragment,bundle) }
        btnSave.setOnClickListener { view ->
            var filled = true
            if (etNumber.text?.length != 11 || !(etNumber.text.isNullOrEmpty() && userItem.number.isEmpty())) { filled=false
                etNumber.error = "Please Fill Out This Field"}
            if (name.text.isNullOrEmpty()){filled=false; name.error = "Please Fill Out This Field"}
            if (filled){
                (activity as DefaultActivity).userItem.name = name.text.toString()
                url = getString(R.string.url,"update_name.php")
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener {},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["user_id"] = userItem.id
                        params["name"] = name.text.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                view.findNavController().navigate(R.id.action_detailsFragment_to_settingFragment)}
        }
        return rootView
    }
}
