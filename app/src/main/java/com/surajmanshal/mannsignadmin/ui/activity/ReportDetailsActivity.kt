package com.surajmanshal.mannsignadmin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityReportDetailsBinding
import com.surajmanshal.mannsignadmin.ui.fragments.DashboardFragment
import com.surajmanshal.mannsignadmin.ui.fragments.OrdersFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ReportsFragment
import com.surajmanshal.mannsignadmin.ui.fragments.reporting.OrderReportFragment
import com.surajmanshal.mannsignadmin.ui.fragments.reporting.ProductReportFragment
import com.surajmanshal.mannsignadmin.ui.fragments.reporting.TransactionReportFragment
import com.surajmanshal.mannsignadmin.ui.fragments.reporting.UserReportFragment

class ReportDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityReportDetailsBinding
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        val i = intent
        index = i.getIntExtra("index",0)
        setupViewPager()

        setContentView(binding.root)
    }

    fun setupViewPager(){
        val fragmentList = listOf(OrderReportFragment(),TransactionReportFragment(),ProductReportFragment(),UserReportFragment())
        binding.vpReports.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.vpReports.setCurrentItem(index)
        binding.bottomNavigationReport.setupWithViewPager2(binding.vpReports)

    }
}