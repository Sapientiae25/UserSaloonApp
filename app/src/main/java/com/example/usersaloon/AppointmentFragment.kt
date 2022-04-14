package com.example.usersaloon

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.slider.RangeSlider
import org.json.JSONObject
import java.util.*

class AppointmentFragment : Fragment(), DatePickerDialog.OnDateSetListener{

    private lateinit var rvDays: RecyclerView
    private lateinit var vpBook: ViewPager2
    private lateinit var rsTime: RangeSlider
    private lateinit var tvDate: TextView
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var ivCalendar: ImageView
    private lateinit var rbAll: RadioButton
    private lateinit var rbFilter: RadioButton
    private lateinit var styleItem: StyleItem
    private lateinit var accountItem: AccountItem
    private var dayList = mutableListOf<DayItem>()
    private var appointmentList = mutableListOf<AppointmentItem>()
    private var defaultList = mutableListOf<AppointmentItem>()
    private var chosenHours =  AppointmentItem()
    private var calendar = Calendar.getInstance()
    private var year = 0
    private var month = 0
    private var day = 0
    lateinit var adapter: SlideDayAdapter
    var date = ""
    var startHour = 0
    var endHour = 0
    var openHour = 0
    var closeHour = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_appointment, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Book Style"
        styleItem = arguments?.getParcelable("styleItem")!!
        accountItem = styleItem.accountItem!!
        rvDays = rootView.findViewById(R.id.rvDays)
        vpBook = rootView.findViewById(R.id.vpBook)
        rsTime = rootView.findViewById(R.id.rsTime)
        tvDate = rootView.findViewById(R.id.tvDate)
        ivCalendar = rootView.findViewById(R.id.ivCalendar)
        rbAll = rootView.findViewById(R.id.rbAll)
        rbFilter = rootView.findViewById(R.id.rbFilter)
        tvStart = rootView.findViewById(R.id.tvStart)
        tvEnd = rootView.findViewById(R.id.tvEnd)
        adapter = SlideDayAdapter(dayList,appointmentList,chosenHours,styleItem)
        vpBook.adapter = adapter

        rvDays.adapter = DayAdapter(dayList){ item ->
            val dateItem = item.date
            val position = dayList.indexOf(item)
            vpBook.setCurrentItem(position,false)
            day = dateItem.first; month = dateItem.second; year = dateItem.third
            date = getString(R.string.user_date,day,month,year)
            tvDate.text = date
            calendar.set(year,month-1,day-1)
//            if (dateItem.first == dayList[dayList.size-1].date.first) makeCalendar()
        }
        rvDays.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        vpBook.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem = dayList[position].date
                for (x in 0 until dayList.size){ val item = dayList[x]
                    if (item.chosen) { item.chosen = false; rvDays.adapter?.notifyItemChanged(x); break }}
                dayList[position].chosen = true; year = currentItem.third; month = currentItem.second; day = currentItem.first
                rvDays.adapter?.notifyItemChanged(position)
                date = getString(R.string.user_date,day,month,year)
                tvDate.text = date
                calendar.set(year,month-1,day-1) } })


        rsTime.addOnChangeListener { _, _, _ ->
            val values = rsTime.values
            startHour = values[0].toInt()
            endHour = values[1].toInt()
            tvStart.text = getString(R.string.time_format,startHour,0)
            tvEnd.text = getString(R.string.time_format,endHour,0)
            chosenHours.start = getString(R.string.time_format,startHour,0)
            chosenHours.end = getString(R.string.time_format,endHour,0)
            var counter = 0
//            appointmentList.clear()
            for (item in defaultList){
                val s = item.start.split(":")[0].toInt()
                val e = item.end.split(":")[0].toInt()

                if ((startHour .. endHour).contains(s) && (startHour .. endHour).contains(e)){
                    if (!appointmentList.contains(item)){appointmentList.add(counter,item)
                        adapter.rvBook.adapter?.notifyItemInserted(counter)}
                    else counter += 1
                }else{
                    val index = appointmentList.indexOf(item)
                    if (index != -1){appointmentList.removeAt(index)
                        adapter.rvBook.adapter?.notifyItemRemoved(index)}}
            }
//            makeAppointmentList()
//            vpBook.adapter?.notifyItemChanged(vpBook.currentItem)
        }

        ivCalendar.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),this,year,month-1,day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis(); datePickerDialog.show() }

        loadData()
        return rootView
        }

    override fun onDateSet(view: DatePicker?, newYear: Int, newMonth: Int, newDay: Int) {
        day = newDay
        month = newMonth+1
        year = newYear
        calendar.set(year,month-1,day)
        date = getString(R.string.user_date,day,month,year)
        tvDate.text = date
        makeCalendar()
    }

    private fun loadData(){
        defaultList.clear()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH) + 1
        year = calendar.get(Calendar.YEAR)
        date = getString(R.string.user_date,day,month,year)
        tvDate.text = date
        makeCalendar()

        val url = getString(R.string.url,"get_booking_info.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response -> val obj = JSONObject(response)
                startHour = obj.getInt("s_hour")
                endHour = obj.getInt("e_hour")
                openHour = startHour
                closeHour = endHour
                tvStart.text = getString(R.string.time_format,startHour,0)
                tvEnd.text = getString(R.string.time_format,endHour,0)
                chosenHours.start = getString(R.string.time_format,startHour,0)
                chosenHours.end = getString(R.string.time_format,endHour,0)

                rsTime.valueFrom = startHour.toFloat()
                rsTime.valueTo = endHour.toFloat()
                val valueList = listOf(startHour.toFloat(),endHour.toFloat())
                rsTime.values = valueList.toList()
                makeAppointmentList()
                                                },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }

    private fun makeAppointmentList(){
        adapter.rvBook.adapter?.notifyItemRangeRemoved(0,appointmentList.size)
        appointmentList.clear()
        for (hour in startHour until endHour){
            for (minute in 0 until 60 step 15) {
                val remainder = (minute + styleItem.time.toInt()) / 60
                val closeMinute = (minute + styleItem.time.toInt()) % 60
                val closeHour = hour + remainder
                val startTime = hour * 60 + minute
                val endTime = closeHour * 60 + closeMinute

                val start = getString(R.string.time_format, hour, minute)
                val end = getString(R.string.time_format, closeHour, closeMinute)
                appointmentList.add(AppointmentItem(start, end, startTime, endTime)) }}
        adapter.rvBook.adapter?.notifyItemRangeInserted(0, appointmentList.size)
        if (defaultList.isEmpty()) defaultList.addAll(appointmentList)

    }

    private fun makeCalendar(){
        dayList.clear()
        for (i in 1 until 8){
            val dt = Triple(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR))
            calendar.add(Calendar.DATE, 1)
        dayList.add(DayItem(dt,i == 1))}
        rvDays.adapter?.notifyItemRangeChanged(0,6)
        vpBook.adapter?.notifyItemRangeChanged(0,6)
}
}