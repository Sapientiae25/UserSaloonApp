package com.example.usersaloon

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.slider.RangeSlider
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AppointmentFragment : Fragment(), DatePickerDialog.OnDateSetListener{

    private lateinit var rvDays: RecyclerView
    private lateinit var rvBook: RecyclerView
    private lateinit var rsTime: RangeSlider
    private lateinit var tvDate: TextView
    private lateinit var llNoBookings: LinearLayout
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var ivCalendar: ImageView
    private lateinit var rbAll: RadioButton
    private lateinit var rgFilter: RadioGroup
    private lateinit var rbFilter: RadioButton
    private lateinit var styleItem: StyleItem
    private lateinit var accountItem: AccountItem
    private var dayList = mutableListOf<DayItem>()
    private var appointmentList = mutableListOf<AppointmentItem>()
    private var defaultList = mutableListOf<AppointmentItem>()
    private var chosenHours =  AppointmentItem()
    private var calendar = Calendar.getInstance()
    val indexList = mutableListOf<Int>()
    private var year = 0
    private var month = 0
    private var day = 0
    var date = ""
    var startHour = 0
    var endHour = 0
    var openHour = 0
    var closeHour = 0
    private val d = Date()
    private val today = DateFormat.format("dd/MM/yyyy", d.time)
    var filter = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_appointment, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Book Style"
        styleItem = arguments?.getParcelable("styleItem")!!
        accountItem = styleItem.accountItem
        rvDays = rootView.findViewById(R.id.rvDays)
        rvBook = rootView.findViewById(R.id.rvBook)
        rsTime = rootView.findViewById(R.id.rsTime)
        tvDate = rootView.findViewById(R.id.tvDate)
        ivCalendar = rootView.findViewById(R.id.ivCalendar)
        rbAll = rootView.findViewById(R.id.rbAll)
        rgFilter = rootView.findViewById(R.id.rgFilter)
        rbFilter = rootView.findViewById(R.id.rbFilter)
        tvStart = rootView.findViewById(R.id.tvStart)
        tvEnd = rootView.findViewById(R.id.tvEnd)
        llNoBookings = rootView.findViewById(R.id.llNoBookings)

        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        rvDays.adapter = DayAdapter(dayList){ item ->
            val dateItem = item.date
            day = dateItem.first; month = dateItem.second; year = dateItem.third
            date = getString(R.string.user_date,day,month,year)
            tvDate.text = date
            calendar.set(year,month-1,day-1)
            findBookings()}
        rvBook.adapter = AppointmentAdapter(appointmentList,styleItem,indexList){ t ->
            val paymentBottomSheet = PaymentBottomSheet { findBookings() }
            val bookingItem = BookingItem("",t,date,styleItem,accountItem)
            val bundle = bundleOf(Pair("bookingItem",bookingItem))
            paymentBottomSheet.arguments = bundle
            paymentBottomSheet.show(childFragmentManager,"paymentBottomSheet")
        }
        rvDays.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        rvBook.layoutManager = LinearLayoutManager(context)
        rvBook.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

        rsTime.addOnChangeListener { _, _, _ ->
            val values = rsTime.values
            startHour = values[0].toInt()
            endHour = values[1].toInt()
            tvStart.text = getString(R.string.time_format,startHour,0)
            tvEnd.text = getString(R.string.time_format,endHour,0)
            chosenHours.start = getString(R.string.time_format,startHour,0)
            chosenHours.end = getString(R.string.time_format,endHour,0)
            var counter = 0
            for (item in appointmentList){
                val s = item.start.split(":")[0].toInt()
                val e = item.end.split(":")[0].toInt()

                if ((startHour .. endHour).contains(s) && (startHour .. endHour).contains(e)){
                    if (!appointmentList.contains(item)){item.visible=true
                        rvBook.adapter?.notifyItemInserted(counter)}
                    else counter += 1
                }else{
                    val index = appointmentList.indexOf(item)
                    if (index != -1){item.visible=false
                        rvBook.adapter?.notifyItemRemoved(index)}} } }

        ivCalendar.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),this,year,month-1,day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis(); datePickerDialog.show() }

        loadData()
        swipeRefresh.setOnRefreshListener { loadData();swipeRefresh.isRefreshing = false }

        rbAll.setOnClickListener { if (filter) {filter = false; findBookings()}}
        rbFilter.setOnClickListener { if (!filter) {filter = true; findBookings() }}

        return rootView
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

                for (hour in startHour until endHour){
                    for (minute in 0 until 60 step 15) {
                        val remainder = (minute + styleItem.time.toInt()) / 60
                        val closeMinute = (minute + styleItem.time.toInt()) % 60
                        val closeHour = hour + remainder
                        if (endHour == closeHour) break
                        val startTime = hour * 60 + minute
                        val endTime = closeHour * 60 + closeMinute
                        val start = getString(R.string.time_format, hour, minute)
                        val end = getString(R.string.time_format, closeHour, closeMinute)
                        appointmentList.add(AppointmentItem(start,end,startTime,endTime,true,date)) }}
                findBookings() },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }


    private fun findBookings(){
        rvBook.adapter?.notifyItemRangeRemoved(0,indexList.size)
        indexList.clear()

        val url = getString(R.string.url,"find_available_times.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                val removeList = mutableListOf<Int>()
                var last = false
                for (z in 0 until appointmentList.size){ val item = appointmentList[z]
                    item.visible = true
                    if (last) item.available = false
                    else{
                        for (x in 0 until arr.length()) {
                            val obj = arr.getJSONObject(x)
                            var startBook = obj.getString("start").toIntOrNull()
                            var endBook = obj.getString("end").toIntOrNull()
                            if (endBook == null) { last = true;break } else {endBook -= 1}
                            if (endBook < item.startMinute) removeList.add(x)
                            if (startBook != null) startBook += 1
                            if ((item.startMinute .. item.endMinute).contains(startBook) || startBook == null ||
                                (item.startMinute .. item.endMinute).contains(endBook)){
                            if (filter) {item.visible = false} else {item.available = false; break } } }
                    for (i in removeList) arr.remove(i) }; if (item.visible) indexList.add(z) }
                if (today == date){ calendar.time = d
                    val todayHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val todayMinute = calendar.get(Calendar.MINUTE)

                    for (i in 0 until appointmentList.size){
                        val item = appointmentList[i]
                        val split = item.start.split(":")
                        val h = split[0].toInt()
                        val m = split[1].toInt()
                        if (todayHour > h || (todayHour == h && todayMinute > m)) {item.visible = false; indexList.remove(i)}
                        else break } }
                llNoBookings.visibility = if (indexList.size == 0) View.VISIBLE else View.GONE
                rvBook.adapter?.notifyItemRangeInserted(0,indexList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            params["start"] = getString(R.string.make_datetime,date,chosenHours.start)
            params["end"] = getString(R.string.make_datetime,date,chosenHours.end)
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun makeCalendar(){
        dayList.clear()
        for (i in 1 until 8){
            val dt = Triple(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR))
            calendar.add(Calendar.DATE, 1)
            dayList.add(DayItem(dt,i == 1))}
        rvDays.adapter?.notifyItemRangeChanged(0,6) }
    override fun onDateSet(view: DatePicker?, newYear: Int, newMonth: Int, newDay: Int) {
        day = newDay
        month = newMonth+1
        year = newYear
        calendar.set(year,month-1,day)
        date = getString(R.string.user_date,day,month,year)
        tvDate.text = date
        makeCalendar()
    }
}