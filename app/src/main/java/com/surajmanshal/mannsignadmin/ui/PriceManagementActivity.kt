package com.surajmanshal.mannsignadmin.ui

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.databinding.ActivityPriceManagementBinding
import com.surajmanshal.mannsignadmin.ui.fragments.DashboardFragment
import com.surajmanshal.mannsignadmin.ui.fragments.OrdersFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ProductFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ReportsFragment
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriceManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityPriceManagementBinding
    val binding get() = _binding
    lateinit var vm : PricingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPriceManagementBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        vm =  ViewModelProvider(this)[PricingViewModel::class.java]
        setupViewPager()

    }
    fun setupViewPager(){
        val fragmentList = listOf(ProductFragment.newInstance(vm))
        binding.viewPager2.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.animatedBottomBar.setupWithViewPager2(binding.viewPager2)
    }
}