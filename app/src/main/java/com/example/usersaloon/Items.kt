package com.example.usersaloon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeItem(val time: String="", val maxTime: String? = null): Parcelable
@Parcelize
data class StyleItem(val name: String="",val price: Float=0.0.toFloat(), val time: TimeItem=TimeItem(),val info: String="",
                     val id: String = "",val bookingId:String="",val filterItem: StyleFilterItem=StyleFilterItem(),
                     var accountItem: AccountItem? = null,val date: String="",val rating:Float?=null,var like: Boolean=false): Parcelable
@Parcelize
data class AccountItem(val id: String="",var name: String="", var password: String? = null,var number: String="",
                       var open: String? = null,var close: String? = null,var addressItem: AddressItem? = null,
                       val filterItem: FilterItem=FilterItem(),val rating:String="",var clicked: Boolean = false,
                       var like: Boolean = false): Parcelable
@Parcelize
data class UserItem(val id: String,val email: String="",val password: String="",var number: String="",
                    val gender: Int = 2): Parcelable
@Parcelize
data class AddressItem(var id:String="",var city: String="",var postcode: String="",var country: String="",var address: String="",
                       var latitude:Double=0.toDouble(),var longitude:Double=0.toDouble(),val distance:String?=null,
                       val town: String=""): Parcelable
data class ReviewItem(val review: String,val rating: Int ,val date: String)
@Parcelize
data class CategoryItem(val id: String,val category: String,val accountItem: AccountItem? = null): Parcelable
@Parcelize
data class BookingItem(val bookingId: String,val time: String="", val date: String="",val duration: String="",
                       val styleItem: StyleItem=StyleItem()): Parcelable
@Parcelize
data class StyleFilterItem(val length: String="",val gender: String="",val hair: String="",val style: String=""): Parcelable
@Parcelize
data class FilterItem(var gender: Int = 2, var length: MutableSet<Int> = mutableSetOf(),
                      var sort: Int=0,val text: String=""): Parcelable
@Parcelize
data class CardItem(val id: String="", val number: String="",val expiry: String="",val cvv: String=""): Parcelable