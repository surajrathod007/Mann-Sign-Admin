package com.surajmanshal.mannsignadmin.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.surajmanshal.mannsignadmin.adapter.recyclerView.OrderItemsAdapter
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.data.model.ordering.OrderItem
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsignadmin.utils.getTwoDecimalValue
import com.surajmanshal.mannsignadmin.utils.itext.UsecaseGenerateInvoice
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

class OrderDetailsActivity : AppCompatActivity() {

    //lateinit var spinner: Spinner
    lateinit var paymentStatusSpinner: Spinner
    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var vm: OrdersViewModel
    var status = 0  //this is not in use
    var orderStatus = -1
    lateinit var order: Order

    companion object{
        val statuses = arrayOf("Pending","Confirmed","Processing","Ready","Out for delivery","Delivered","Canceled")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val i = intent
        status = i.getIntExtra("status", 0)
        order = i.getSerializableExtra("order") as Order

        vm.getOrderById(order.orderId)
        setContentView(binding.root)
        //spinner = findViewById(R.id.spOrderStatus)
        paymentStatusSpinner = findViewById(R.id.spOrderPaymentStatus)

        CoroutineScope(Dispatchers.IO).launch {
            vm.fetchUserByEmail(order.emailId)
        }
        //setupSpinner()
        setUpPaymentStatus()
        //setupOrderDetails(order)
        //setupOrderItems(order.orderItems)
        //setupUpdateButton(order.orderStatus)

        vm.serverResponse.observe(this) {
            Toast.makeText(this@OrderDetailsActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

        vm.user.observe(this) {
            with(binding) {
                txtCustomerEmail.text = it.emailId
                txtCustomerFirstName.text = it.firstName
                txtCustomerLastName.text = it.lastName
                txtCustomerPhoneNumber.text = it.phoneNumber
                txtCustomerAddress.text = it.address
                txtCustomerPinCode.text = it.pinCode.toString()
            }
        }

        vm.msg.observe(this){
            Functions.makeToast(this,it)
        }

        vm.order.observe(this){
            setupOrderDetails(it)
            setupOrderItems(it.orderItems)
            setupUpdateButton(it.orderStatus)
        }

        binding.btnUpdateOrder.setOnClickListener {

            if(binding.edEstimatedDays.text.toString() == "0" || binding.edEstimatedDays.text.toString().isNullOrEmpty()){
                Toast.makeText(this,"Please enter the estimated days !",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(orderStatus == Constants.ORDER_OUT_FOR_DELIVERY && binding.edTrackingUrl.text.toString().isNullOrEmpty())
            {
                Toast.makeText(this,"Please Provide The Tracking Url ! !",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showConfirmDialog()
        }

        binding.btnGenerateInvoice.setOnClickListener {
//            makeInvoice()
            vm.user.value?.let { it1 -> UsecaseGenerateInvoice(this).invoke(order, it1) }?: kotlin.run {
                Toast.makeText(this, "please try after few minutes", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnProductBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnOrderChat.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val email = getStringPreferences(Constants.DATASTORE_EMAIL)
                withContext(Dispatchers.Main){
                    val i = Intent(this@OrderDetailsActivity,ChatActivity::class.java)
                    i.putExtra("id",order.orderId)
                    i.putExtra("email",email)
                    startActivity(i)
                }
            }
        }


    }

    suspend fun getStringPreferences(key : String) : String? {
        val data = preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }


    fun setupUpdateButton(s: Int) {
        when (s) {

            Constants.ORDER_PENDING -> {
                orderStatus = Constants.ORDER_CONFIRMED
                binding.btnUpdateOrder.text = "Confirm Order"
            }

            Constants.ORDER_CONFIRMED -> {
                orderStatus = Constants.ORDER_PROCCESSING
                binding.btnUpdateOrder.text = "Start Proccesing"
            }

            Constants.ORDER_PROCCESSING -> {
                orderStatus = Constants.ORDER_READY
                binding.btnUpdateOrder.text = "Make Order Ready"
            }

            Constants.ORDER_READY -> {
                orderStatus = Constants.ORDER_OUT_FOR_DELIVERY
                binding.btnUpdateOrder.text = "Make Order Out For Delivery"
            }

            Constants.ORDER_OUT_FOR_DELIVERY -> {
                orderStatus = Constants.ORDER_DELIVERED
                binding.btnUpdateOrder.text = "Make order delivered"
                //binding.btnUpdateOrder.isEnabled = false
            }

            Constants.ORDER_DELIVERED -> {
                orderStatus = Constants.ORDER_CANCELED
                binding.btnUpdateOrder.text = "Order Delivered !"
                binding.btnUpdateOrder.isEnabled = false
            }

            Constants.ORDER_CANCELED -> {
                orderStatus = Constants.ORDER_CANCELED
                binding.btnUpdateOrder.text = "Order canceled !"
                binding.btnUpdateOrder.isEnabled = false
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun showConfirmDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure ?")
        builder.setMessage("After setting order to \"${statuses.get(orderStatus)}\" it can not be undone...")
        builder.setPositiveButton("Change Status"){dialog,which ->

            val o = vm.order.value
            order.also {
                it.orderStatus = orderStatus
                //it.paymentStatus = paymentStatusSpinner.selectedItemPosition
                if (binding.edEstimatedDays.text.isNullOrEmpty()) {
                    it.days = 0
                } else {
                    it.days = Integer.parseInt(binding.edEstimatedDays.text.toString())
                }
                it.trackingUrl = binding.edTrackingUrl.text.toString()
            }
            CoroutineScope(Dispatchers.IO).launch {
                vm.updateOrder(order)
            }

        }
        builder.setNegativeButton("Cancel"){_,_->
            Toast.makeText(this,"Action Canceled",Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun setupOrderItems(orderItems: List<OrderItem>?) {
        binding.rvOrderItems.adapter = OrderItemsAdapter(this@OrderDetailsActivity, orderItems!!)
    }

    private fun setupOrderDetails(order: Order) {

        with(binding) {
            txtOrderIdDetails.text = order.orderId
            txtOrderDateDetails.text = order.orderDate.toString()
            txtOrderQuantityDetails.text = order.quantity.toString()
            txtOrderTotalDetails.text = "₹" + order.total.toString()
            if (order.days != null)
                edEstimatedDays.setText(order.days.toString())
            if (!order.trackingUrl.isNullOrEmpty()) {
                edTrackingUrl.setText(order.trackingUrl)
            }
            paymentStatusSpinner.setSelection(order.paymentStatus)
            //spinner.setSelection(order.orderStatus)
        }
    }

    private fun setUpPaymentStatus() {
        val adp = ArrayAdapter(
            this@OrderDetailsActivity,
            R.layout.custom_spinner_item,
            resources.getStringArray(R.array.order_payment_status)
        )
        paymentStatusSpinner.adapter = adp
    }

    private fun setupSpinner() {
        val adp = ArrayAdapter(
            this@OrderDetailsActivity,
            R.layout.custom_spinner_item,
            resources.getStringArray(R.array.order_status_array)
        )
        //spinner.adapter = adp

    }

    fun makeInvoice() {

        try {

            var lst = order.orderItems

//            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            val path = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.toString()
            }else{
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            }
            val file = File(path, "mann_invoice${System.currentTimeMillis()}.pdf")
            val output = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)
            //document.setMargins(1f,1f,1f,1f)


            //header
            val headerImg = this.getDrawable(R.drawable.invoice_header)
            val bitmap = (headerImg as BitmapDrawable).bitmap
            val opstream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, opstream)
            val bitmapdata = opstream.toByteArray()

            val img = ImageDataFactory.create(bitmapdata)
            val myheader = Image(img)
            document.add(myheader)


            //first table
            val c: FloatArray = floatArrayOf(220F, 220F, 200F)
            val table1 = Table(c)


            // row 1
            table1.addCell(Cell(0,1).add(Paragraph("Bill to Party ").setFontSize(10.0f)).setBold())
            //table1.addCell(Cell().add(Paragraph("").setFontSize(10.0f)).setBold())
            table1.addCell(Cell().add(Paragraph("Date : ").setFontSize(10.0f)).setBold())
            val today = Functions.getFormatedTimestamp(System.currentTimeMillis(),"dd-MM-yyyy")
            table1.addCell(
                Cell().add(
                    Paragraph(today?:LocalDate.now().toString()).setFontSize(10.0f).setBold()
                )
            )

            //row 2
            table1.addCell(Cell(4, 0).add(Paragraph("857,indiranagar -2 ").setFontSize(8.0f)))
            //table1.addCell(Cell(4, 0).add(Paragraph("").setFontSize(8.0f)))
            table1.addCell(Cell().add(Paragraph("Invoice No : ").setFontSize(10.0f)))
//            table1.addCell(Cell().add(Paragraph("${order.orderId}").setFontSize(10.0f)))
            val invoiceNo = 1
            val invoiceId = Functions.getFinancialYearString(LocalDate.now()) + "-MS-$invoiceNo"
            table1.addCell(Cell().add(Paragraph(invoiceId).setFontSize(10.0f)))

            //row 3
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("Order No. :").setFontSize(10.0f)))
            table1.addCell(Cell().add(Paragraph(order.orderId).setFontSize(10.0f)))

            //row 4
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("Order Date : ").setFontSize(10.0f)))
            table1.addCell(
                Cell().add(
                    Paragraph(
                        Functions.getFormatedTimestamp(order.orderDate.toEpochDay(),"dd-MM-yyyy")
                    ).setFontSize(10.0f)
                )
            )

            //row 5
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("Pin Code : ").setFontSize(10.0f)))
            table1.addCell(Cell().add(Paragraph("24").setFontSize(10.0f)))

            //row 6
            table1.addCell(Cell().add(Paragraph("Pin Code : ").setFontSize(8.0f).setBold()))
            //table1.addCell(Cell().add(Paragraph("").setFontSize(8.0f).setBold()))
            table1.addCell(
                Cell(0, 2).add(Paragraph("COMPANY GSTIN NO : ").setFontSize(8.0f).setBold())
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
            )
            //table1.addCell(Cell().add(Paragraph("")))

            //row 7
            table1.addCell(
                Cell().add(
                    Paragraph("GSTIN NO : ").setFontSize(8.0f).setBold()
                )
            )
//            table1.addCell(
//                Cell().add(
//                    Paragraph("GSTIN NO : <add here>").setFontSize(8.0f).setBold()
//                )
//            )
            table1.addCell(
                Cell(0, 2).add(Paragraph("24BENPP0006B1Z4").setFontSize(8.0f).setBold())
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
            )
            //table1.addCell(Cell().add(Paragraph("")))

            document.add(table1)

            //2nd table
            val table2 = Table(6)
            table2.useAllAvailableWidth()
            //row1
            table2.addCell(Cell().add(Paragraph("Sr No.").setBold().setFontSize(10.0f))).setTextAlignment(TextAlignment.CENTER)
            table2.addCell(
                Cell().add(
                    Paragraph("Product Description").setBold().setFontSize(10.0f)
                ).setTextAlignment(TextAlignment.CENTER)
            )
            table2.addCell(Cell().add(Paragraph("HSN Code").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
            //table2.addCell(Cell().add(Paragraph("UOM").setBold().setFontSize(10.0f)))
            //table2.addCell(Cell().add(Paragraph("Product Type").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("Quantity").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
            table2.addCell(Cell().add(Paragraph("Rate").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
            table2.addCell(Cell().add(Paragraph("Amount").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))

            //add items
            var sr = 1
            var gtotal = 0.0f
            var extraRows = 15 - lst!!.size
            with(table2) {
                lst!!.forEach {
                    addCell(sr.toString()).setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                    addCell(it.product!!.posterDetails!!.title).setFontSize(10f)
                    addCell("Hsn$sr").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)

                    /*
                    addCell("UOM$sr").setFontSize(10f)
                    //set product type
                    if (it.product!!.posterDetails != null)
                        addCell("Poster").setFontSize(10f)
                    if (it.product!!.boardDetails != null)
                        addCell("ACP Board").setFontSize(10f)
                    if (it.product!!.bannerDetails != null)
                        addCell("Banner").setFontSize(10f)

                     */

                    addCell("${it.quantity}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                    addCell("${it.variant!!.variantPrice}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                    addCell("${it.totalPrice.getTwoDecimalValue()}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                    gtotal += it.totalPrice
                    sr++
                }

                for (i in 1..extraRows) {
                    addCell(sr.toString()).setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    //addCell("").setFontSize(10f)
                    //addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    sr++
                }
            }

            val cgst = (gtotal * 9) / 100
            val sgst = (gtotal * 9) / 100

            document.add(table2)

            val col = floatArrayOf(200f, 200f, 200f, 180f)
            val table3 = Table(col)

            //bar
            val barImg = this.getDrawable(R.drawable.upi_barcode)
            val bitmap1 = (barImg as BitmapDrawable).bitmap
            val opstream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, opstream1)
            val bitmapdata1 = opstream1.toByteArray()

            var img1 = ImageDataFactory.create(bitmapdata1)

            var upibar = Image(img1)
            upibar.setHorizontalAlignment(HorizontalAlignment.CENTER)
            upibar.setHeight(80f)
            upibar.setWidth(80f)

            //row1
            table3.addCell(
                Cell().add(
                    Paragraph("Bank Detail").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Total Amount before Tax : ").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph(gtotal.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row2
            table3.addCell(
                Cell().add(
                    Paragraph("Bank : Bank of Maharashtra").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
            )
            table3.addCell(
                Cell(5, 0).add(upibar).setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(
                        VerticalAlignment.MIDDLE
                    )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Add: CGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph(cgst.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row3
            table3.addCell(Cell().add(Paragraph("Bank A/C : 60207512779\n").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Add: SGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph(sgst.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row4
            table3.addCell(Cell().add(Paragraph("Bank IFSC : MAHB0001632").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Total Tax Amount : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph((cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row5
            table3.addCell(Cell().add(Paragraph("PAN No. : BENPP0006B").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Total Amount after Tax  ₹").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph((gtotal + cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )

            //row6
            table3.addCell(Cell().add(Paragraph("").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Transportation Charge : ").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row7
            table3.addCell(Cell().add(Paragraph("").setFontSize(10f)))
            table3.addCell(
                Cell().add(
                    Paragraph("UPI ID: 7405736990@okbizaxis").setFontSize(8f).setBold()
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Grand Total : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph((gtotal + cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )

            document.add(table3)

            //last table
            val table4 = Table(floatArrayOf(300f, 100f, 100f, 100f))

            //bar
            val stampImg = this.getDrawable(R.drawable.stamp)
            val bitmap2 = (stampImg as BitmapDrawable).bitmap
            val opstream2 = ByteArrayOutputStream()
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, opstream2)
            val bitmapdata2 = opstream2.toByteArray()

            var img2 = ImageDataFactory.create(bitmapdata2)
            var stamp = Image(img2)
            stamp.setHorizontalAlignment(HorizontalAlignment.CENTER)
            stamp.setHeight(80f)
            stamp.setWidth(80f)


            //row1
            table4.addCell(Cell(2, 0).add(Paragraph("Rupees :").setFontSize(10f)))
            table4.addCell(Cell(6, 0).add(Paragraph("  ").setFontSize(10f)))
            table4.addCell(Cell(6, 0).add(stamp))
            table4.addCell(
                Cell(5, 0).add(
                    Paragraph("for MANN SIGN:").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            //row2
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row3
            table4.addCell(Cell().add(Paragraph("** E. & O. E.").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row4
            table4.addCell(
                Cell(3, 0).add(
                    Paragraph("SUBJECT TO AHMEDABAD JURISDICTION\nThis is a Computer Generated Invoice").setFontSize(
                        10f
                    ).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row5
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row6
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            table4.addCell(
                Cell().add(
                    Paragraph("Authorised Signatory ").setVerticalAlignment(
                        VerticalAlignment.BOTTOM
                    ).setFontSize(8f).setTextAlignment(TextAlignment.CENTER)
                )
            ).setVerticalAlignment(
                VerticalAlignment.BOTTOM
            )



            document.add(table4)
            document.close()
            Toast.makeText(this, "Pdf Created", Toast.LENGTH_SHORT).show()
            if (path != null) {
                openFile(file, path)
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    fun openFile(file: File, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)
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