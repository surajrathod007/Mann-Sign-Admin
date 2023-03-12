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
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductReportAdapter
import com.surajmanshal.mannsignadmin.databinding.FragmentProductReportBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ProductReportFragment : Fragment() {


    lateinit var binding: FragmentProductReportBinding
    lateinit var vm: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        if(vm.products.value.isNullOrEmpty())
        {
            vm.getPosters()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_report, container, false)
        binding = FragmentProductReportBinding.bind(view)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)
        vm.getPosters()
        setUpObservers()

        binding.btnGenerateProductReport.setOnClickListener {
            generateReport()
        }
        return binding.root
    }

    private fun setUpObservers() {
        vm.products.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.rvProductReports.adapter = ProductReportAdapter(it, requireContext())
            }
        }
        vm.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressReport.visibility = View.VISIBLE
            } else {
                binding.progressReport.visibility = View.GONE
            }
        }
    }

    fun generateReport() {

        var sales = 0f
        try {

            val p = vm.products.value!!
            val orders = vm.allOrders.value
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            val file = File(path, "product_report${System.currentTimeMillis()}.pdf")
            val output = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            document.add(
                Paragraph("Product Report Of Mann Sign").setTextAlignment(TextAlignment.CENTER)
                    .setBold().setFontSize(14f)
            )

            val table = Table(8)
            table.useAllAvailableWidth()

            //row1
            table.addCell(Cell().add(Paragraph("Product Id").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Product Code").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Title").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Type").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Sub category").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Main category").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Base price").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Total sales").setBold().setFontSize(8f)))

            p.forEach {
                with(table) {
                    addCell(it.productId.toString()).setFontSize(8f)
                    addCell("${it.productCode.toString()}").setFontSize(8f)

                    //type checking
                    if (it.bannerDetails != null) {
                        addCell(it.bannerDetails!!.text).setFontSize(8f)
                        addCell("Banner").setFontSize(8f)
                    }else if (it.posterDetails != null) {
                        addCell(it.posterDetails!!.title).setFontSize(8f)
                        addCell("Poster").setFontSize(8f)
                    } else if (it.boardDetails != null) {
                        addCell(it.boardDetails!!.text).setFontSize(8f)
                        addCell("Acp board").setFontSize(8f)
                    }else{
                        addCell("-").setFontSize(8f)
                        addCell("-").setFontSize(8f)
                    }

                    addCell(it.subCategory.toString()).setFontSize(8f)
                    addCell(it.category.toString()).setFontSize(8f)
                    addCell(it.basePrice.toString()).setFontSize(8f)
                    if (!orders.isNullOrEmpty()) {
                        var c = 0
                        orders.forEach { order ->
                            c += order.orderItems?.filter { orderItem ->
                                orderItem.product!!.productId == it.productId
                            }!!.size
                        }
                        sales += c
                        addCell("$c").setFontSize(8f)
                    } else {
                        addCell("-").setFontSize(8f)
                    }
                }
            }

            document.add(table)

            document.add(
                Paragraph("Total Products : ${vm.products.value?.size}").setFontSize(8f).setBold()
            )
            document.add(
                Paragraph("Total Products Sold : $sales").setFontSize(8f).setBold()
            )
            val d =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            document.add(
                Paragraph("Report Generated At : $d").setFontSize(8f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
            document.close()
            Toast.makeText(requireContext(), "Report Created", Toast.LENGTH_SHORT).show()
            openFile(file, path)
        } catch (e: Exception) {
            Functions.makeToast(requireContext(), "Exceptions : ${e.message}")
        }

    }

    fun openFile(file: File, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )
            intent.setData(uri)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } else {
            intent.setDataAndType(Uri.parse(path), "application/pdf")
            val i = Intent.createChooser(intent, "Open File With")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
    }

}