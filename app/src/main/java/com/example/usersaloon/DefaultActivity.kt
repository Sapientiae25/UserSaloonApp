package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class DefaultActivity : AppCompatActivity(),UpdateLocation {

    lateinit var userItem: UserItem
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var searchFragment: SearchFragment
    private lateinit var navController: NavController
    private lateinit var tvLocation: TextView
//    private lateinit var ivBooking: ImageView
//    private lateinit var ivSearch: ImageView
    lateinit var chosenLocation: AddressItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        userItem = intent.getParcelableExtra("userItem")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        tvLocation = findViewById(R.id.tvLocation)
        tvLocation.setOnClickListener{
        val locationBottomSheet = LocationBottomSheet()
        locationBottomSheet.show(supportFragmentManager,"locationBottomSheet")}

        fragmentContainer = findViewById(R.id.activityFragment)
        searchFragment = SearchFragment()
        bottomNavigationView.setOnItemSelectedListener { when (it.itemId){R.id.userFragment -> it.onNavDestinationSelected(navController)
            R.id.mapFragment -> it.onNavDestinationSelected(navController)
            R.id.settingFragment -> it.onNavDestinationSelected(navController)
        }
            true }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu.findItem(R.id.miSearch)
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
        R.id.bookingFragment -> { item.onNavDestinationSelected(navController) }
        else -> { super.onOptionsItemSelected(item) } }

    override fun onSupportNavigateUp(): Boolean { return navController.navigateUp() || super.onSupportNavigateUp() }
    override fun update(location: LatLng, address: String) { chosenLocation.latitude = location.latitude
        chosenLocation.longitude = location.longitude; tvLocation.text = address; Log.println(Log.ASSERT,"TOM","UPDATED") }
}