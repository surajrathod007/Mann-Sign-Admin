package com.surajmanshal.mannsignadmin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.onesignal.OneSignal
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityMainBinding
import com.surajmanshal.mannsignadmin.ui.fragments.DashboardFragment
import com.surajmanshal.mannsignadmin.ui.fragments.OrdersFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ReportsFragment

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding
    var isRead = false
    var isWrite = false

    lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        setupViewPager()
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }

        requestPermission()
        //store this id in DB when user login
        val uid = OneSignal.getDeviceState()?.userId
        Toast.makeText(this,"Player id " + uid, Toast.LENGTH_LONG).show()


    }

    fun setupViewPager() {
        val fragmentList = listOf(DashboardFragment(),OrdersFragment(),ReportsFragment())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
    }

    fun requestPermission(){

        //check permission already granted or not
        isRead = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWrite = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        var permissionRequest : MutableList<String> = ArrayList()

        if(!isRead){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(!isWrite){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permissionRequest.isNotEmpty()){
            //request permission
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }


    }


}