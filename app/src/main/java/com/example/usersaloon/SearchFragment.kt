package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject


class SearchFragment : Fragment() {

    private lateinit var rvStyles: RecyclerView
    private lateinit var rvSaloons: RecyclerView
    private lateinit var tvNoStyles: TextView
    private lateinit var tvAll: TextView
    private lateinit var llStyles: LinearLayout
    private lateinit var llSaloons: LinearLayout
    private var displayStyleList = mutableListOf<String>()
    private var displaySaloonList = mutableListOf<AccountItem>()
    private lateinit var rootView: View
    private lateinit var tvSaloons: TextView
    private lateinit var tvStyles: TextView
    var currentText = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =  inflater.inflate(R.layout.fragment_filter_style, container, false)
        (activity as DefaultActivity).supportActionBar?.title = ""
        tvAll = rootView.findViewById(R.id.tvAll)
        llStyles = rootView.findViewById(R.id.llStyles)
        llSaloons = rootView.findViewById(R.id.llSaloons)
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        tvStyles = rootView.findViewById(R.id.tvStyles)
        tvSaloons = rootView.findViewById(R.id.tvSaloons)
        rvSaloons = rootView.findViewById(R.id.rvSaloons)
        rvSaloons.layoutManager = LinearLayoutManager(context)
        rvSaloons.adapter = SaloonSearchAdapter(displaySaloonList)
        rvStyles = rootView.findViewById(R.id.rvStyles)
        rvStyles.adapter = TextAdapter(displayStyleList,activity as DefaultActivity)
        rvStyles.layoutManager = LinearLayoutManager(context)
        val searchView = (activity as DefaultActivity).searchView

        empty()

        tvSaloons.setOnClickListener {
            val url = getString(R.string.url,"search_saloons.php")
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val arr = JSONArray(response)
                    val count = displaySaloonList.size
                    displaySaloonList.clear()
                    rvStyles.adapter?.notifyItemRangeRemoved(0,count)
                    for (i in 0 until arr.length()){
                        val saloon = arr.getJSONObject(i)
                        val name = saloon.getString("name")
                        val id = saloon.getString("id")
                        displaySaloonList.add(AccountItem(id,name)) }
                    rvStyles.adapter?.notifyItemRangeInserted(0,displaySaloonList.size) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["text"] = currentText
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
        tvStyles.setOnClickListener {
            val url = getString(R.string.url,"search_styles.php")
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val arr = JSONArray(response)
                    val count = displayStyleList.size
                    displayStyleList.clear()
                    rvStyles.adapter?.notifyItemRangeRemoved(0,count)
                    for (i in 0 until arr.length()){ displayStyleList.add(arr.getString(i)) }
                    rvStyles.adapter?.notifyItemRangeInserted(0,displayStyleList.size) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["text"] = currentText
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()){currentText = ""; empty()}else{ currentText = newText;search(newText)}
                return true } })

        return rootView }

    private fun search(text: String){
        tvAll.visibility = View.VISIBLE
        val saloonCount = displaySaloonList.size
        displaySaloonList.clear()
        val count = displayStyleList.size
        displayStyleList.clear()
        var url = getString(R.string.url,"filter_search.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val obj = JSONObject(response)
                val saloons = obj.getJSONArray("saloons")
                val styles = obj.getJSONArray("styles")
                llStyles.visibility = if (styles.length() == 0) View.GONE else View.VISIBLE
                llSaloons.visibility = if (saloons.length() == 0) View.GONE else View.VISIBLE
                if (styles.length() == 0 && saloons.length() == 0) { tvNoStyles.visibility = View.VISIBLE; tvAll.visibility = View.GONE
                } else { tvNoStyles.visibility = View.GONE;tvAll.visibility = View.VISIBLE }
                rvStyles.adapter?.notifyItemRangeRemoved(0,count)
                for (i in 0 until styles.length()){
                    val style = styles.getString(i)
                    displayStyleList.add(style) }
                rvStyles.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                rvSaloons.adapter?.notifyItemRangeRemoved(0,saloonCount)
                for (i in 0 until saloons.length()){
                    val saloon = saloons.getJSONObject(i)
                    val name = saloon.getString("name")
                    val id = saloon.getString("id")
                    displaySaloonList.add(AccountItem(id,name)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,displaySaloonList.size)
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["text"] = text
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        tvAll.setOnClickListener { view ->
            val styleList = mutableListOf<StyleItem>()
            url = getString(R.string.url,"filter_word_search.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val arr = JSONArray(response)
                    for (i in 0 until arr.length()){
                        val obj = arr.getJSONObject(i)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val rating = obj.getString("rating").toFloatOrNull()
                        val accountId = obj.getString("account_fk")
                        val accountName = obj.getString("account_name")
                        val accountItem = AccountItem(accountId,accountName)
                        val imageId = obj.getString("image_id")
                        styleList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId))}
                    val bundle = bundleOf(Pair("styleItem",styleList))
                    view.findNavController().navigate(R.id.action_searchFragment_to_resultFragment,bundle) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["text"] = text
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        tvAll.text = getString(R.string.see_all_results_for,text)
    }
    private fun empty(){
        tvAll.visibility = View.GONE
        val saloonCount = displaySaloonList.size
        displaySaloonList.clear()
        val count = displayStyleList.size
        displayStyleList.clear()
        val url = getString(R.string.url,"popular_searches.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val obj = JSONObject(response)
                val saloons = obj.getJSONArray("saloons")
                val styles = obj.getJSONArray("styles")
                llStyles.visibility = if (styles.length() == 0) View.GONE else View.VISIBLE
                llSaloons.visibility = if (saloons.length() == 0) View.GONE else View.VISIBLE
                if (styles.length() == 0 && saloons.length() == 0) { tvNoStyles.visibility = View.VISIBLE
                } else { tvNoStyles.visibility = View.GONE}
                rvStyles.adapter?.notifyItemRangeRemoved(0,count)
                for (i in 0 until styles.length()){
                    val style = styles.getString(i)
                    displayStyleList.add(style) }
                rvStyles.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                rvSaloons.adapter?.notifyItemRangeRemoved(0,saloonCount)
                for (i in 0 until saloons.length()){
                    val saloon = saloons.getJSONObject(i)
                    val name = saloon.getString("name")
                    val id = saloon.getString("id")
                    displaySaloonList.add(AccountItem(id,name)) }
                rvSaloons.adapter?.notifyItemRangeInserted(0,displaySaloonList.size)
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { return HashMap() }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
