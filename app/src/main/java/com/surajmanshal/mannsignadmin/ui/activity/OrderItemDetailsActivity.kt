package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ReviewAdapter
import com.surajmanshal.mannsignadmin.data.model.ordering.OrderItem
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderItemDetailsBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderItemDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderItemDetailsBinding
    lateinit var vm: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderItemDetailsBinding.inflate(layoutInflater)
        val i = intent
        val orderItem = i.getSerializableExtra("orderItem") as OrderItem

        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            if (orderItem.variant != null)
                vm.fetchOrderItemDetails(
                    sid = orderItem.variant!!.sizeId,
                    lid = orderItem.variant!!.languageId,
                    mid = orderItem.variant!!.materialId
                )
            vm.fetchProductReview(orderItem.product!!.productId.toString())
        }


        setupOrderItemDetails(orderItem)

        //observerse
        vm.serverResponse.observe(this) {
            Toast.makeText(this@OrderItemDetailsActivity, it.message, Toast.LENGTH_LONG).show()
        }

        vm.size.observe(this) {
            binding.txtOrderItemDetailsSize.text = "${it.width}\" x ${it.height}\""
        }

        vm.material.observe(this) {
            binding.txtOrderItemDetailsMaterial.text = it.name + " (Per Inch Price : ₹" + it.price +")"
        }

        vm.langauge.observe(this) {
            binding.txtOrderItemDetailsLanguage.text = it.name
        }

        vm.reviews.observe(this){
            binding.rvReviews.adapter = ReviewAdapter(it)
            if(it.isEmpty()){
                binding.txtRatingDetails.text = "No Reviews"
            }else{
                var sum = 0
                it.forEach {
                    sum+=it.rating
                }
                binding.txtRatingDetails.text = "Total Reviews : ${it.size} , Avg Rating : ${sum/it.size}"
            }
        }

        binding.btnProductBack.setOnClickListener {
            onBackPressed()

        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun setupOrderItemDetails(orderItem: OrderItem) {
        with(binding) {

            //default items
            txtProductCode.text = orderItem.product?.productCode ?: "None"
            txtOrderItemDetailsId.text = orderItem.product!!.productId.toString()
            txtOrderItemDetailsTotalPrice.text = "₹" + orderItem.totalPrice.toString() + " (Variant Price x Quantity)"
            txtOrderItemDetailsBasePrice.text ="₹" + orderItem.product!!.basePrice.toString()
            txtOrderItemDetailsQty.text = orderItem.quantity.toString()
            txtOrderItemDetailsVariantPrice.text = "₹" +orderItem.variant!!.variantPrice.toString()

            //image load
            Glide.with(this@OrderItemDetailsActivity)
                .load(Functions.urlMaker(orderItem.product!!.images!!.get(0).url))
                .into(imgOrderItem)

            //for poster details
            if (orderItem.product!!.posterDetails != null) {
                txtOrderItemDetailsProductType.text = "Poster Details"
                gridPosterDetails.visibility = View.VISIBLE
                val p = orderItem.product!!.posterDetails!!
                txtOrderItemDetailsPosterTitle.text = p.title.toString()
                txtOrderItemDetailsPosterShort.text = p.short_desc
                txtOrderItemDetailsPosterLong.text = p.long_desc
            }

            //for acp details
            if (orderItem.product!!.boardDetails != null) {
                txtOrderItemDetailsProductType.text = "ACP Board Details"
                gridAcpDetails.visibility = View.VISIBLE
                val a = orderItem.product!!.boardDetails!!
                txtOrderItemDetailsAcpText.text = a.text
                txtOrderItemDetailsAcpEmboss.text = a.emboss.toString()
                txtOrderItemDetailsAcpFont.text = a.font.toString()
                txtOrderItemDetailsAcpRgb.text = a.rgb.toString()
            }

            //for banner details
            if (orderItem.product!!.boardDetails != null) {
                txtOrderItemDetailsProductType.text = "Banner Details"
                gridBannerDetails.visibility = View.VISIBLE
                val b = orderItem.product!!.bannerDetails!!
                txtOrderItemDetailsBannerText.text = b.text
                txtOrderItemDetailsBannerFont.text = b.font.toString()
            }

        }
    }
}