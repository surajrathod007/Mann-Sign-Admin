package com.surajmanshal.mannsignadmin.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.CategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.databinding.FragmentCategoryBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class CategoryFragment : AdapterFragment() {

    protected lateinit var _binding : FragmentCategoryBinding
    val binding get() = _binding
    lateinit var vm : CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[CategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        _binding = FragmentCategoryBinding.bind(view)
        vm.getCategories()
        binding.rvCategories.layoutManager = LinearLayoutManager(activity)


        with(binding){
            btnCancel.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch{
                    vm.deletionCategory.value?.let { it1 -> vm.deleteFromDB(it1) }
                }
                vm.onDeletionCancelOrDone()
            }
            btnAddCategory.setOnClickListener {
                setupInputDialog().show()
            }
        }
        vm.categories.observe(viewLifecycleOwner, Observer {
            setAdapterWithList(it,binding.rvCategories, CategoryAdapter(vm))
        })
        vm.isDeleting.observe(viewLifecycleOwner, Observer {
            binding.alertDialog.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.serverResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        })
        return binding.root
    }
    protected fun setupInputDialog(): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Add New Category")
        val etName = EditText(activity)
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
    }

}