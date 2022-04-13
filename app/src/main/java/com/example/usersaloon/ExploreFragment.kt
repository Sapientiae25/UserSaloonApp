package com.example.usersaloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import java.util.*

class ExploreFragment : Fragment(){

    private lateinit var rvExplore: RecyclerView
    private lateinit var userItem: UserItem
    val styleList = mutableListOf<StyleItem>()
    val displayStyleList = mutableListOf<StyleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_explore, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Explore"
        userItem = (activity as DefaultActivity).userItem
        val svExplore = rootView.findViewById<SearchView>(R.id.svExplore)
        rvExplore = rootView.findViewById(R.id.rvExplore)
        rvExplore.layoutManager = GridLayoutManager(context,3)
        rvExplore.adapter = ExploreImageAdapter(displayStyleList)
        rvExplore.addItemDecoration(DividerItemDecoration(context,GridLayoutManager.HORIZONTAL))
        rvExplore.addItemDecoration(DividerItemDecoration(context,GridLayoutManager.VERTICAL))
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        svExplore.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()){
                    rvExplore.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (style in styleList) { if (style.name.lowercase(Locale.getDefault()).contains(search))
                    { displayStyleList.add(style) } }
                    rvExplore.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                    if (displayStyleList.size == 0){rvExplore.visibility = View.VISIBLE}
                }else{
                    rvExplore.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    displayStyleList.addAll(styleList)
                    rvExplore.adapter?.notifyItemRangeInserted(0,displayStyleList.size) }
                return true } })
        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun loadData(){
        val url = getString(R.string.url,"get_explore_images.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val accountId = obj.getString("account_id")
                    val accountName = obj.getString("account_name")
                    val imageId = obj.getString("image_id")
                    val accountItem = AccountItem(accountId,accountName)
                    styleList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId))
                    displayStyleList.addAll(styleList)}
                rvExplore.adapter?.notifyItemRangeInserted(0,displayStyleList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["gender"] = userItem.gender.toString()
                return params  }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
