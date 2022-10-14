package com.surajmanshal.mannsignadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.surajmanshal.mannsignadmin.ui.ProductManagementActivity
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityMainBinding
import com.surajmanshal.mannsignadmin.ui.fragments.DashboardFragment
import com.surajmanshal.mannsignadmin.ui.fragments.OrdersFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ReportsFragment

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this,ProductManagementActivity::class.java))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
    }

    fun setupViewPager(){
        val fragmentList = listOf(DashboardFragment(),OrdersFragment(),ReportsFragment())
        binding.viewPager.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
    }
}