package com.example.usersaloon

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SaloonBottomSheet : BottomSheetDialogFragment(){

    private lateinit var rvSaloons: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_saloon_bottom_sheet, container, false)
        val saloonList = arguments?.getParcelableArrayList<AccountItem>("saloonList")!!
        rvSaloons = rootView.findViewById(R.id.rvSaloons)
//        rvSaloons.adapter = MapSaloonAdapter(saloonList,)
        rvSaloons.layoutManager = LinearLayoutManager(context)
        rvSaloons.adapter?.notifyItemRangeInserted(0,saloonList.size)
        return rootView
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply { setCanceledOnTouchOutside(false) } }

}
