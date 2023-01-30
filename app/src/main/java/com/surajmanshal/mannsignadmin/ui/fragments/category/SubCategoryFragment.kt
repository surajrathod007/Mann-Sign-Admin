package com.surajmanshal.mannsignadmin.ui.fragments.category

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
import com.surajmanshal.mannsignadmin.adapter.recyclerView.SubCategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.databinding.FragmentCategoryBinding
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SubCategoryFragment : CategoryFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[CategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        _binding = FragmentCategoryBinding.bind(view)
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        val id = arguments?.getInt("id")
        vm.getSubCategories(id)
        if(id==null) activity?.onBackPressed()
        with(binding){
            btnCancel.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch{
                    vm.deletionSubCategory.value?.let { it1 -> vm.removeSubCategory(it1) }
                }
                vm.onDeletionCancelOrDone()
            }
            btnAddCategory.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("New Subcategory")
                val etName = EditText(activity)
                dialog.setView(etName)
                dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        CoroutineScope(Dispatchers.IO).launch {
                            vm.addNewSubCategory(SubCategory(mainCategoryId = id!!, name = etName.text.toString()))
                            vm.getSubCategories(id)
                        }
                    }
                })
                dialog.show()
            }
            toolbar.apply {
                ivBack.setOnClickListener { activity?.onBackPressed() }
                ivAction.hide()
                tvToolBarTitle.text = "Subcategories"
            }
        }
        vm.subCategories.observe(viewLifecycleOwner, Observer {
            setAdapterWithList(it,binding.rvCategories, SubCategoryAdapter(vm))
        })
        vm.isDeleting.observe(viewLifecycleOwner, Observer {
            binding.alertDialog.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.serverResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        })
        return binding.root
    }

}