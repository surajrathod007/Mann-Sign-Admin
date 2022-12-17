package com.surajmanshal.mannsignadmin.ui.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsignadmin.data.model.Area
import com.surajmanshal.mannsignadmin.data.model.DiscountCoupon
import com.surajmanshal.mannsignadmin.databinding.ActivityPriceManagementBinding
import com.surajmanshal.mannsignadmin.databinding.DialogContainerBinding
import com.surajmanshal.mannsignadmin.ui.fragments.resources_and_pricing.DeliveryPricingFragment
import com.surajmanshal.mannsignadmin.ui.fragments.resources_and_pricing.DiscountPricingFragment
import com.surajmanshal.mannsignadmin.ui.fragments.resources_and_pricing.MaterialPricingFragment
import com.surajmanshal.mannsignadmin.ui.fragments.resources_and_pricing.ProductPricingFragment
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.setInputTypeDecimalNumbers
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

class PriceManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityPriceManagementBinding
    val binding get() = _binding
    lateinit var vm : PricingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPriceManagementBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        val addables = arrayOf(2,3)
        vm =  ViewModelProvider(this)[PricingViewModel::class.java]
        setupViewPager()

        vm.allowAdding.observe(this, Observer {
            binding.btnAddNew.isVisible = it
        })

        binding.animatedBottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                if(addables.contains(newIndex)){
                    vm.allowToAdd(true)
                    binding.btnAddNew.setOnClickListener {
                        if(newIndex==2) setUpShippingChargeDialog()
                        if(newIndex==3) setUpCouponCodeDialog()
                    }
                }
                else vm.allowToAdd(false)
            }
        })
        vm.serverResponse.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpCouponCodeDialog(){
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle("New Discount Coupon")
        val etCode = EditText(this)
        val etValue = EditText(this)
        val etQty = EditText(this)
        etCode.hint = resources.getString(R.string.couponCode)
        etQty.hint = resources.getString(R.string.Quantity)
        etValue.hint = "Discount Value( 0-100%)"
        Functions.setTypeNumber(etValue)
        Functions.setTypeNumber(etQty)
        val dialogContainerView =  DialogContainerBinding.inflate(layoutInflater)
        with(dialogContainerView.dialogContainer){
            addView(etCode)
            addView(etValue)
            addView(etQty)
        }

        dialog.setView(dialogContainerView.root)
        dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.addCoupon(
                        DiscountCoupon("${etCode.text}",
                            etValue.text.toString().toInt(),
                            etQty.text.toString().toInt()
                        )
                    )
                    vm.getCoupons()
                }
            }
        })
        val d = dialog.create()

        etValue.doOnTextChanged{ text, _, _, _ ->
            enableOrDisableAddButton(etValue,etCode,etQty,d)
        }
        etQty.doOnTextChanged{ text, _, _, _ ->
            enableOrDisableAddButton(etValue,etCode,etQty,d)
        }
        etCode.doOnTextChanged{ text, _, _, _ ->
            enableOrDisableAddButton(etValue,etCode,etQty,d)
        }
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    private fun setUpShippingChargeDialog(){
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle("New Area")
        val etPinCode = EditText(this)
        val etAreaName = EditText(this).apply {
            setSingleLine()
        }
        val etMinCharges = EditText(this)
        etPinCode.hint = resources.getString(R.string.pinCode)
        etMinCharges.hint = "Minimum charges"
        etAreaName.hint = resources.getString(R.string.areaName)
        etMinCharges.setInputTypeDecimalNumbers()
        Functions.setTypeNumber(etPinCode)
        val dialogContainerView =  DialogContainerBinding.inflate(layoutInflater)
        with(dialogContainerView.dialogContainer){
            addView(etPinCode)
            addView(etAreaName)
            addView(etMinCharges)
        }

        dialog.setView(dialogContainerView.root)
        dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.addArea(
                        Area(etPinCode.text.toString(),
                            etAreaName.text.toString(),
                            etMinCharges.text.toString().toFloat()
                        )
                    )
                    vm.getAreas()
                }
            }
        })
        val d = dialog.create()

        etAreaName.doOnTextChanged{ text, _, _, _ ->
            enableOrDisableAddButton(etAreaName,etPinCode,etMinCharges,d)
        }
        etMinCharges.doOnTextChanged{ text, _, _, _ ->
            if (text != null) {
                if(text.length>7) etMinCharges.clearFocus()
            }
            enableOrDisableAddButton(etAreaName,etPinCode,etMinCharges,d)
        }
        etPinCode.doOnTextChanged{ text, _, _, _ ->
            if (text != null) {
                if(text.length>5) etAreaName.requestFocus()
            }
            enableOrDisableAddButton(etAreaName,etPinCode,etMinCharges,d)
        }
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    private fun enableOrDisableAddButton(et1: EditText,et2: EditText,et3: EditText,d: android.app.AlertDialog) {
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = et1.text.isNotEmpty()&&et2.text.isNotEmpty()&&et3.text.isNotEmpty()
    }

    fun setupViewPager(){
        val fragmentList = listOf(
            ProductPricingFragment.newInstance(vm), MaterialPricingFragment.newInstance(vm)
            , DeliveryPricingFragment.newInstance(vm), DiscountPricingFragment.newInstance(vm))
        binding.viewPager2.adapter = MainViewPagerAdapter(fragmentList,this)
        binding.animatedBottomBar.setupWithViewPager2(binding.viewPager2)
    }
}