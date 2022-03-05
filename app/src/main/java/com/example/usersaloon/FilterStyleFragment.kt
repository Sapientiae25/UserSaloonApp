package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class FilterStyleFragment : Fragment() {

    private lateinit var filterItem: FilterItem
    private var displayStyleList = mutableListOf<StyleItem>()
    private var styleItemList = mutableListOf<StyleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_category, container, false)
        filterItem = arguments?.getParcelable("filterItem")!!
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val filterObj = JSONObject()
        val length = JSONArray(filterItem.length)
        val gender = filterItem.gender
        val rvCategoryStyleItems = rootView.findViewById<RecyclerView>(R.id.rvCategoryStyleItems)
        val ivStoreFront = rootView.findViewById<ImageSlider>(R.id.ivStoreFront)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        ivStoreFront.setImageList(imageList)
        rvCategoryStyleItems.layoutManager = LinearLayoutManager(context)
        rvCategoryStyleItems.adapter = CategoryStyleAdapter(displayStyleList)
        filterObj.put("length",length)
        filterObj.put("gender",gender)
        val filterArr = JSONArray()
        filterArr.put(filterObj)
        Log.println(Log.ASSERT,"array",filterObj.toString())
        val url = getString(R.string.url,"filter_styles.php")
        val jsonRequest = JsonArrayRequest(
            Request.Method.POST, url,filterArr, { arr ->
                Log.println(Log.ASSERT,"Phil",arr.toString())
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE; ivStoreFront.visibility=View.VISIBLE}
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
                    val accountItem = AccountItem(accountFk,accountName,addressItem= AddressItem(address=address))
                    val timeItem = TimeItem(time,maxTime)
                    styleItemList.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem,rating=rating)) }
                displayStyleList.addAll(styleItemList)
                rvCategoryStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size) },
            { volleyError -> println(volleyError.message) })
        VolleySingleton.instance?.addToRequestQueue(jsonRequest)

        return rootView
    }

}
