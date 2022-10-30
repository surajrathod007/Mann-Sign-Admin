package com.surajmanshal.mannsignadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductManagementBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel


class ProductDetailsFragment : Fragment() {

    private lateinit var _binding : ActivityProductManagementBinding
    val binding get() = _binding
    lateinit var mVM : ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_product_management, container, false)
        _binding = ActivityProductManagementBinding.bind(view)

        mVM._currentProduct.value?.let {
            with(binding){
            // Calls for resources -------------------------------------------------------
            with(mVM){

                getCategoryById(it.category!!)
                getSubCategoryById(it.subCategory!!)
                it.languages?.forEach { getLanguageById(it) }
                it.materials?.forEach { getMaterialById(it) }
                _currentProductCategory.observe(viewLifecycleOwner, Observer {
                    setupCategoryView(it.name)
                })
                _currentProductSubCategory.observe(viewLifecycleOwner, Observer {
                    setupSubCategoryView(it.name)
                })
                _currentProductMaterial.observe(viewLifecycleOwner, Observer {
                    setupMaterialViews(it.name)
                })
                _currentProductLanguage.observe(viewLifecycleOwner, Observer {
                    setupLanguageViews(it.name)
                })
            }

            // Set up Views  ----------------------------------------------------------------

                with(Functions){
                    Glide.with(this@ProductDetailsFragment)
                        .load(urlMaker(it.images!![0].url)).into(ivProduct)
                    makeViewVisible(tvSubCategory)
                    makeViewGone(categorySpinner)
                    makeViewGone(btnAddProduct)
                    makeViewVisible(tvBasePrice)
                }
                makeETDisableAndSetText(etTitle,it.posterDetails!!.title)
                makeETDisableAndSetText(etShortDescription,it.posterDetails!!.short_desc)
                it.posterDetails!!.long_desc?.let { makeETDisableAndSetText(etLongDescription, it) }
                tvBasePrice.text = "${tvBasePrice.text} ${it.basePrice}"
                it.sizes?.forEach { setupSizesViews(it) }
            }
        }

        return binding.root
    }

    private fun setupCategoryView(name: String) {
        binding.tvCategory.text = binding.tvCategory.text.toString() + name
    }

    private fun setupSubCategoryView(name: String) {
        binding.tvSubCategory.text = binding.tvSubCategory.text.toString() + name
    }

    private fun createTextView(text : String): TextView {
        val textView = TextView(activity)
        textView.text = text
        return textView
    }

    fun setupSizesViews(size: Size) = binding.gvSizes.addView(createTextView("${size.width} x ${size.height}"))

    fun setupMaterialViews(name : String) = binding.gvMaterials.addView(createTextView(name))

    fun setupLanguageViews(name : String) = binding.gvLanguages.addView(createTextView(name))

    fun makeETDisableAndSetText(et : EditText,text : String){
        et.isEnabled = false
        et.setText(text)
    }

    companion object {

        @JvmStatic
        fun newInstance(vm: ProductsViewModel) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    mVM = vm
                }
            }
    }
}