package com.surajmanshal.mannsignadmin.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
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
import com.surajmanshal.mannsignadmin.MainActivity
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.adapter.OrdersAdapter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.databinding.FragmentOrdersBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.ui.OrderDetailsActivity
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentOrdersBinding
    lateinit var viewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(OrdersViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)

        //setup click listners
        binding.txtPendingOrders.setOnClickListener(this)
        binding.txtAllOrders.setOnClickListener(this)
        binding.txtProcessingOrders.setOnClickListener(this)
        binding.txtReadyOrders.setOnClickListener(this)
        binding.txtDeliveredOrders.setOnClickListener(this)
        binding.txtCanceledOrders.setOnClickListener(this)
        binding.txtConfirmedOrders.setOnClickListener(this)

        if (NetworkService.checkForInternet(requireContext())) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.setupViewModelDataMembers()
            }

            viewModel.allOrders.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    binding.rvOrders.adapter = OrdersAdapter(requireContext(), it)
                }
            }

            viewModel.isEmptyList.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(requireContext(), "No Orders", Toast.LENGTH_LONG).show()
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) {
                if (!it) {
                    binding.loading.visibility = View.GONE
                }
                if (it) {
                    binding.loading.visibility = View.VISIBLE
                }
            }


        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_LONG).show()
        }


        return binding.root
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.txtAllOrders -> {
                    viewModel.isLoading.postValue(true)
                    //Toast.makeText(requireContext(),"${viewModel.allOrders.value}",Toast.LENGTH_LONG).show()
                    viewModel.getAllOrders()
                    binding.rvOrders.adapter =
                        OrdersAdapter(requireContext(), viewModel.allOrders.value!!)
                }
                R.id.txtPendingOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PENDING })
                }
                R.id.txtConfirmedOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CONFIRMED })
                }
                R.id.txtProcessingOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PROCCESSING })
                }
                R.id.txtReadyOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_READY })
                }
                R.id.txtDeliveredOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_DELIVERED })
                }
                R.id.txtCanceledOrders -> {
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CANCELED })
                }

            }
        }

    }


}