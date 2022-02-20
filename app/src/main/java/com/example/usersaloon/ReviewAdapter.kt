package com.example.usersaloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter (private val reviewList: MutableList<ReviewItem>)
    : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvReview: TextView = itemView.findViewById(R.id.tvReview)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(index: Int){
            val currentItem = reviewList[index]
            tvReview.text = currentItem.review
            tvDate.text = currentItem.date
            rating.rating = (currentItem.rating / 10).toFloat()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_layout,
            parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = reviewList.size
}