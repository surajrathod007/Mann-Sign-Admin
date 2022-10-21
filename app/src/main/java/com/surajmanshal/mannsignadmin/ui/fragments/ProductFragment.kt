package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.ProductPricingAdapter
import com.surajmanshal.mannsignadmin.data.model.ProductType
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel


class ProductFragment : Fragment() {

    private var columnCount = 1
    private lateinit var vm : PricingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            vm = it.getSerializable("vm") as PricingViewModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                vm.getProductTypes()
                val t = ProductType(1,"Banner",350f)
                val t2 = ProductType(1,"Board",500f)
                adapter = ProductPricingAdapter(listOf(t,t2,t,t2,t2),vm)

                vm.productTypes.observe(viewLifecycleOwner, Observer {
                    adapter = ProductPricingAdapter(it,vm)
                })
                vm.serverResponse.observe(viewLifecycleOwner, Observer {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                })
            }
        }

        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(vm: PricingViewModel ,columnCount: Int = 1) =
            ProductFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putSerializable("vm",vm)
                }
            }
    }
}