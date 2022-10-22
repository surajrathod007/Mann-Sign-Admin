package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.PricingAdapter
import com.surajmanshal.mannsignadmin.utils.PricingItems
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel


class MaterialPricingFragment : Fragment() {
    private var columnCount = 1
    private lateinit var vm: PricingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ProductPricingFragment.ARG_COLUMN_COUNT)
            vm = it.getSerializable("vm") as PricingViewModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                vm.getMaterials()

                vm.material.observe(viewLifecycleOwner, Observer {
                    adapter = PricingAdapter(it,vm)
                })
                vm.serverResponse.observe(viewLifecycleOwner, Observer {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                })
            }
        }
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(vm: PricingViewModel ,columnCount: Int = 1) =
            MaterialPricingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ProductPricingFragment.ARG_COLUMN_COUNT, columnCount)
                    putSerializable("vm", vm)
                }
            }
    }
}