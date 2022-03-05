package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class DefaultActivity : AppCompatActivity(),UpdateLocation {

    lateinit var userItem: UserItem
    private lateinit var searchFragment: SearchFragment
    private lateinit var navController: NavController
    private lateinit var tvLocation: TextView
    private var notificationCount = 0
    private lateinit var notification:  ConstraintLayout
    private lateinit var cvCount: CardView
    private lateinit var tvCount: TextView
    lateinit var chosenLocation: AddressItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        userItem = intent.getParcelableExtra("userItem")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        notification = findViewById(R.id.notification)
        tvLocation = findViewById(R.id.tvLocation)
        cvCount = findViewById(R.id.cvCount)
        tvCount = findViewById(R.id.tvCount)
        tvLocation.setOnClickListener{
        val locationBottomSheet = LocationBottomSheet()
        locationBottomSheet.show(supportFragmentManager,"locationBottomSheet")}
        searchFragment = SearchFragment()
        bottomNavigationView.setOnItemSelectedListener { when (it.itemId){R.id.userFragment -> it.onNavDestinationSelected(navController)
            R.id.mapFragment -> it.onNavDestinationSelected(navController)
            R.id.settingFragment -> it.onNavDestinationSelected(navController)
            R.id.exploreFragment -> it.onNavDestinationSelected(navController)
        }
            true }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        notification.setOnClickListener { findNavController(R.id.activityFragment).navigate(R.id.action_global_bookingFragment) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu.findItem(R.id.searchFragment)
        if (item != null){
            val searchView = item.actionView as SearchView
            searchView.queryHint = "Search Styles"
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean { return true }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()){ searchFragment.emptyDb() }else{searchFragment.searchDb(newText) }
                    return true } }) }
        return true }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.searchFragment -> { item.onNavDestinationSelected(navController) }
//        R.id.bookingFragment -> { item.onNavDestinationSelected(navController) }
        else -> { super.onOptionsItemSelected(item) } }
    fun addNotification(){ if (notificationCount < 100){
            notificationCount += 1; cvCount.visibility = View.VISIBLE; tvCount.text = notificationCount.toString() } }
    fun clearNotification(){ cvCount.visibility = View.GONE; notificationCount = 0 }

    override fun onSupportNavigateUp(): Boolean { return navController.navigateUp() || super.onSupportNavigateUp() }
    override fun update(location: LatLng, address: String) { chosenLocation.latitude = location.latitude
        chosenLocation.longitude = location.longitude; tvLocation.text = address; Log.println(Log.ASSERT,"TOM","UPDATED") }
}