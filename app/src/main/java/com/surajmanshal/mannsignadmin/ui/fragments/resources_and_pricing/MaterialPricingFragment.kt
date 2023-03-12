package com.surajmanshal.mannsignadmin.ui.fragments.resources_and_pricing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.PricingAdapter
import com.surajmanshal.mannsignadmin.utils.Constants
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
                vm.getMaterials(listOf(Constants.TYPE_POSTER,Constants.TYPE_BANNER))

                vm.materials.observe(viewLifecycleOwner, Observer {
                    adapter = PricingAdapter(it,vm)
                })
            }
        }
        return view
    }
    override fun onPause() {
        super.onPause()
        vm.serverResponse.removeObserver{
        }
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