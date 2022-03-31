package com.example.usersaloon

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class DefaultActivity : AppCompatActivity(),UpdateLocation,CloseSearch {

    lateinit var userItem: UserItem
    lateinit var navController: NavController
    private lateinit var tvLocation: TextView
    private var notificationCount = 0
    private lateinit var notification:  ConstraintLayout
    private lateinit var cvCount: CardView
    private lateinit var tvCount: TextView
    lateinit var chosenLocation: AddressItem
    private lateinit var mapFragment: MenuItem
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        userItem = intent.getParcelableExtra("userItem")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mapFragment = bottomNavigationView.menu.findItem(R.id.mapFragment)
        setSupportActionBar(toolbar)
        notification = findViewById(R.id.notification)
        tvLocation = findViewById(R.id.tvLocation)
        cvCount = findViewById(R.id.cvCount)
        tvCount = findViewById(R.id.tvCount)
        tvLocation.setOnClickListener{
        val locationBottomSheet = LocationBottomSheet()
        locationBottomSheet.show(supportFragmentManager,"locationBottomSheet")}
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        notification.setOnClickListener { findNavController(R.id.activityFragment).navigate(R.id.action_global_bookingFragment) }
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.mapFragment,R.id.userFragment,R.id.settingFragment,R.id.exploreFragment))
        mapFragment.setOnMenuItemClickListener{goToMap(); true}
        NavigationUI.setupWithNavController(bottomNavigationView,navController)
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu.findItem(R.id.searchFragment)
        if (item != null){
            searchView = item.actionView as SearchView
            searchView.queryHint = "Search Styles"
        }
        return true }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.searchFragment -> {item.onNavDestinationSelected(navController) }
        android.R.id.home -> {
            val queue = navController.backQueue
            val index = if (queue.size > 1) queue.size-2 else 0
            if (queue[index].destination.id == R.id.searchFragment){ navController.popBackStack(); navController.popBackStack() }
            else{ super.onOptionsItemSelected(item)} }
        else -> { super.onOptionsItemSelected(item) } }
    fun addNotification(){ if (notificationCount < 100){
        notificationCount += 1; cvCount.visibility = View.VISIBLE; tvCount.text = notificationCount.toString() } }
    fun clearNotification(){ cvCount.visibility = View.GONE; notificationCount = 0 }
    fun goToMap(bundle: Bundle? = null){mapFragment.isChecked = true
        findNavController(R.id.activityFragment).navigate(R.id.action_global_mapFragment,bundle) }
    override fun onSupportNavigateUp(): Boolean { return navController.navigateUp() || super.onSupportNavigateUp() }
    override fun update(location: LatLng, address: String) { chosenLocation.latitude = location.latitude
        chosenLocation.longitude = location.longitude; tvLocation.text = address }

    override fun closeSearch() {
        searchView.isIconified = true
        searchView.onActionViewCollapsed() }
}