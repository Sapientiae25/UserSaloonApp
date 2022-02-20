package com.example.usersaloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray

class PaymentFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_payment, container, false)
        requireActivity().title = "Payment Methods"
        val rvCards = rootView.findViewById<RecyclerView>(R.id.rvAddress)
        val tvAddCard = rootView.findViewById<TextView>(R.id.tvAddCard)
        val cardList = mutableListOf<CardItem>()
        rvCards.layoutManager = LinearLayoutManager(context)
        rvCards.adapter = CardAdapter(cardList)
        tvAddCard.setOnClickListener {  view ->
            view.findNavController().navigate(R.id.action_paymentFragment_to_cardFragment) }

        return rootView
    }
}
