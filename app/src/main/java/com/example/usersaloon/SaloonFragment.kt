package com.example.usersaloon

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.abs

class SaloonFragment : Fragment() {

    var displayStyleList = mutableListOf<StyleItem>()
    var styleItemList = mutableListOf<StyleItem>()
    lateinit var rvStyleItems: RecyclerView
    lateinit var tvNoStyles: TextView
    lateinit var accountItem: AccountItem
    private var back = 0
    private lateinit var vpImages: ViewPager2
    private val imageUrls = mutableListOf<String>()
    lateinit var userItem: UserItem
    lateinit var rvStyleCategories: RecyclerView
    val categoryList = mutableListOf<CategoryItem>()
    lateinit var ivLike: ImageView
    lateinit var cgCategories: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_saloon, container, false)
        userItem = (activity as DefaultActivity).userItem
        accountItem = arguments?.getParcelable("accountItem")!!
        val addressItem = accountItem.addressItem
        (activity as DefaultActivity).supportActionBar?.title = accountItem.name
        back = arguments?.getInt("back")!!
        rvStyleItems = rootView.findViewById(R.id.rvStyleItems)
        rvStyleCategories = rootView.findViewById(R.id.rvStyleCategories)
        cgCategories = rootView.findViewById(R.id.cgCategories)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvOpen = rootView.findViewById<TextView>(R.id.tvOpen)
        val tvRating = rootView.findViewById<TextView>(R.id.tvRating)
        val tvMap = rootView.findViewById<TextView>(R.id.tvMap)
        val svStyle = rootView.findViewById<SearchView>(R.id.svStyle)
        ivLike = rootView.findViewById(R.id.ivLike)
        rvStyleCategories.adapter = StyleCategoryAdapter(categoryList,accountItem)
        rvStyleCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvStyleItems.layoutManager = LinearLayoutManager(context)
        rvStyleItems.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        val btnFilter = rootView.findViewById<FloatingActionButton>(R.id.btnFilter)
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        tvRating.text = accountItem.rating
        tvAddress.text = getString(R.string.comma,addressItem?.address,addressItem?.postcode)
        tvOpen.text = getString(R.string.separate,accountItem.open,accountItem.close)
        btnFilter.setOnClickListener { view ->
            val bundle = bundleOf(Pair("accountItem",accountItem))
            view.findNavController().navigate(R.id.action_saloonFragment_to_filterFragment,bundle) }
        ivLike.setOnClickListener {
            if (accountItem.like){ accountItem.like = false
                ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_border_24))
            }else {accountItem.like = true
                ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_24)) }
            val url = getString(R.string.url,"like_saloon.php")
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener {},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    params["user_id"] = userItem.id
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }


        svStyle.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()){
                    rvStyleItems.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (style in styleItemList) { if (style.name.lowercase(Locale.getDefault()).contains(search))
                    { displayStyleList.add(style) } }
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                    if (displayStyleList.size == 0){tvNoStyles.visibility = View.VISIBLE}
                }else{
                    rvStyleItems.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                }
                return true } })
        tvMap.setOnClickListener {
            val bundle = bundleOf(Pair("addressItem",addressItem))
            (activity as DefaultActivity).goToMap(bundle) }

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        vpImages.adapter = SaloonImageAdapter(imageUrls)
        vpImages.clipChildren = false
        vpImages.clipToPadding = false
        vpImages.offscreenPageLimit = 3
        vpImages.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position -> val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f }

        vpImages.setPageTransformer(compositePageTransformer)
        val sliderRunnable = Runnable {vpImages.currentItem = if(vpImages.currentItem+1 == imageUrls.size) 0
        else vpImages.currentItem+1}

        TabLayoutMediator(tabLayout,vpImages) { _, _ -> }.attach()

        vpImages.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000) } })

        loadData()
        loadImages()
        swipeRefresh.setOnRefreshListener { loadData();loadImages();swipeRefresh.isRefreshing = false }

        return rootView
    }
    private fun loadImages(){
        vpImages.adapter?.notifyItemRangeRemoved(0,imageUrls.size)
        imageUrls.clear()
        val url = getString(R.string.url,"get_saloon_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            if (arr.length() == 0) vpImages.visibility = View.GONE
            for (i in 0 until arr.length()){
                val imageId = arr.getString(i)
                imageUrls.add(imageId)}
            vpImages.adapter?.notifyItemRangeInserted(0,imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadData(){
        rvStyleItems.adapter?.notifyItemRangeRemoved(0,styleItemList.size)
        rvStyleCategories.adapter?.notifyItemRangeRemoved(0,categoryList.size)
        styleItemList.clear()
        categoryList.clear()

        var url = getString(R.string.url,"get_saloon_like.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                if (response == "true"){ accountItem.like = true
                    ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_24))}
                else { accountItem.like = false; ivLike.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_border_24))}},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_categories.php")
        stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            for (x in 0 until arr.length()){
                val obj = arr.getJSONObject(x)
                val category = obj.getString("category")
                val categoryId = obj.getString("id")
                val imageId = obj.getString("image_id")
                val categoryItem = CategoryItem(categoryId,category,accountItem,imageId)
                categoryList.add(categoryItem)
                val btn = Chip(context)
                btn.text = category
                val params = ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT,ChipGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(4,0,4,0)
                btn.layoutParams = params
                btn.setOnClickListener{ view ->
                    val bundle = bundleOf(Pair("categoryItem",categoryList[x]),Pair("accountItem",accountItem))
                    view.findNavController().navigate(R.id.action_saloonFragment_to_categoryFragment,bundle) }
                cgCategories.addView(btn) }
            rvStyleCategories.adapter?.notifyItemRangeInserted(0,categoryList.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        if (back == 0){
            url = getString(R.string.url,"get_style.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val arr = JSONArray(response)
                    if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                    for (x in 0 until arr.length()){
                        val obj = arr.getJSONObject(x)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val rating = obj.getString("rating").toFloatOrNull()
                        val imageId = obj.getString("image_id")
                        styleItemList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId)) }
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList)},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)}
        else if (back == 1){
            val filterItem = accountItem.filterItem
            val filterObj = JSONObject()
            val length = JSONArray(filterItem.length)
            val gender = JSONArray(filterItem.gender)
            filterObj.put("length",length)
            filterObj.put("gender",gender)
            filterObj.put("account_id",accountItem.id)
            val filterArr = JSONArray()
            filterArr.put(filterObj)
            url = getString(R.string.url,"filter_account.php")
            val jsonRequest = JsonArrayRequest(
                Request.Method.POST, url,filterArr, { arr ->
                    if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                    for (x in 0 until arr.length()){
                        val obj = arr.getJSONObject(x)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val imageId = obj.getString("image_id")
                        styleItemList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,imageId=imageId)) }
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList) },
                { volleyError -> println(volleyError.message) })
            VolleySingleton.instance?.addToRequestQueue(jsonRequest) }
    }
}