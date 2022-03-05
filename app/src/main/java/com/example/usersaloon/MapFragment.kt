package com.example.usersaloon

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.GeoPoint
import org.json.JSONArray
import java.io.IOException

class MapFragment : Fragment(),OnMapReadyCallback,MoveMarker {

    private var currentLat = 0.toDouble()
    private var currentLong = 0.toDouble()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var location: LatLng
    private lateinit var mMap: GoogleMap
    private lateinit var saloonList: MutableList<AccountItem>
    private lateinit var rvSaloons: RecyclerView
    private lateinit var btnHome: FloatingActionButton
    private lateinit var svLocation: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_map, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as DefaultActivity)
        saloonList = mutableListOf()
        rvSaloons = rootView.findViewById(R.id.rvSaloons)
        btnHome = rootView.findViewById(R.id.btnHome)
        rvSaloons.layoutManager = LinearLayoutManager(context)
        svLocation = rootView.findViewById(R.id.svLocation)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        svLocation.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val chosenLocation = getLocationFromAddress(query)
                if (chosenLocation != null){
                    currentLat = chosenLocation.latitude
                    currentLong = chosenLocation.longitude
                    location = LatLng(currentLat, currentLong)
                    findSaloons() }
                else{
                    val dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.no_results_layout)
                    val close = dialog.findViewById<TextView>(R.id.close)
                    close.setOnClickListener { dialog.dismiss() }
                }
                return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true }})

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val chosenLocation = (activity as DefaultActivity).chosenLocation
        currentLat = chosenLocation.latitude; currentLong= chosenLocation.longitude
        btnHome.setOnClickListener{mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLat, currentLong),14f))}
        location = LatLng(currentLat, currentLong)
        findSaloons()}
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity as DefaultActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101)
            return }
        task.addOnSuccessListener {
            if (it != null){ currentLat = it.latitude; currentLong= it.longitude }else{currentLat= 51.5072; currentLong = -0.1276}
            btnHome.setOnClickListener{mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLat, currentLong),14f))}
            location = LatLng(currentLat, currentLong)
            findSaloons()} }
    private fun findSaloons(){
        val saloonList = mutableListOf<AccountItem>()
        mMap.addMarker(MarkerOptions().position(location).title("Here"))?.alpha = 1f
        val url = getString(R.string.url,"find_near_saloons.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"RES",response)
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val accountId = obj.getString("account_id")
                    val addressId = obj.getString("address_id")
                    val address = obj.getString("address")
                    val close = obj.getString("close")
                    val postcode = obj.getString("postcode")
                    val open = obj.getString("open")
                    val rating = obj.getString("rating")
                    val latitude = obj.getDouble("latitude")
                    val longitude = obj.getDouble("longitude")
                    val distance = obj.getString("distance")
                    val addressItem = AddressItem(addressId,"",postcode,"",address,latitude,longitude,distance)
                    saloonList.add(AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating))
                    val marker = mMap.addMarker(MarkerOptions().position(LatLng(latitude,longitude)).title(name))
                    marker?.tag = x
                }
                rvSaloons.adapter = MapSaloonAdapter(saloonList,this)
                rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["long"] = currentLat.toString()
                params["lat"] = currentLong.toString()
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f))
        mMap.setOnMarkerClickListener { mark ->
            Log.println(Log.ASSERT,"MA",mark.toString())
            if (mark.tag != null){
                rvSaloons.smoothScrollToPosition(mark.tag as Int)
                rvSaloons.findViewHolderForAdapterPosition(mark.tag as Int)?.itemView?.performClick()}
            true
        }
    }
    private fun getLocationFromAddress(strAddress: String?): GeoPoint? {
        val coder = Geocoder(context)
        val address: List<Address>?
        val p1: GeoPoint?
        try {
            address = coder.getFromLocationName(strAddress, 1)
            if (address == null) { return null }
            val location: Address = address[0]
            location.latitude
            location.longitude
            p1 = GeoPoint((location.latitude * 1E6), (location.longitude * 1E6))
            return p1
        } catch (e: IOException) { e.printStackTrace() }
        return null
    }
    override fun move(location: LatLng) {
        Log.println(Log.ASSERT,"MOVE",location.toString())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f))
    }
}