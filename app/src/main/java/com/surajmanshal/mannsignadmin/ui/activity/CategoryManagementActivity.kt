package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding

class CategoryManagementActivity : AdapterActivity() {
    private lateinit var _binding : ActivityCategoryManagementBinding
    val binding get() = _binding
    /*lateinit var vm : CategoryViewModel*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}