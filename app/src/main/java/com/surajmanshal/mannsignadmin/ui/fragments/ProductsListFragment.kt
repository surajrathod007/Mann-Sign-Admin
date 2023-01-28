package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.FragmentProductsListBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel

class ProductsListFragment : Fragment() {

    private lateinit var _binding : FragmentProductsListBinding
    val binding get() = _binding
    lateinit var mVM : ProductsViewModel
    lateinit var mActivity: ProductsActivity
    var columCount = 2
    var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        mVM.products.observe(requireActivity(), Observer {
            if(binding.rvProducts.adapter == null)
                setAdapterWithList(it)
        })
        mVM.getPosters()
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

        with(binding) {
            etSearch.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(requireContext(), "$query", Toast.LENGTH_SHORT).show()
                    val resultList = getSearchedProducts(query)
                    if (resultList.isNullOrEmpty()) {
                        tvNoProducts.show()
                        rvProducts.hide()
                        return true
                    }
                    tvNoProducts.hide()
                    rvProducts.show()
                    setAdapterWithList(resultList)
                    lastQuery = query!!
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText.isNullOrBlank()){
                        tvNoProducts.hide()
                        rvProducts.show()
                        mVM.products.value?.let { setAdapterWithList(it) }
                    }
                    return false
                }
            })
            getSearchedProducts(
                lastQuery
            )?.let {
                setAdapterWithList(it)
            }
        }

        return view
    }

    fun setAdapterWithList(list: List<Product>){
        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list.reversed(),mActivity)
    }

    fun getSearchedProducts(query : String?): List<Product>? {
        return mVM.products.value?.filter {
            query?.let { it1 ->
                it.productId.toString().contains(it1) or
                it.posterDetails?.title.toString().contains(it1,true) or
                it.productCode.toString().contains(it1,true)
            } == true
        }
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