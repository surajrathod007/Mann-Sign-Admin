package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentDashboardBinding
import com.surajmanshal.mannsignadmin.ui.activity.CategoryManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.PriceManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {

    private lateinit var _binding : FragmentDashboardBinding
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        _binding = FragmentDashboardBinding.bind(view)

        with(binding){
            btnProductManagement.setOnClickListener{ startActivity(Intent(activity, ProductsActivity::class.java)) }
            btnCategoryManagement.setOnClickListener{ startActivity(Intent(activity, CategoryManagementActivity::class.java)) }
            btnPriceManagement.setOnClickListener{ startActivity(Intent(activity, PriceManagementActivity::class.java)) }
        }
        return binding.root
    }

}