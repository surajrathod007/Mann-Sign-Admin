package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.FragmentProductsListBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel

class ProductsListFragment : Fragment() {

    private lateinit var _binding : FragmentProductsListBinding
    val binding get() = _binding
    lateinit var mVM : ProductsViewModel
    lateinit var mActivity: ProductsActivity
    var columCount = 2

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
        val view = inflater.inflate(R.layout.fragment_products_list, container, false)
        _binding = FragmentProductsListBinding.bind(view)
        binding.rvProducts.layoutManager = GridLayoutManager(activity,columCount)

        // Views Setup
        with(binding){
            btnAddProduct.setOnClickListener {
                startActivity(Intent(activity, ProductManagementActivity::class.java))
                activity?.finish()
            }
        }
        mVM.products.observe(viewLifecycleOwner, Observer {
            setAdapterWithList(it)
        })
        mVM.getPosters()
        return view
    }

    fun setAdapterWithList(list: List<Product>){
        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list.reversed(),mActivity)
    }


    companion object {

        @JvmStatic
        fun newInstance(vm : ProductsViewModel,activity: ProductsActivity) =
            ProductsListFragment().apply {
                arguments = Bundle().apply {
                   mVM = vm
                    mActivity = activity
                }
            }
    }
}