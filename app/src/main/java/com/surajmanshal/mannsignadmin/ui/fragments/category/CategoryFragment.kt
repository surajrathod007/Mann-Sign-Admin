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
import com.surajmanshal.mannsignadmin.adapter.recyclerView.CategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.databinding.DialogContainerBinding
import com.surajmanshal.mannsignadmin.databinding.FragmentCategoryBinding
import com.surajmanshal.mannsignadmin.ui.activity.CategoryManagementActivity
import com.surajmanshal.mannsignadmin.ui.fragments.AdapterFragment
import com.surajmanshal.mannsignadmin.utils.ResourceType
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class CategoryFragment : AdapterFragment() {

    protected lateinit var _binding: FragmentCategoryBinding
    val binding get() = _binding
    lateinit var vm: CategoryViewModel
    val imageUploading by lazy {
        (requireActivity() as CategoryManagementActivity).imageUploading
    }
    var dialogBinding: DialogContainerBinding? = null

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


        with(binding) {
            btnCancel.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.deletionCategory.value?.let { it1 -> vm.deleteFromDB(it1) }
                }
                vm.onDeletionCancelOrDone()
            }
            btnAddCategory.setOnClickListener {
                setupInputDialog().show()
            }
            toolbar.apply {
                ivBack.setOnClickListener { activity?.onBackPressed() }
                ivAction.hide()
                tvToolBarTitle.text = "Categories"
            }
        }
        vm.categories.observe(viewLifecycleOwner, Observer { it ->
            setAdapterWithList(it, binding.rvCategories, CategoryAdapter(vm) {
                setupUpdateDialog(it as Category).show()
            })
        })
        vm.isDeleting.observe(viewLifecycleOwner, Observer {
            binding.alertDialog.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.serverResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        })

        imageUploading.imageUri.observe(viewLifecycleOwner) {
            dialogBinding?.apply {
                ivResource.setImageURI(it)
                tvChooseImage.hide()
            }
        }
        return binding.root
    }

    protected fun setupInputDialog(): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Add New Category")
        val etName = EditText(requireContext())
        dialogBinding = DialogContainerBinding.inflate(layoutInflater).apply {
            chooseImageView.show()
            chooseImageView.setOnClickListener {
                imageUploading.chooseImageFromGallary()
            }
            root.addView(etName)
            dialog.setView(root)
            dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    imageUploading.imageUploadResponse.observe(viewLifecycleOwner) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        if (it.success) {
                            val imgUrl = it.data as String
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.addNewCategory(
                                    Category(
                                        name = etName.text.toString(),
                                        imgUrl = imgUrl
                                    )
                                )
                                vm.getCategories()
                            }
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        imageUploading.sendImage(
                            ResourceType.Category,
                            imageUploading.createImageMultipart()
                        )
                    }
                }
            })
        }

        return dialog
    }

    protected fun setupUpdateDialog(category: Any, categoryId: Int? = null): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(requireContext())
        val etName = EditText(requireContext())
        dialog.setTitle(
            "Update ${
                when (category) {
                    is Category -> {
                        etName.setText(category.name)
                        "Category"
                    }

                    is SubCategory -> {
                        etName.setText(category.name)
                        "Subcategory"
                    }

                    else -> {
                        ""
                    }
                }
            }"
        )
        dialog.setView(DialogContainerBinding.inflate(layoutInflater).also {
            it.chooseImageView.hide()
            it.dialogContainer.addView(etName)
        }.root)
        dialog.setPositiveButton("Update", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (category is Category) {
                        vm.updateCategory(category.apply { name = etName.text.toString() })
                        vm.getCategories()
                    } else if (category is SubCategory) {
                        vm.updateSubcategory(category.apply { name = etName.text.toString() })
                        vm.getSubCategories(categoryId)
                    }
                }
            }
        })
        return dialog
    }

}