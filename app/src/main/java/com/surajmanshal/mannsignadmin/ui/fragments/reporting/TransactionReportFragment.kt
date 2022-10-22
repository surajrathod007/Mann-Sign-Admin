package com.surajmanshal.mannsignadmin.ui.fragments.reporting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.TransactionAdapter
import com.surajmanshal.mannsignadmin.data.model.DateFilter
import com.surajmanshal.mannsignadmin.databinding.FragmentTransactionReportBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionReportFragment : Fragment() {

    lateinit var binding: FragmentTransactionReportBinding
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
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)
        binding = FragmentTransactionReportBinding.bind(view)

        setupSpinner()

        vm.serverResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }

        vm.transactionItems.observe(viewLifecycleOwner){
            binding.rvTransaction.adapter = TransactionAdapter(it)
        }


        vm.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.transactionLoading.visibility = View.GONE
            }
            if (it) {
                binding.transactionLoading.visibility = View.VISIBLE
            }
        }
        binding.spTransactionFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> {
                            Toast.makeText(
                                requireContext(),
                                "Fetch All Transactions",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getAllTransactions()
                            }
                        }
                        1 -> {
                            val d = DateFilter(LocalDate.now().minusDays(7), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        2 -> {
                            val d = DateFilter(LocalDate.now().minusDays(30), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        3 -> {
                            val d = DateFilter(LocalDate.now().minusDays(180), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        4 -> {
                            val d = DateFilter(LocalDate.now().minusDays(365), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        5 -> {
                            Toast.makeText(
                                requireContext(),
                                "Open Calender",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        return binding.root
    }

    private fun setupSpinner() {

        val all = "Lifetime"

        val last7Days = "Last 7 days (${
            LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last30Days = "Last 30 days (${
            LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last180Days = "Last 180 days (${
            LocalDate.now().minusDays(180).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last365Days = "Last 365 days (${
            LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val custom = "Custom Date"

        val arr = arrayOf(all, last7Days, last30Days, last180Days, last365Days, custom)

        val adp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arr)
        binding.spTransactionFilter.adapter = adp

    }

}