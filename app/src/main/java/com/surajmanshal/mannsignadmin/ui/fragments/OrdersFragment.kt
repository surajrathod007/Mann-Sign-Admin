package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.madapps.liquid.LiquidRefreshLayout
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.OrdersAdapter
import com.surajmanshal.mannsignadmin.databinding.FragmentOrdersBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                    binding.refreshLayout.finishRefreshing()
                }
                if (it) {
                    binding.loading.visibility = View.VISIBLE
                }
            }


            binding.refreshLayout.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
                override fun completeRefresh() {

                }

                override fun refreshing() {
                    viewModel.isLoading.postValue(true)
                    //Toast.makeText(requireContext(),"${viewModel.allOrders.value}",Toast.LENGTH_LONG).show()
                    viewModel.getAllOrders()
                    binding.rvOrders.adapter =
                        OrdersAdapter(requireContext(), viewModel.allOrders.value!!)
                }
            })

        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.setupViewModelDataMembers()
        }
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
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PENDING }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Pending Orders", Toast.LENGTH_SHORT)
                            .show()

                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PENDING })
                }
                R.id.txtConfirmedOrders -> {
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CONFIRMED }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Confirmed Orders", Toast.LENGTH_SHORT)
                            .show()

                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CONFIRMED })
                }
                R.id.txtProcessingOrders -> {
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PROCCESSING }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Processing Orders", Toast.LENGTH_SHORT)
                            .show()
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_PROCCESSING })
                }
                R.id.txtReadyOrders -> {
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_READY }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Ready Orders", Toast.LENGTH_SHORT)
                            .show()
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_READY })
                }
                R.id.txtDeliveredOrders -> {
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_DELIVERED }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Delivered Orders", Toast.LENGTH_SHORT)
                            .show()
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_DELIVERED })
                }
                R.id.txtCanceledOrders -> {
                    if (viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CANCELED }
                            .isEmpty())
                        Toast.makeText(requireContext(), "No Canceled Orders", Toast.LENGTH_SHORT)
                            .show()
                    binding.rvOrders.adapter = OrdersAdapter(
                        requireContext(),
                        viewModel.allOrders.value!!.filter { it.orderStatus == Constants.ORDER_CANCELED })
                }
            }
        }

    }


}