package com.example.usersaloon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val res = arrayListOf("buzz cut", "bowl cut", "fade", "high top", "low fade", "taper fade", "buzz", "braids",
//            "dreads","curly","afro","mini afro", "quiff","perm","mohawk","bald","low cut","high fade","waves","cornrows",
//        "bob cut","curly bangs","wavy","curly hair","permed","The Mohawk","The buzz cut","bow cut","Long Cornrows",
//        "Short Bradis","Curly Afro","Low Fade","Long bradis","The bald")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController


    }
}