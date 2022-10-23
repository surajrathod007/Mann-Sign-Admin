package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentReportsBinding
import com.surajmanshal.mannsignadmin.ui.ReportDetailsActivity
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

class ReportsFragment : Fragment() {

    lateinit var binding: FragmentReportsBinding
    lateinit var vm: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        binding = FragmentReportsBinding.bind(view)

        vm.allOrders.observe(viewLifecycleOwner){
            binding.txtTotalOrders.text = it.size.toString()
            Toast.makeText(requireContext(),it.size.toString(),Toast.LENGTH_SHORT).show()
        }

        vm.transactionItems.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.txtTotalTransactions.text = it.size.toString()
                var t = 0.0f
                it.forEach {
                    t += it.transaction.amount
                }
                binding.txtTotalEarnings.text = "₹" + t
            }
        }

        vm.allUsers.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.txtTotalUsers.text = it.size.toString()
            }
        }

        vm.products.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                //its total product
                binding.txtTotalInvoice.text = it.size.toString()
            }
        }

        vm.serverResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
        }

        //card click listners
        binding.cardTransaction.setOnClickListener {
            val i = Intent(it.context, ReportDetailsActivity::class.java)
            i.putExtra("index", 1)
            startActivity(i)
        }
        binding.cardOrders.setOnClickListener {
            val i = Intent(it.context, ReportDetailsActivity::class.java)
            i.putExtra("index", 0)
            startActivity(i)
        }
        binding.cardProducts.setOnClickListener {
            val i = Intent(it.context, ReportDetailsActivity::class.java)
            i.putExtra("index", 2)
            startActivity(i)
        }
        binding.cardUsers.setOnClickListener {
            val i = Intent(it.context, ReportDetailsActivity::class.java)
            i.putExtra("index", 3)
            startActivity(i)
        }

        return binding.root
    }


}