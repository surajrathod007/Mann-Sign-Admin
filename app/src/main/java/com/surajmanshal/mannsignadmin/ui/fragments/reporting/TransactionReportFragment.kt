package com.surajmanshal.mannsignadmin.ui.fragments.reporting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.TransactionAdapter
import com.surajmanshal.mannsignadmin.data.model.DateFilter
import com.surajmanshal.mannsignadmin.databinding.FragmentTransactionReportBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionReportFragment : Fragment() {

    lateinit var binding: FragmentTransactionReportBinding
    lateinit var vm: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)
        binding = FragmentTransactionReportBinding.bind(view)

        setupSpinner()

        vm.serverResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }

        vm.transactionItems.observe(viewLifecycleOwner){
            binding.rvTransaction.adapter = TransactionAdapter(it)
        }


        vm.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.transactionLoading.visibility = View.GONE
            }
            if (it) {
                binding.transactionLoading.visibility = View.VISIBLE
            }
        }

        binding.spTransactionFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> {
                            Toast.makeText(
                                requireContext(),
                                "Fetch All Transactions",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getAllTransactions()
                            }
                        }
                        1 -> {
                            val d = DateFilter(LocalDate.now().minusDays(7), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        2 -> {
                            val d = DateFilter(LocalDate.now().minusDays(30), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        3 -> {
                            val d = DateFilter(LocalDate.now().minusDays(180), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        4 -> {
                            val d = DateFilter(LocalDate.now().minusDays(365), LocalDate.now())
                            Toast.makeText(
                                requireContext(),
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.filterTransaction(d)
                            }
                        }
                        5 -> {
                            Toast.makeText(
                                requireContext(),
                                "Open Calender",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }


        binding.btnGenerateTransactionReport.setOnClickListener {
            generateReport()
        }
        return binding.root
    }

    private fun setupSpinner() {

        val all = "Lifetime"

        val last7Days = "Last 7 days (${
            LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last30Days = "Last 30 days (${
            LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last180Days = "Last 180 days (${
            LocalDate.now().minusDays(180).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last365Days = "Last 365 days (${
            LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val custom = "Custom Date"

        val arr = arrayOf(all, last7Days, last30Days, last180Days, last365Days, custom)

        val adp = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, arr)
        binding.spTransactionFilter.adapter = adp

    }


    private fun generateReport(){
        try{

            var lst = vm.transactionItems.value

            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            val file = File(path,"order_report${System.currentTimeMillis()}.pdf")
            val output = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            document.add(Paragraph("Transaction Report Of Mann Sign").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(14f))

            val table = Table(7)
            table.useAllAvailableWidth()

            //row1
            table.addCell(Cell().add(Paragraph("Transaction Id").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Date").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Email Id").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Order Id").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Mode").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Delivery Charge").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Amount").setBold().setFontSize(8f)))

            lst!!.forEach {
                with(table){
                    addCell(it.transaction.transactionId).setFontSize(8f)
                    addCell(it.transaction.date.toString()).setFontSize(8f)
                    addCell(it.transaction.emailId).setFontSize(8f)
                    addCell(it.transaction.orderId).setFontSize(8f)
                    addCell(it.transaction.mode.toString()).setFontSize(8f) //TODO : It Need to check
                    addCell(it.transaction.deliveryCharge.toString()).setFontSize(8f)
                    addCell(it.transaction.amount.toString()).setFontSize(8f)
                }
            }

            document.add(table)
            document.add(Paragraph("Total Transactions : ${lst.size}").setFontSize(8f).setBold())
            val d = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            document.add(
                Paragraph("Report Generated At : $d").setFontSize(8f).setTextAlignment(
                    TextAlignment.RIGHT))
            document.close()
            Toast.makeText(requireContext(),"Report Created",Toast.LENGTH_SHORT).show()
            openFile(file,path)

        }catch (e : Exception){
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }

    }

    fun openFile(file : File, path : String){
        val intent = Intent(Intent.ACTION_VIEW)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            val uri = FileProvider.getUriForFile(requireContext(),requireContext().packageName+".provider",file)
            intent.setData(uri)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }else{
            intent.setDataAndType(Uri.parse(path),"application/pdf")
            val i = Intent.createChooser(intent,"Open File With")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
    }



}