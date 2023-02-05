package com.surajmanshal.mannsignadmin.ui.fragments.reporting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductReportAdapter
import com.surajmanshal.mannsignadmin.databinding.FragmentProductReportBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel


class ProductReportFragment : Fragment() {


    lateinit var binding : FragmentProductReportBinding
    lateinit var vm : StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_report, container, false)
        binding = FragmentProductReportBinding.bind(view)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)
        vm.getPosters()
        setUpObservers()
        return binding.root
    }

    private fun setUpObservers() {
        vm.products.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                binding.rvProductReports.adapter = ProductReportAdapter(it,requireContext())
            }
        }
        vm.isLoading.observe(viewLifecycleOwner){
            if(it){
                binding.progressReport.visibility = View.VISIBLE
            }else{
                binding.progressReport.visibility = View.GONE
            }
        }
    }


}