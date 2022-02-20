package com.example.usersaloon

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import org.json.JSONObject
import java.io.IOException

class AddressFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_address, container, false)
        requireActivity().title = "Details"
        val update = arguments?.getBoolean("update")!!
        val setting = arguments?.getBoolean("setting")!!
        var addressItem = arguments?.getParcelable<AddressItem>("addressItem")
        val userItem = (activity as DefaultActivity).userItem
        val etAddress = rootView.findViewById<TextInputEditText>(R.id.etAddress)
        val etPostcode = rootView.findViewById<TextInputEditText>(R.id.etPostcode)
        val etCity = rootView.findViewById<TextInputEditText>(R.id.etCity)
        val acCountry = rootView.findViewById<AutoCompleteTextView>(R.id.acCountry)
        val etTown = rootView.findViewById<TextInputEditText>(R.id.etTown)
        val btnSave = rootView.findViewById<AppCompatButton>(R.id.btnSave)
        val countries = arrayListOf("England","Whales","Scotland","USA")

        etAddress.setText("6 Rochester Close")
        etPostcode.setText("SW16 5DL")
        etCity.setText("London")
        etTown.setText("Streatham")
        acCountry.setAdapter(ArrayAdapter(requireContext(),R.layout.text_layout,countries.toTypedArray()))

        if (update){
            etAddress.setText(addressItem!!.address)
            etPostcode.setText(addressItem.postcode)
            etCity.setText(addressItem.city)
            etTown.setText(addressItem.town)
            acCountry.setText(addressItem.country) }

        btnSave.setOnClickListener { view ->
            var filled = true
            if (etAddress.text!!.isEmpty()){filled=false;etAddress.error="This field must be filled"}
            if (etPostcode.text!!.isEmpty()){filled=false;etPostcode.error="This field must be filled"}
            if (etCity.text!!.isEmpty()){filled=false;etCity.error="This field must be filled"}
            if (etTown.text!!.isEmpty()){filled=false;etTown.error="This field must be filled"}
            if (filled){
                val address = etAddress.text.toString()
                val postcode = etPostcode.text.toString()
                val country = acCountry.text.toString()
                val city = etCity.text.toString()
                val town = etTown.text.toString()
                val latLong = getLocationFromAddress(getString(R.string.location_comm,postcode,address,town,city,country))
                val lat = latLong?.latitude.toString()
                val long = latLong?.longitude.toString()
                if (update){
                    val url = "http://192.168.1.102:8012/saloon/update_location.php"
                    val stringRequest = object : StringRequest(
                        Method.POST, url, Response.Listener {  },
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["location_id"] = addressItem!!.id
                            params["address"] = address
                            params["city"] = city
                            params["postcode"] = postcode
                            params["country"] = country
                            params["latitude"] = lat
                            params["longitude"] = long
                            params["town"] = town
                            return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest)
                }else {
                    val url = "http://192.168.1.102:8012/saloon/save_location.php"
                    val stringRequest = object : StringRequest(
                        Method.POST, url, Response.Listener { response ->
                            val obj = JSONObject(response)
                            val id = obj.getString("address_id")
                            addressItem = AddressItem(id,city, postcode, country, address,lat.toDouble(),long.toDouble())
                            if (!setting){ view.findNavController().navigate(R.id.action_addressFragment_to_userFragment)} },
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["user_id"] = userItem.id
                            params["address"] = address
                            params["city"] = city
                            params["town"] = town
                            params["postcode"] = postcode
                            params["country"] = country
                            params["latitude"] = lat
                            params["longitude"] = long
                            return params } }
                    VolleySingleton.instance?.addToRequestQueue(stringRequest) }
                if (setting){ view.findNavController().navigate(R.id.action_addressFragment_to_detailsFragment)} }
        }
        return rootView
    }
    private fun getLocationFromAddress(strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        try {
            Log.println(Log.ASSERT,"ADD",strAddress.toString())
            val address = coder.getFromLocationName(strAddress, 1)
            Log.println(Log.ASSERT,"ADD",address.toString())
            if (address == null) { return null }
            val location: Address = address[0]
            return LatLng(location.latitude, location.longitude)
        } catch (e: IOException) { e.printStackTrace() }
        return null
    }
}
