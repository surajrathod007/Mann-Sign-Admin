package com.surajmanshal.mannsignadmin.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajmanshal.mannsignadmin.adapter.CategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryManagementActivity : AdapterActivity() {
    private lateinit var _binding : ActivityCategoryManagementBinding
    val binding get() = _binding
    /*lateinit var vm : CategoryViewModel*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        vm = ViewModelProvider(this)[CategoryViewModel::class.java]
        /*vm.getCategories()
        binding.rvCategories.layoutManager = LinearLayoutManager(this)


        with(binding){
            btnCancel.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch{
                    vm.deletionCategory.value?.let { it1 -> vm.deleteCategory(it1) }
                }
                vm.onDeletionCancelOrDone()
            }
            btnAddCategory.setOnClickListener {
                setupInputDialog().show()
            }
        }
        vm.categories.observe(this, Observer {
            setAdapterWithList(it,binding.rvCategories,CategoryAdapter(vm))
        })
        vm.isDeleting.observe(this, Observer {
             binding.alertDialog.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.serverResponse.observe(this, Observer {
            Toast.makeText(this@CategoryManagementActivity, it.message, Toast.LENGTH_SHORT).show()
        })*/
    }

    /*private fun setupInputDialog(): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this@CategoryManagementActivity)
        dialog.setTitle("Add New Category")
        val etName = EditText(this@CategoryManagementActivity)
        dialog.setView(etName)
        dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.addNewCategory(Category(name = etName.text.toString()))
                    vm.getCategories()
                }
            }
        })
        return dialog
    }*/
}