package com.example.usersaloon

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs

class FilterStyleFragment : Fragment() {

    private lateinit var filterItem: FilterItem
    private var displayStyleList = mutableListOf<StyleItem>()
    private var styleItemList = mutableListOf<StyleItem>()
    private lateinit var vpImages: ViewPager2
    private lateinit var imageUrls: MutableList<Pair<String,String>>
    private lateinit var tvNoStyles: TextView
    private lateinit var rvCategoryStyleItems: RecyclerView
    private val filterArr = JSONArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_category, container, false)
        filterItem = arguments?.getParcelable("filterItem")!!
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        val filterObj = JSONObject()
        val length = JSONArray(filterItem.length)
        val gender = filterItem.gender
        rvCategoryStyleItems = rootView.findViewById(R.id.rvCategoryStyleItems)
        rvCategoryStyleItems.layoutManager = LinearLayoutManager(context)
        rvCategoryStyleItems.adapter = CategoryStyleAdapter(displayStyleList)
        rvCategoryStyleItems.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        filterObj.put("length",length)
        filterObj.put("gender",gender)
        filterArr.put(filterObj)

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val adapter = ClickStyleImageAdapter(imageUrls)
        vpImages.adapter = adapter
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
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }

        return rootView }
    private fun loadData(){
        val url = getString(R.string.url,"filter_styles.php")
        val jsonRequest = JsonArrayRequest(
            Request.Method.POST, url,filterArr, { arr ->
                Log.println(Log.ASSERT,"Phil",arr.toString())
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val accountFk = obj.getString("account_fk")
                    val address = obj.getString("address")
                    val accountName = obj.getString("account_name")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val imageId = obj.getString("image_id")
                    if (imageId.isNotEmpty() && imageUrls.size < 6) imageUrls.add(Pair(imageId,styleId))
                    val accountItem = AccountItem(accountFk,accountName,addressItem= AddressItem(address=address))
                    styleItemList.add(StyleItem(name,price,time,info,styleId,accountItem=accountItem,rating=rating,imageId=imageId)) }
                if (imageUrls.size == 0) vpImages.visibility = View.GONE
                vpImages.adapter?.notifyItemRangeInserted(1,imageUrls.size)
                displayStyleList.addAll(styleItemList)
                rvCategoryStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size) },
            { volleyError -> println(volleyError.message) })
        VolleySingleton.instance?.addToRequestQueue(jsonRequest)
    }
}
