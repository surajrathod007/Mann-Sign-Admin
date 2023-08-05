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
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.SubCategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.databinding.DialogContainerBinding
import com.surajmanshal.mannsignadmin.databinding.FragmentCategoryBinding
import com.surajmanshal.mannsignadmin.ui.activity.CategoryManagementActivity
import com.surajmanshal.mannsignadmin.utils.ResourceType
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SubCategoryFragment : CategoryFragment() {

//    override lateinit var imageUploading : ImageUploading
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        _binding = FragmentCategoryBinding.bind(view)
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        val id = arguments?.getInt("id")
        vm.getSubCategories(id)
        if (id == null) activity?.onBackPressed()
        with(binding) {
            btnCancel.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.onDeletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.deletionSubCategory.value?.let { it1 -> vm.removeSubCategory(it1) }
                }
                vm.onDeletionCancelOrDone()
            }
            btnAddCategory.setOnClickListener {
                (requireActivity() as CategoryManagementActivity).initImageUploader()
                imageUploading = (requireActivity() as CategoryManagementActivity).imageUploading
                imageUploading.imageUri.observe(viewLifecycleOwner){
                    dialogBinding?.apply {
                        ivResource.setImageURI(it)
                        tvChooseImage.hide()
                    }
                }
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("New Subcategory")
                val etName = EditText(activity)
                dialog.setView(DialogContainerBinding.inflate(layoutInflater).apply {
                    chooseImageView.show()
                    chooseImageView.setOnClickListener {
                        imageUploading.chooseImageFromGallary()
                    }
                    dialogContainer.addView(etName)
                    dialogBinding = this
                }.root)
                dialog.setPositiveButton("Add", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        imageUploading.imageUploadResponse.observe(viewLifecycleOwner) {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            if (it.success) {
                                val imgUrl = it.data as String
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.addNewSubCategory(
                                        SubCategory(
                                            mainCategoryId = id!!,
                                            name = etName.text.toString(),
                                            imgUrl = imgUrl
                                        )
                                    )
                                    vm.getSubCategories(id)
                                }
                            }
                            imageUploading.imageUploadResponse.removeObservers(viewLifecycleOwner)

                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            imageUploading.sendImage(
                                ResourceType.Subcategory,
                                imageUploading.createImageMultipart()
                            )
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
        vm.subCategories.observe(viewLifecycleOwner, Observer { it ->
//            print(it.toString())
            setAdapterWithList(it, binding.rvCategories, SubCategoryAdapter(vm) {
                setupUpdateDialog(it, id).show()
            })
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