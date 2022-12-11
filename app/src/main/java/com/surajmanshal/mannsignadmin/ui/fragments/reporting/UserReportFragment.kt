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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.UserAdapter
import com.surajmanshal.mannsignadmin.data.model.auth.User
import com.surajmanshal.mannsignadmin.databinding.FragmentUserReportBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserReportFragment : Fragment() {

    lateinit var binding: FragmentUserReportBinding
    lateinit var vm: StatsViewModel
    lateinit var bottomSheetDialog: BottomSheetDialog
    var o = ""

    override fun onResume() {
        super.onResume()
        vm.getAllOrders()
        vm.getAllUsers()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)

        vm.getAllUsers()
        vm.getAllOrders()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_report, container, false)
        binding = FragmentUserReportBinding.bind(view)


        vm.allUsers.observe(viewLifecycleOwner) {
            binding.rvUsers.adapter = UserAdapter(it, vm)
        }

        vm.user.observe(viewLifecycleOwner) {
            showBottomSheet(it)
        }

        binding.btnGenerateUserReport.setOnClickListener {
            generateReport()
        }

        return binding.root
    }

    fun showBottomSheet(u: User) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val sheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.user_detail_bottomsheet, null)

        val userName = sheetView.findViewById<TextView>(R.id.txtUserName)
        val userEmail = sheetView.findViewById<TextView>(R.id.txtUserEmail)
        val userPhone = sheetView.findViewById<TextView>(R.id.txtUserPhoneNumber)
        val address = sheetView.findViewById<TextView>(R.id.txtUserAddress)
        val pin = sheetView.findViewById<TextView>(R.id.txtUserPinCode)
        val profile =
            sheetView.findViewById<ImageView>(R.id.imgUserProfile) //todo : set profile picture
        val orders = sheetView.findViewById<TextView>(R.id.txtUserOrders)
        val btnClose = sheetView.findViewById<ImageView>(R.id.btnCloseUserProfile)

        userName.text = u.firstName + " " + u.lastName
        userEmail.text = u.emailId
        userPhone.text = u.phoneNumber
        address.text = u.address
        pin.text = u.pinCode.toString()

        btnClose.setOnClickListener {
            bottomSheetDialog.hide()
        }

        o = vm.allOrders.value!!.filter { it.emailId == u.emailId }.size.toString()
        if (!o.isNullOrEmpty())
            orders.text = o


        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    private fun generateReport() {
        try {

            var lst = vm.allUsers.value

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            val file = File(path, "user_report${System.currentTimeMillis()}.pdf")
            val output = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            document.add(
                Paragraph("Users Report Of Mann Sign").setTextAlignment(TextAlignment.CENTER)
                    .setBold().setFontSize(14f)
            )

            val table = Table(7)
            table.useAllAvailableWidth()

            //row1
            table.addCell(Cell().add(Paragraph("Email Id").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("First Name").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Last Name").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Phone Number").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Address").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Pin Code").setBold().setFontSize(8f)))
            table.addCell(Cell().add(Paragraph("Recieved Orders").setBold().setFontSize(8f)))

            lst!!.forEach {
                with(table) {
                    addCell(it.emailId).setFontSize(8f)
                    addCell(it.firstName).setFontSize(8f)
                    addCell(it.lastName).setFontSize(8f)
                    addCell(it.phoneNumber).setFontSize(8f)
                    addCell(it.address).setFontSize(6f)
                    addCell(it.pinCode.toString()).setFontSize(8f)
                    o = vm.allOrders.value!!.filter { i -> i.emailId == it.emailId }.size.toString()
                    if (!o.isNullOrEmpty()) {
                        addCell(o)
                    } else {
                        addCell("0")
                    }


                }
            }

            document.add(table)
            document.add(Paragraph("Total Users : ${lst.size}").setFontSize(8f).setBold())
            val d =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            document.add(
                Paragraph("Report Generated At : $d").setFontSize(8f).setTextAlignment(
                    TextAlignment.RIGHT
                )
            )
            document.close()
            Toast.makeText(requireContext(), "Report Created", Toast.LENGTH_SHORT).show()
            openFile(file, path)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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