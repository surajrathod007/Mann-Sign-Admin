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
    private var mProduct = Product(0)

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
        with(binding){
            with(Functions){
                makeViewVisible(tvSubCategory)
                makeViewGone(categorySpinner)
                makeViewGone(btnAddProduct)
                makeViewVisible(tvBasePrice)
            }
            makeETDisableAndSetText(etTitle,mProduct.posterDetails!!.title)
            makeETDisableAndSetText(etShortDescription,mProduct.posterDetails!!.short_desc)
            mProduct.posterDetails!!.long_desc?.let { makeETDisableAndSetText(etLongDescription, it) }
            tvCategory.text // call get Category by id and set it
            tvSubCategory.text // call get SubCategory by id and set it
            tvBasePrice.text = "${tvBasePrice.text} ${mProduct.basePrice}"
        }
        mProduct.sizes?.forEach {
            setupSizesViews(it)
        }
        mProduct.languages?.forEach {
            // todo : call get language by id and set it
        }
        mProduct.materials?.forEach {
            // todo : call get material by id and set it
        }
        return binding.root
    }

    private fun createTextView(text : String): TextView {
        val textView = TextView(activity)
        textView.text = text
        return textView
    }

    fun setupSizesViews(size: Size) = binding.gvSizes.addView(createTextView("${size.width} x ${size.height}"))

    fun setupMaterialViews(name : String) = binding.gvMaterials.addView(createTextView(name))

    fun setupLanguageViews(name : String) = binding.gvLanguages.addView(createTextView(name))

    fun setupSubcategoryViews(name : String) = binding.tvSubCategory.setText(name)

    fun makeETDisableAndSetText(et : EditText,text : String){
        et.isEnabled = false
        et.setText(text)
    }

    companion object {

        @JvmStatic
        fun newInstance(vm: ProductsViewModel, product: Product, productsActivity: ProductsActivity) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    mVM = vm
                    mProduct = product
                }
            }
    }
}