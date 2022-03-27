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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import kotlin.math.abs

class CategoryFragment : Fragment(){

    lateinit var categoryItem: CategoryItem
    private lateinit var vpImages: ViewPager2
    private lateinit var imageUrls: MutableList<Pair<String,String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_category, container, false)
        categoryItem = arguments?.getParcelable("categoryItem")!!
        (activity as DefaultActivity).supportActionBar?.title = categoryItem.category
        val styleItemList = mutableListOf<StyleItem>()
        val rvCategoryStyleItems = rootView.findViewById<RecyclerView>(R.id.rvCategoryStyleItems)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        rvCategoryStyleItems.adapter = CategoryItemAdapter(styleItemList)
        rvCategoryStyleItems.layoutManager = LinearLayoutManager(context)
        val url = getString(R.string.url,"get_category_styles.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
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
                    val timeItem = TimeItem(time,maxTime)
                    val imageId = obj.getString("image_id")
                    styleItemList.add(StyleItem(name,price,timeItem,info,id=styleId,rating=rating,imageId=imageId)) }
                rvCategoryStyleItems.adapter?.notifyItemRangeInserted(0,styleItemList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["category_id"] = categoryItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
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

        loadImages()

        return rootView }
    private fun loadImages(){
        val url = getString(R.string.url,"get_category_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Log.println(Log.ASSERT,"CATEG",response)
            val arr = JSONArray(response)
            if (arr.length() == 0) {vpImages.visibility = View.GONE}
            for (i in 0 until arr.length()){
                val obj = arr.getJSONObject(i)
                val styleId = obj.getString("style_id")
                val imageId = obj.getString("image_id")
                imageUrls.add(Pair(styleId,imageId))}
            if (imageUrls.size == 0) vpImages.visibility = View.GONE
            vpImages.adapter?.notifyItemRangeInserted(1,imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = java.util.HashMap<String, String>()
            params["category_id"] = categoryItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
