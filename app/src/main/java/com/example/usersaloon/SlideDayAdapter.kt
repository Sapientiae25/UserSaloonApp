package com.example.usersaloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class SlideDayAdapter (private val bookingList: MutableList<DayItem>,var appointmentList: MutableList<AppointmentItem>,
                       val chosenHours:AppointmentItem,val styleItem: StyleItem)
    : RecyclerView.Adapter<SlideDayAdapter.SlideDayViewHolder>(),ChangeDate {

    val accountItem = styleItem.accountItem!!
    lateinit var rvBook: RecyclerView

    inner class SlideDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(index: Int){
            rvBook = itemView.findViewById(R.id.rvBook)
            val currentItem = bookingList[index].date
            val date = itemView.context.getString(R.string.user_date,currentItem.first,currentItem.second,currentItem.third)
            rvBook.adapter = AppointmentAdapter(appointmentList,styleItem)
            rvBook.addItemDecoration(DividerItemDecoration(itemView.context, RecyclerView.VERTICAL))
            rvBook.layoutManager = LinearLayoutManager(itemView.context)
            if (appointmentList.isNotEmpty()) findBookings(date)
        }

        private fun findBookings(date: String){
            val url = itemView.context.getString(R.string.url,"find_available_times.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val obj = JSONObject(response)
                    val datesArray = obj.getJSONArray("dates")
                    val breaksArray = obj.getJSONArray("break")
                    for (x in 0 until datesArray.length()) {
                        val dt = datesArray.getJSONObject(x)
                        val startBook = dt.getInt("start")
                        val endBook = dt.getInt("end")
                        for (z in 0 until appointmentList.size){ val item = appointmentList[z]
                            if ((item.startMinute .. item.endMinute).contains(startBook) ||
                                (item.startMinute .. item.endMinute).contains(endBook)){ item.available = false
                                rvBook.adapter?.notifyItemChanged(z)} } }
                    for (x in 0 until breaksArray.length()) {
                        val breaks = breaksArray.getJSONObject(x)
                        val startBreak = breaks.getInt("start")
                        val endBreak = breaks.getInt("end")
                        for (z in 0 until appointmentList.size){ val item = appointmentList[z]
                            if ((item.startMinute .. item.endMinute).contains(startBreak) ||
                                (item.startMinute .. item.endMinute).contains(endBreak)){ item.available = false
                                rvBook.adapter?.notifyItemChanged(z)} } } },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    params["start"] = itemView.context.getString(R.string.make_datetime,date,chosenHours.start)
                    params["end"] = itemView.context.getString(R.string.make_datetime,date,chosenHours.end)
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideDayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout, parent, false)
        return SlideDayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SlideDayViewHolder, position: Int) { holder.bind(position) }

    override fun getItemCount() = bookingList.size
    override fun changeDate(start: String, end: String) {

    }


}