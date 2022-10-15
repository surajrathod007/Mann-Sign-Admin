package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.databinding.FragmentOrdersBinding
import com.surajmanshal.mannsignadmin.ui.fragments.ordertabs.*

class OrdersFragment : Fragment() {


    lateinit var binding: FragmentOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_orders,container,false)


        //setupViewPager()
        return binding.root
    }

    /*
    fun setupViewPager(){

        val fragmentList = listOf(AllOrdersFragment(),PendingOrdersFragment(),ProcessingOrdersFragment(),ReadyOrdersFragment(),DeliveredOrdersFragment(),CanceledOrdersFragment())
        binding.viewPagerOrders.adapter = MainViewPagerAdapter(fragmentList,
            requireActivity() as AppCompatActivity
        )
        TabLayoutMediator(binding.tabLayout,binding.viewPagerOrders){tab,position->

            when(position){
                0-> {
                    tab.text = "All Orders"
                }
                1-> {
                    tab.text = "Pending"
                }
                2-> {
                    tab.text = "Proccessing"
                }
                3-> {
                    tab.text = "Ready"
                }
                4-> {
                    tab.text = "Delivered"
                }
                5-> {
                    tab.text = "Canceled"
                }

            }

        }.attach()


    }


     */

}