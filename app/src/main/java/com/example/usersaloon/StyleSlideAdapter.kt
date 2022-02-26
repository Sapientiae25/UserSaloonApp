package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

//class StyleSlideAdapter(private val slideList: MutableList<FilterItem>) : SliderViewAdapter<StyleSlideAdapter.VH>() {
//    fun renewItems(slideItems: MutableList<FilterItem>) {
//        slideList = slideItems
//        notifyDataSetChanged()
//    }
//
//    fun addItem(sliderItem: String) {
//        slideList.add(sliderItem)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup): VH {
//        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.slide_layout, false)
//        return VH(inflate)
//    }
//
//    override fun onBindViewHolder(viewHolder: VH, position: Int) { viewHolder.bind(position) }
//
//    override fun getCount() = slideList.size
//    inner class VH(itemView: View) : ViewHolder(itemView) {
//        val ivSlide: ImageView = itemView.findViewById(R.id.ivSlide)
//        val tvText: TextView = itemView.findViewById(R.id.tvText)
//        fun bind(index: Int){
//            val currentItem = slideList[index]
//            tvText.text = currentItem.text
//        }
//
//    }
//}