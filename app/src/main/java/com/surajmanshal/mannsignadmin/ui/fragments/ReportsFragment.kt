package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentReportsBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    lateinit var binding: FragmentReportsBinding
    lateinit var vm: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        binding = FragmentReportsBinding.bind(view)

        vm.allOrders.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                binding.txtTotalOrders.text = it.size.toString()
        }

        vm.transactions.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.txtTotalTransactions.text = it.size.toString()
                var t = 0.0f
                it.forEach {
                    t += it.amount
                }
                binding.txtTotalEarnings.text = "₹" + t
            }
        }

        vm.serverResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
        }
        return binding.root
    }


}