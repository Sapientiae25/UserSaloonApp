package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import java.util.HashMap

class PaymentFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_payment, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Payment Methods"
        val rvCards = rootView.findViewById<RecyclerView>(R.id.rvCards)
        val btnAddCard = rootView.findViewById<AppCompatButton>(R.id.btnAddCard)
        val cardList = mutableListOf<CardItem>()
        rvCards.layoutManager = LinearLayoutManager(context)
        rvCards.adapter = CardAdapter(cardList)
        val url = getString(R.string.url,"get_cards.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val cardNum = obj.getString("card_number")
                    val cvv = obj.getString("cvv")
                    val expiry = obj.getString("expiry")
                    val cardId = obj.getString("card_id")
                    cardList.add(CardItem(cardId, cardNum, expiry, cvv)) }
                rvCards.adapter?.notifyItemRangeInserted(0,cardList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_fk"] = (activity as DefaultActivity).userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnAddCard.setOnClickListener {  view ->
            view.findNavController().navigate(R.id.action_paymentFragment_to_cardFragment) }

        return rootView
    }
}
