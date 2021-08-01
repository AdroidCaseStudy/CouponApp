package com.cg.couponsapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg.couponsapp.utils.CouponsAdapter
import com.cg.couponsapp.utils.MakeProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CouponsFragment : Fragment() {

    lateinit var couponsList : RecyclerView
    lateinit var couponAdapter : CouponsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupons_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        couponsList = view.findViewById(R.id.couponsListRView)
        couponAdapter = CouponsAdapter(view)

        couponsList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        couponsList.layoutManager = layout

        couponAdapter.displayAllCoupons(couponsList)
    }

    override fun onPause() {
        super.onPause()
    }



}


