package com.example.usersaloon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray

class LocationBottomSheet : BottomSheetDialogFragment(){

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_location_bottom_sheet, container, false)
        val userItem = (activity as DefaultActivity).userItem
        var chosenLocation: Int? = null
        val addressList = mutableListOf<AddressItem>()
        val rgLocation = rootView.findViewById<RadioGroup>(R.id.rgLocation)
        val tvAddAddress = rootView.findViewById<TextView>(R.id.tvAddAddress)
        val rbHere = RadioButton(context)
        rbHere.text = getString(R.string.current_location)
        rbHere.id = 0
        rbHere.setOnClickListener{ fetchLocation()}
        rgLocation.addView(rbHere)
        val url = getString(R.string.url,"get_locations.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"SUI",response)
                val arr = JSONArray(response)
                for ( i in 0 until arr.length()){
                    val obj = arr.getJSONObject(i)
                    val address = obj.getString("address")
                    val postcode = obj.getString("postcode")
                    val latitude = obj.getDouble("postcode")
                    val longitude = obj.getDouble("longitude")
                    val chosen = obj.getInt("chosen")
                    val id = obj.getString("id")
                    addressList.add(AddressItem(id,"", postcode, "", address,latitude,longitude))
                    val radioButton = RadioButton(context)
                    if (chosen == 1){chosenLocation = i; radioButton.isChecked = true}
                    radioButton.id = i+1
                    radioButton.setOnClickListener{
                        val url2 = getString(R.string.url,"choose_location.php")
                        val stringRequest: StringRequest = object : StringRequest(
                            Method.POST, url2, Response.Listener { },
                            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                            @Throws(AuthFailureError::class)
                            override fun getParams(): Map<String, String> {
                                val params = HashMap<String, String>()
                                params["user_id"] = userItem.id
                                params["user_id"] = id
                                return params }}
                        VolleySingleton.instance?.addToRequestQueue(stringRequest)
                        val updateLocation = activity as DefaultActivity
                        val locationText = getString(R.string.comma,address,postcode)
                        (activity as DefaultActivity).chosenLocation=AddressItem("","",postcode,"",address,latitude,longitude)
                        updateLocation.update(LatLng(latitude, longitude),locationText)
                        dismiss()}; rgLocation.addView(radioButton) }
                if (chosenLocation == null){rbHere.isChecked = true} },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        tvAddAddress.setOnClickListener {
            val bundle = bundleOf(Pair("setting",false),Pair("update",false))
            this.findNavController().navigate(R.id.action_userFragment_to_addressFragment,bundle)
            dismiss()
        }

        return rootView
    }
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity as DefaultActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101)
            return }
        task.addOnSuccessListener {
            if (it != null){
                val latitude = it.latitude ; val longitude = it.longitude
                val updateLocation = activity as DefaultActivity
                updateLocation.update(LatLng(latitude,longitude),"Current Location")
                dismiss()
            } } }
}
