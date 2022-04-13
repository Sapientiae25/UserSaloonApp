package com.example.usersaloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.slider.RangeSlider
import org.json.JSONObject
import java.util.*

class AppointmentFragment : Fragment(){

    private lateinit var rvDays: RecyclerView
    private lateinit var rvBook: RecyclerView
    private lateinit var rsTime: RangeSlider
    private lateinit var tvDate: TextView
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var ivCalendar: ImageView
    private lateinit var rbAll: RadioButton
    private lateinit var rbFilter: RadioButton
    private lateinit var styleItem: StyleItem
    private lateinit var accountItem: AccountItem
    private lateinit var dayList: MutableList<Triple<Int,Int,Int>>
    var appointmentList = mutableListOf<AppointmentItem>()
    private var calendar = Calendar.getInstance()
    private var year = 0
    private var month = 0
    var day = 0
    var date = ""
    var startHour = 0
//    var startMinute = 0
    var endHour = 0
//    var endMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_address, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Book Style"
        date = getString(R.string.user_date,day,month,year)
        styleItem = arguments?.getParcelable("styleItem")!!
        accountItem = styleItem.accountItem!!
        rvDays = rootView.findViewById(R.id.rvDays)
        rvBook = rootView.findViewById(R.id.rvBook)
        rsTime = rootView.findViewById(R.id.rsTime)
        tvDate = rootView.findViewById(R.id.tvDate)
        ivCalendar = rootView.findViewById(R.id.ivCalendar)
        rbAll = rootView.findViewById(R.id.rbAll)
        rbFilter = rootView.findViewById(R.id.rbFilter)
        tvStart = rootView.findViewById(R.id.tvStart)
        tvEnd = rootView.findViewById(R.id.tvEnd)

        tvDate.text = date

        rvDays.adapter = DayAdapter(dayList){ calendar.set(it.third,it.second,it.first)
        if (it.third == dayList[6].first) makeCalendar()}
        rvDays.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        rvBook.adapter = AppointmentAdapter(appointmentList,styleItem)
        rvBook.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        rsTime.addOnChangeListener { _, _, _ ->
            val values = rsTime.values
            startHour = values[0].toInt()
            endHour = values[1].toInt()
            tvStart.text = startHour.toString()
            tvEnd.text = endHour.toString()
        }



        loadData()
        return rootView
    }

    private fun loadData(){
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        makeCalendar()

        val url = getString(R.string.url,"get_booking_info.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val obj = JSONObject(response)
                startHour = obj.getInt("s_hour")
//                startMinute = obj.getInt("s_min")
                endHour = obj.getInt("e_hour")
//                endMinute = obj.getInt("e_min")
//                val start = getString(R.string.time_colon,startHour.toString(),startMinute.toString())
//                val end = getString(R.string.time_colon,endHour.toString(),endMinute.toString())
                tvStart.text = startHour.toString()
                tvEnd.text = endHour.toString()

                rsTime.valueFrom = startHour.toFloat()
                rsTime.valueTo = endHour.toFloat()
                rsTime.setMinSeparationValue(1f)
                makeAppointmentList() },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun makeAppointmentList(){
        for (hour in startHour until endHour+1){
//            val x = if (hour == startHour) startMinute else 0
//            val y = if (hour == endHour) endMinute else 60
            for (minute in 0 until 60) {
                val remainder = (minute + styleItem.time.toInt()) / 60
                val mod = (minute + styleItem.time.toInt()) % 60
                val closeHour = hour + remainder
                val closeMinute = minute + mod

                val startTime = hour * 60 + minute
                val endTime = closeHour * 60 + closeMinute

                val start = getString(R.string.time_colon,hour.toString(),minute.toString())
                val end = getString(R.string.time_colon,closeHour.toString(),closeMinute.toString())

                appointmentList.add(AppointmentItem(start,end,startTime,endTime)) } }
        findBookings()
    }

    private fun findBookings(){

        val url = getString(R.string.url,"find_available_times.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val obj = JSONObject(response)
                val datesArray = obj.getJSONArray("dates")
                val breaksArray = obj.getJSONArray("break")
                for (x in 0 until datesArray.length()) {
                    val date = datesArray.getJSONObject(x)
                    val startBook = date.getInt("start")
                    val endBook = date.getInt("end")
                    for (item in appointmentList){ if ((item.startMinute .. item.endMinute).contains(startBook) ||
                            (item.startMinute .. item.endMinute).contains(endBook)){ item.available = false } } }
                for (x in 0 until breaksArray.length()) {
                    val breaks = breaksArray.getJSONObject(x)
                    val startBreak = breaks.getInt("start")
                    val endBreak = breaks.getInt("end")
                    for (item in appointmentList){ if ((item.startMinute .. item.endMinute).contains(startBreak) ||
                        (item.startMinute .. item.endMinute).contains(endBreak)){ item.available = false } } }
                rvBook.adapter?.notifyItemRangeChanged(0,appointmentList.size)
                                                },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["day"] = date
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun makeCalendar(){
        dayList.clear()
        rvDays.adapter?.notifyItemRangeRemoved(0,7)
        for (i in 1 until 8){
        calendar.add(Calendar.DATE, i)
        val dt = Triple(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR))
        Log.println(Log.ASSERT,"DT",dt.toString())
        dayList.add(dt)}
        rvDays.adapter?.notifyItemRangeInserted(0,7)
    }
}
