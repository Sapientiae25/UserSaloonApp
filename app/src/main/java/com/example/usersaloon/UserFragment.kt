package com.example.usersaloon

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray


class UserFragment : Fragment(), UpdateLocation {

    private val gender = listOf("Male","Female")
    private val length = listOf("Long","Medium","Short")
    private var currentLat: Double? = null
    private var currentLong: Double? = null
    private val filters = listOf(gender,length)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var updateLocation: UpdateLocation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.activity_user, container, false)
        val userItem = (activity as DefaultActivity).userItem
        val rvPopular = rootView.findViewById<RecyclerView>(R.id.rvPopular)
        val rvSaloons = rootView.findViewById<RecyclerView>(R.id.rvSaloons)
        val rvCategories = rootView.findViewById<RecyclerView>(R.id.rvCategories)
        val rvRecent = rootView.findViewById<RecyclerView>(R.id.rvRecent)
        val llRecent = rootView.findViewById<LinearLayout>(R.id.llRecent)
        val recentList = mutableListOf<StyleItem>()
        val popularList = mutableListOf<StyleItem>()
        val saloonList = mutableListOf<AccountItem>()
        rvPopular.adapter = PopularAdapter(popularList)
        rvPopular.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvSaloons.adapter = SaloonAdapter(saloonList)
        rvSaloons.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        rvCategories.adapter = CategoryAdapter(filters)
        rvCategories.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        rvRecent.adapter = PopularAdapter(recentList)
        rvRecent.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        activity?.title = "Sapientiae"
        var filterSize=0
        updateLocation = activity as DefaultActivity
        for (i in filters){filterSize += i.size}
        rvCategories.adapter?.notifyItemRangeInserted(0,filterSize)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as DefaultActivity)
        var url = getString(R.string.url,"get_chosen_locations.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                if (arr.length() == 0){fetchLocation() }
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val address = obj.getString("address")
                    val postcode = obj.getString("postcode")
                    val latitude = obj.getDouble("latitude")
                    val longitude = obj.getDouble("longitude")
                    currentLong = longitude
                    currentLat = longitude
                    (activity as DefaultActivity).chosenLocation=AddressItem("","",postcode,"", address,latitude,longitude)
                    updateLocation.update(LatLng(latitude,longitude), getString(R.string.comma,address,postcode)) } },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"popular_styles.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val accountId = obj.getString("account_id")
                    val accountName = obj.getString("account_name")
                    val accountItem = AccountItem(accountId,accountName)
                    val timeItem = TimeItem(time,maxTime)
                    popularList.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)) }
                rvPopular.adapter?.notifyItemRangeInserted(0,popularList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = java.util.HashMap<String, String>()
                params["gender"] = userItem.gender.toString()
                return params  }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_saloons.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"ARR",response)
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val accountId = obj.getString("account_id")
                    val addressId = obj.getString("address_id")
                    val address = obj.getString("address")
                    val postcode = obj.getString("postcode")
                    val rating = obj.getString("rating")
                    val latitude = obj.getDouble("latitude")
                    val longitude = obj.getDouble("longitude")
                    val open = obj.getString("open")
                    val close = obj.getString("close")
                    var distance: String? = null
                    if (currentLat != null){distance = String.format("%.2f",getDistance(currentLat!!,
                        currentLong!!,latitude,longitude))}
                    val addressItem = AddressItem(addressId,"",postcode,"",address,latitude,longitude,distance)
                    saloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { return HashMap() }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_recently_viewed.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"RVW",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){llRecent.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val styleId = obj.getString("style_fk")
                    recentList.add(StyleItem(name,id=styleId)) }
                rvRecent.adapter?.notifyItemRangeInserted(0,recentList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_fk"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity as DefaultActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101)
            return }
        task.addOnSuccessListener {
            if (it != null){
                currentLat = it.latitude
                currentLong= it.longitude
                updateLocation.update(LatLng(it.latitude,it.longitude), "Current Location")
            } } }
    private fun getDistance(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat,startLon,endLat,endLon,results)
        return results[0] / 1000 }

    override fun update(location: LatLng, address: String) { currentLat = location.latitude; currentLong = location.longitude }
}
