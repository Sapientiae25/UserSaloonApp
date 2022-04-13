package com.example.usersaloon

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private val filters = mutableListOf<Pair<String,String>>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var updateLocation: UpdateLocation
    private lateinit var rvSaloons: RecyclerView
    private lateinit var rvPopular: RecyclerView
    private lateinit var userItem: UserItem
    private lateinit var rvFavouriteSaloons: RecyclerView
    private lateinit var rvFavouriteStyles: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvRecent: RecyclerView
    private lateinit var llFavouriteStyles: LinearLayout
    private lateinit var llRecent: LinearLayout
    private lateinit var llFavouriteSaloons: LinearLayout
    val recentList = mutableListOf<StyleItem>()
    val popularList = mutableListOf<StyleItem>()
    val saloonList = mutableListOf<AccountItem>()
    val likedSaloonList = mutableListOf<AccountItem>()
    val likedStyleList = mutableListOf<StyleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_user, container, false)
        userItem = (activity as DefaultActivity).userItem
        (activity as DefaultActivity).supportActionBar?.title = "Sapientiae"
        rvPopular = rootView.findViewById(R.id.rvPopular)
        rvSaloons = rootView.findViewById(R.id.rvSaloons)
        rvCategories = rootView.findViewById(R.id.rvCategories)
        rvRecent = rootView.findViewById(R.id.rvRecent)
        rvFavouriteStyles = rootView.findViewById(R.id.rvFavouriteStyles)
        rvFavouriteSaloons = rootView.findViewById(R.id.rvFavouriteSaloons)
        llFavouriteStyles = rootView.findViewById(R.id.llFavouriteStyles)
        llFavouriteSaloons = rootView.findViewById(R.id.llFavouriteSaloons)
        llRecent = rootView.findViewById(R.id.llRecent)

        rvPopular.adapter = PopularAdapter(popularList)
        rvPopular.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvFavouriteStyles.adapter = PopularAdapter(likedStyleList)
        rvFavouriteStyles.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvSaloons.adapter = SaloonAdapter(saloonList)
        rvSaloons.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        rvFavouriteSaloons.adapter = SaloonAdapter(likedSaloonList)
        rvFavouriteSaloons.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        rvCategories.adapter = CategoryAdapter(filters)
        rvCategories.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        rvRecent.adapter = PopularAdapter(recentList)
        rvRecent.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        updateLocation = activity as DefaultActivity
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as DefaultActivity)
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(activity as DefaultActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101)
            return }
        task.addOnSuccessListener {
            if (it != null){
                currentLat = it.latitude
                currentLong= it.longitude
                updateLocation.update(LatLng(it.latitude,it.longitude), "Current Location") } } }
    private fun getDistance(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat,startLon,endLat,endLon,results)
        return results[0] / 1000 }

    override fun update(location: LatLng, address: String) { currentLat = location.latitude; currentLong = location.longitude }
    private fun loadData() {
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
                    val imageId = obj.getString("image_id")
                    popularList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId)) }
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
                    val imageId = obj.getString("image_id")
                    var distance: String? = null
                    if (currentLat != null){distance = String.format("%.2f",getDistance(currentLat!!,
                        currentLong!!,latitude,longitude))}
                    val addressItem = AddressItem(addressId,"",postcode,"",address,latitude,longitude,distance)
                    saloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating,
                        imageId=imageId)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { return HashMap() }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_liked_saloons.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                llFavouriteSaloons.visibility = if (arr.length() == 0) View.GONE else View.VISIBLE
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
                    val imageId = obj.getString("image_id")
                    var distance: String? = null
                    if (currentLat != null){distance = String.format("%.2f",getDistance(currentLat!!,
                        currentLong!!,latitude,longitude))}
                    val addressItem = AddressItem(addressId,"",postcode,"",address,latitude,longitude,distance)
                    likedSaloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating,
                        imageId=imageId)) }
                rvFavouriteSaloons.adapter?.notifyItemRangeInserted(0,likedSaloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_liked_styles.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                llFavouriteStyles.visibility = if (arr.length() == 0) View.GONE else View.VISIBLE
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
                    val imageId = obj.getString("image_id")
                    likedStyleList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId)) }
                rvFavouriteStyles.adapter?.notifyItemRangeInserted(0,likedStyleList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_recently_viewed.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                llRecent.visibility = if (arr.length() == 0) View.GONE else View.VISIBLE
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
                    val imageId = obj.getString("image_id")
                    recentList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId)) }
                rvRecent.adapter?.notifyItemRangeInserted(0,recentList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["user_fk"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        for (i in gender.indices){
            url = getString(R.string.url,"get_gender_images.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    filters.add(Pair(gender[i],response))
                    rvCategories.adapter?.notifyItemInserted(filters.size)
                    if (i == gender.size-1){
                        for (x in length.indices){
                            url = getString(R.string.url,"get_length_images.php")
                            stringRequest = object : StringRequest(
                                Method.POST, url, Response.Listener { response ->
                                    filters.add(Pair(length[x],response))
                                    rvCategories.adapter?.notifyItemInserted(filters.size)},
                                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String> {
                                    val params = java.util.HashMap<String, String>()
                                    params["length"] = x.toString()
                                    return params }}
                            VolleySingleton.instance?.addToRequestQueue(stringRequest)} } },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["gender"] = i.toString()
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)}
    }}
