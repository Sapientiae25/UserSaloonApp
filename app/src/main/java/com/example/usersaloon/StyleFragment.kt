package com.example.usersaloon

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class StyleFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0
    private var month = 0
    private var year = 0
    private lateinit var max: String
    private lateinit var accountItem : AccountItem
    private lateinit var styleItem : StyleItem
    private lateinit var timeItem : TimeItem
    private lateinit var chosenDate: String
    private  var bookedTimes = mutableListOf<Pair<Int,Int>>()
    private lateinit var timeValue: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_style, container, false)
        styleItem = arguments?.getParcelable("styleItem")!!
        val userItem = (activity as DefaultActivity).userItem
        accountItem = styleItem.accountItem!!
        val tvDuration = rootView.findViewById<TextView>(R.id.tvDuration)
        val tvName = rootView.findViewById<TextView>(R.id.tvName)
        val tvInfo = rootView.findViewById<TextView>(R.id.tvInfo)
        val tvPrice = rootView.findViewById<TextView>(R.id.tvPrice)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvOpenHours = rootView.findViewById<TextView>(R.id.tvOpenHours)
        val llReviews = rootView.findViewById<LinearLayout>(R.id.llReviews)
        val styleRating = rootView.findViewById<RatingBar>(R.id.styleRating)
        val similarStyles = mutableListOf<StyleItem>()
        val rvMoreLike = rootView.findViewById<RecyclerView>(R.id.rvMoreLike)
        val btnBook = rootView.findViewById<AppCompatButton>(R.id.btnBook)
        val rvReviews = rootView.findViewById<RecyclerView>(R.id.rvReviews)
        val reviewList = mutableListOf<ReviewItem>()
        val cal: Calendar = Calendar.getInstance()
        val ivStyleImage = rootView.findViewById<ImageSlider>(R.id.ivStyleImage)
        val imageList = ArrayList<SlideModel>()
        val ivLike = rootView.findViewById<ImageView>(R.id.ivLike)
        val llMoreLikeThis = rootView.findViewById<LinearLayout>(R.id.llMoreLikeThis)
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        ivStyleImage.setImageList(imageList)
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        chosenDate = getString(R.string.datetime,year,month,day)
        rvReviews.adapter = ReviewAdapter(reviewList)
        rvReviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        timeItem = styleItem.time
        if (timeItem.maxTime.isNullOrEmpty()) { timeValue = timeItem.time; max = timeItem.time }
        else { timeValue = getString(R.string.time_distance, timeItem.time, timeItem.maxTime); max = timeItem.maxTime!! }
        tvDuration.text = getString(R.string.duration_time,timeValue)
        tvPrice.text = getString(R.string.money,styleItem.price)
        btnBook.text = getString(R.string.separate,"BOOK NOW",tvPrice.text)
        requireActivity().title = styleItem.accountItem?.name
        tvName.text = styleItem.name
        tvInfo.text = styleItem.info
        ivLike.setOnClickListener {
            if (styleItem.like){ styleItem.like = false
                ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_border_24))
            }else {styleItem.like = true
                ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_24)) }
            val url = getString(R.string.url,"like_style.php")
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener {},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["style_id"] = styleItem.id
                    params["user_id"] = userItem.id
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        var url = getString(R.string.url,"get_saloon_like.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                if (response == "true"){ styleItem.like = true
                    ivLike.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_24))}
                else { styleItem.like = false; ivLike.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_border_24))}},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                params["user_id"] = userItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"get_saloon.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val obj = JSONObject(response)
                val name = obj.getString("name")
                val accountId = obj.getString("account_id")
                val addressId = obj.getString("address_id")
                val address = obj.getString("address")
                val postcode = obj.getString("postcode")
                val rating = obj.getString("rating")
                val open = obj.getString("open")
                val close = obj.getString("close")
                tvOpenHours.text = getString(R.string.open_hours,getString(R.string.separate,open,close))
                tvAddress.text = getString(R.string.comma,address,postcode)
                val addressItem = AddressItem(addressId,"",postcode,"",address)
                val accountItem = AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating)
                styleItem.accountItem = accountItem },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        llReviews.setOnClickListener { rvReviews.visibility = if (rvReviews.visibility == View.GONE){View.VISIBLE} else {View.GONE} }
        url = getString(R.string.url,"get_reviews.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                var total = 0
                styleRating.rating = 0f
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val review = obj.getString("review")
                    val rating = obj.getInt("rating")
                    val reviewDate = obj.getString("date")
                    total += rating
                    reviewList.add(ReviewItem(review,rating,reviewDate)) }
                if (reviewList.size > 0) { val average = total / reviewList.size
                    styleRating.rating = average.toFloat()
                    rvReviews.adapter?.notifyItemRangeInserted(0, reviewList.size) }
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnBook.setOnClickListener{ val datePickerDialog = DatePickerDialog(requireContext(),this,year,month,day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis(); datePickerDialog.show()}
        rvMoreLike.adapter = SimilarAdapter(similarStyles)
        url = getString(R.string.url,"popular_styles.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"POP",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){llMoreLikeThis.visibility = View.GONE}
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
                    val accountItem = AccountItem(accountId,accountName)
                    val timeItem = TimeItem(time,maxTime)
                    similarStyles.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)) }
                rvMoreLike.adapter?.notifyItemRangeInserted(0,similarStyles.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { val params = HashMap<String, String>()
                params["gender"] = userItem.gender.toString()
                return params  }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"view_style.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_fk"] = userItem.id
                params["style_fk"] = styleItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        return rootView
    }

    override fun onDateSet(view: DatePicker?, newYear: Int, newMonth: Int, newDay: Int) {
        bookedTimes.clear()
        year=newYear;month=newMonth;day=newDay
        chosenDate = getString(R.string.datetime,newYear,(newMonth+1),newDay)
        val url = getString(R.string.url,"check_booking_day.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"DAY",response)
                val arr = JSONArray(response)
                for (i in 0 until arr.length()){
                    val obj = arr.getJSONObject(i)
                    val startHour = obj.getInt("s_hour") * 60
                    val startMinute = obj.getInt("s_min")
                    val endHour = obj.getInt("e_hour") * 60
                    val endMinute = obj.getInt("e_min")
                    val startTime = startHour + startMinute
                    val endTime = endHour + endMinute
                    bookedTimes.add(Pair(startTime,endTime)) } },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["date"] = chosenDate
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        showCustomDialog()
    }
    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        var hour = 0
        var minute = 0
        val minOptions = arrayOf("00","15","30","45")
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.time_picker_layout)
        val numPickerHour = dialog.findViewById<NumberPicker>(R.id.numPickerHour)
        val numPickerMins = dialog.findViewById<NumberPicker>(R.id.numPickerMins)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPickerHour.minValue = 0
        numPickerHour.maxValue  = 23
        numPickerMins.minValue = 0
        numPickerMins.maxValue = 3
        numPickerMins.displayedValues = minOptions
        numPickerHour.setOnValueChangedListener { numberPicker, _, _ ->  hour = numberPicker.value}
        numPickerMins.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value] ;minute = x.toInt() }
        close.setOnClickListener { dialog.dismiss(); val datePickerDialog = DatePickerDialog(requireContext(),this,year,month,day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis(); datePickerDialog.show() }
        save.setOnClickListener {
            var booked = false
            val currentMin = (hour * 60) + minute
            for (book in bookedTimes){
                if ((book.first .. book.second).contains(currentMin)){
                    Toast.makeText(context,"Time Already Booked",Toast.LENGTH_SHORT).show()
                    booked = true; break } }
            if (!booked){
                val chosenTime = getString(R.string.clock,hour,minute)
                val startDateTime = getString(R.string.make_datetime,chosenDate,chosenTime)
                val url = getString(R.string.url,"book.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        Log.println(Log.ASSERT,"BOOK",response)
                        if (response == "0"){ dialog.dismiss()
                            Toast.makeText(context,"Style Booked",Toast.LENGTH_SHORT).show()
                            (activity as DefaultActivity).addNotification()
                        }
                        else{Toast.makeText(context,"Invalid Time",Toast.LENGTH_SHORT).show()} },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["start"] = startDateTime
                        params["diff"] = getString(R.string.clock,max.toInt() / 60,max.toInt() % 60)
                        params["style_id"] = styleItem.id
                        params["user_id"] = (activity as DefaultActivity).userItem.id
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            } }
        dialog.show()
    }
}