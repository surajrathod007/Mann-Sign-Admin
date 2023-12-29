package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.FragmentProductsListBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductManagementActivity
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsListFragment : Fragment() {

    private lateinit var _binding: FragmentProductsListBinding
    val binding get() = _binding
    lateinit var mVM: ProductsViewModel
    lateinit var parent: ProductsActivity

    var columCount = 2
    var lastQuery = ""

    val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter(activity = parent)
    }

    val searchAdapter: ProductsAdapter by lazy {
        ProductsAdapter(activity = parent)
    }

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
        parent = requireActivity() as ProductsActivity

        try {
            binding.rvProducts.layoutManager = GridLayoutManager(parent, columCount)
        }catch (e : IllegalArgumentException){

        }
        binding.rvProducts.adapter = productsAdapter
        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // For pagination
                    val visibleCount = binding.rvProducts.layoutManager!!.childCount
                    val totalCount = binding.rvProducts.layoutManager!!.itemCount
                    val pastCount =
                        (binding.rvProducts.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    val threshHold = visibleCount + pastCount + 8
                    if (threshHold >= totalCount && !mVM.isLoading) {
                        parent.getMorePosters()
                    }
                }
            }
        })
        parent.vm.products.observe(requireActivity(), Observer {
            if (binding.rvProducts.adapter == null) {
                binding.rvProducts.adapter = productsAdapter
            }
            productsAdapter.submitData(it)
        })
        // Views Setup
        with(binding) {
            btnAddProduct.setOnClickListener {
//                startActivity(Intent(requireContext(), ProductManagementActivity::class.java))
                requireActivity().startActivityForResult(Intent(requireContext(), ProductManagementActivity::class.java),1020)
//                requireActivity().finish()
            }
        }

        with(binding) {
            etSearch.setOnQueryTextFocusChangeListener { view, b ->
                if (binding.rvSearchRes.adapter  == null){
                    binding.rvSearchRes.layoutManager = GridLayoutManager(parent, columCount)
                    binding.rvSearchRes.adapter = searchAdapter
                }
                rvProducts.isVisible = !b
                rvProducts.isVisible = etSearch.query.isBlank()
                rvSearchRes.isVisible = !etSearch.query.isNullOrBlank() || b
            }
            etSearch.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(requireContext(), "$query", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        onQuerySubmit(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()) {
                        tvNoProducts.hide()
                        rvSearchRes.hide()
                        rvProducts.show()
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
        parent.getMorePosters()
        return view
    }

    private suspend fun onQuerySubmit(query: String?) {
        binding.loaderLayout.show()
        val resultList = withContext(Dispatchers.IO) {
            parent.vm.searchProducts(query)
        }
        withContext(Dispatchers.Main) {
            if (resultList.isNullOrEmpty()) {
                binding.tvNoProducts.show()
                binding.rvSearchRes.hide()
                return@withContext
            }
            binding.tvNoProducts.hide()
            binding.rvSearchRes.show()
            binding.loaderLayout.hide()
            searchAdapter.changeDate(resultList)
        }
        lastQuery = query!!
    }

    fun setAdapterWithList(list: List<Product>) {
//        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list.toMutableList()/*.reversed()*/,parent)
    }

    fun getSearchedProducts(query: String?): List<Product>? {
        if (query.isNullOrEmpty()) {
            return null // No need to filter if query is empty
        }
        return productsAdapter.productList.filter {

            if (it.productId.toString().lowercase().contains(query)) return@filter true

            if (it.posterDetails?.title?.lowercase()?.contains(query , true) == true) return@filter true

            if (it.productCode.toString().lowercase().contains(query , true)) return@filter true

            false // No match found
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(vm: ProductsViewModel, activity: ProductsActivity) =
            ProductsListFragment().apply {
                arguments = Bundle().apply {
                    mVM = vm
//                    parent = activity
                }
            }
    }
}