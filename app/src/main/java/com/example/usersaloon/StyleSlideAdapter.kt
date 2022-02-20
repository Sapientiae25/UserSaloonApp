package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import java.util.*

class StyleSlideAdapter(val slideList: MutableList<FilterItem>) : StyleSlideAdapter<StyleSlideAdapter.VH>() {
    private var mSliderItems = ArrayList<String>()
    fun renewItems(slideItems: MutableList<FilterItem>) {
        mSliderItems = slideItems
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: String) {
        slideList.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.slide_layout, null)
        return VH(inflate)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        Picasso.get().load(slideList[position]).fit().into(viewHolder.ivSlide)
    }
    override fun getCount() = slideList.size
    inner class VH(itemView: View) : ViewHolder(itemView) {
        val ivSlide: ImageView = itemView.findViewById(R.id.ivSlide)
        val tvText: TextView = itemView.findViewById(R.id.tvText)

    }
}