package com.example.usersaloon

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CancelAdapter (private val styleItemList: MutableList<BookingItem>,val fragment: BookingFragment)
    : RecyclerView.Adapter<CancelAdapter.CancelViewHolder>() {

    inner class CancelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val tvReason: TextView = itemView.findViewById(R.id.tvReason)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val styleItem = currentItem.styleItem
            tvReason.text = currentItem.reason
            tvReason.post{
                val lineCount = tvReason.lineCount
                if (lineCount > 1){
                    tvReason.maxLines = 1
                    tvReason.ellipsize = TextUtils.TruncateAt.END
                    tvReason.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_arrow_drop_down_24)
                    tvReason.setOnClickListener{
                    if (tvReason.maxLines == 1) { tvReason.ellipsize = null; tvReason.maxLines = Integer.MAX_VALUE
                    tvReason.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_keyboard_arrow_up_24)}
                    else {tvReason.maxLines = 1;tvReason.ellipsize = TextUtils.TruncateAt.END
                    tvReason.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_arrow_drop_down_24)}}}}

            name.text = styleItem.name
            price.text = itemView.context.getString(R.string.money,styleItem.price)
            tvAddress.text = styleItem.accountItem.addressItem?.address
            if (styleItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = styleItem.rating.toFloat()}
            time.text = itemView.context.getString(R.string.separate,currentItem.time,currentItem.date)
            itemView.setOnClickListener {
                val bookedBottomSheet = BookedBottomSheet()
                val bundle = bundleOf(Pair("styleItem",currentItem.styleItem),Pair("location",false))
                bookedBottomSheet.arguments = bundle
                bookedBottomSheet.show(fragment.childFragmentManager,"bookedBottomSheet") }
            Picasso.get().load(itemView.context.getString(
                R.string.url,"style_images/${styleItem.imageId}.jpeg")).fit().centerCrop().into(image)
        } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cancel_layout,
            parent, false)
        return CancelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CancelViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = styleItemList.size
}