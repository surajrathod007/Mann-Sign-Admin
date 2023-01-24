package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentDashboardBinding
import com.surajmanshal.mannsignadmin.ui.activity.CategoryManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.PriceManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.ui.activity.ResourcesManagementActivity

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
            btnProductManagement.root.setOnClickListener{ startActivity(Intent(activity, ProductsActivity::class.java)) }
            btnCategoryManagement.root.setOnClickListener{ startActivity(Intent(activity, CategoryManagementActivity::class.java)) }
            btnPriceManagement.root.setOnClickListener{ startActivity(Intent(activity, PriceManagementActivity::class.java)) }
            btnResManagement.root.setOnClickListener { startActivity(Intent(activity, ResourcesManagementActivity::class.java))  }

            val managementCardViews = arrayOf(
                btnProductManagement,btnCategoryManagement,btnPriceManagement,btnResManagement
            )
            val cardLabels = arrayOf(
                "Products Management",
                "Category Management",
                "Price Management",
                "Resources Management"
            )
            val cardIcons = arrayOf(
                R.drawable.ic_box,
                R.drawable.ic_baseline_import_contacts_24,
                R.drawable.ic_money,
                R.drawable.ic_fabric
            )

            val cardColors = arrayOf(
                R.color.title_text_color,
                R.color.authButtonColor,
                R.color.gold,
                R.color.purple_700
            )
            for(i in managementCardViews.indices){
                managementCardViews[i].apply {
                    tvUserName.text = cardLabels[i]
                    ivCardIcon.apply {
                        setImageResource(cardIcons[i])
                        imageTintList = AppCompatResources.getColorStateList(requireContext(),cardColors[i])
                    }
                }
            }

        }
        return binding.root
    }

}