package com.example.usersaloon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class SettingFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_setting, container, false)
        requireActivity().title = "Account"
        val tvBooked = rootView.findViewById<TextView>(R.id.tvBooked)
        val tvFavouriteStyles = rootView.findViewById<TextView>(R.id.tvFavouriteStyles)
        val tvFavouriteSaloons = rootView.findViewById<TextView>(R.id.tvFavouriteSaloons)
        val tvDetails = rootView.findViewById<TextView>(R.id.tvDetails)
        val tvPayment = rootView.findViewById<TextView>(R.id.tvPayment)
        val tvLogOut = rootView.findViewById<TextView>(R.id.tvLogOut)

        tvBooked.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_oldBookingFragment) }
        tvFavouriteStyles.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_settingFragment_to_favouriteStylesFragment) }
        tvFavouriteSaloons.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_settingFragment_to_favouriteSaloonsFragment) }
        tvDetails.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_detailsFragment) }
        tvPayment.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_paymentFragment) }
        tvLogOut.setOnClickListener { val intent = Intent(context, MainActivity::class.java); startActivity(intent) }

        return rootView
    } }
