package com.cg.couponsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg.couponsapp.utils.CouponsAdapter
import com.cg.couponsapp.utils.DealsAdapter

class DealsFragment : Fragment() {

    lateinit var dealsList : RecyclerView
    lateinit var dealAdapter : DealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_coupons_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dealsList = view.findViewById(R.id.couponsListRView)
        dealAdapter = DealsAdapter(view)

        dealsList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        dealsList.layoutManager = layout

        dealAdapter.displayAllDeals(dealsList)
    }
}