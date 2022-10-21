package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityPriceManagementBinding
import com.surajmanshal.mannsignadmin.ui.fragments.MaterialPricingFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ProductPricingFragment
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel

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
        val fragmentList = listOf(ProductPricingFragment.newInstance(vm),MaterialPricingFragment.newInstance(vm))
        binding.viewPager2.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.animatedBottomBar.setupWithViewPager2(binding.viewPager2)
    }
}