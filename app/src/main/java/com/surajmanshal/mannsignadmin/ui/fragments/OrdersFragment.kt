package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.adapter.OrdersAdapter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.databinding.FragmentOrdersBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : Fragment() {

    lateinit var binding: FragmentOrdersBinding
    lateinit var viewModel : OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(OrdersViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_orders,container,false)

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.setupViewModelDataMembers()
        }

        viewModel.allOrders.observe(viewLifecycleOwner,{
            if(!it.isNullOrEmpty())
            binding.rvOrders.adapter = OrdersAdapter(requireContext(),it)
        })

        binding.txtAllOrders.setOnClickListener {
            //Toast.makeText(requireContext(),"${viewModel.allOrders.value}",Toast.LENGTH_LONG).show()
            viewModel.getAllOrders()
            binding.rvOrders.adapter = OrdersAdapter(requireContext(),viewModel.allOrders.value!!)
        }

        binding.txtConfirmedOrders.setOnClickListener {
            viewModel.filterOrder(Constants.ORDER_CONFIRMED)
        }

        return binding.root
    }



}