package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
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
import org.json.JSONObject

class ReviewFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_review, container, false)
        val userItem = (activity as DefaultActivity).userItem
        val styleItem = arguments?.getParcelable<StyleItem>("styleItem")!!
        requireActivity().title = "Review"
        val etReview = rootView.findViewById<TextView>(R.id.etReview)
        val rating = rootView.findViewById<RatingBar>(R.id.rating)
        val btnSubmit = rootView.findViewById<AppCompatButton>(R.id.btnSubmit)
        var empty = true
        var url = getString(R.string.url,"check_review.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"REV",response)
                if (response.isNotEmpty()){
                    val obj = JSONObject(response)
                    etReview.text = obj.getString("review")
                    rating.rating = obj.getString("rating").toFloat()
                    empty = false }
            }, Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnSubmit.setOnClickListener { view ->
            if (etReview.text.isNotEmpty()){
            url = if (empty) getString(R.string.url,"review.php") else getString(R.string.url,"update_review.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { }, Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["review"] = etReview.text.toString()
                    params["rating"] = rating.rating.toString()
                    params["user_id"] = userItem.id
                    params["style_id"] = styleItem.id
                    return params
                }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                Toast.makeText(context,"Review Submitted",Toast.LENGTH_SHORT).show()
                view.findNavController().popBackStack()
        } else{etReview.error = "Please Fill This Area"}}
        return rootView
    }
}
