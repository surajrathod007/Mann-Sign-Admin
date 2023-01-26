package com.surajmanshal.mannsignadmin.ui.activity

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ChatAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityChatBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.URIPathHelper
import com.surajmanshal.mannsignadmin.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel
    var id: String? = null
    var email: String? = null

    val REQUEST_CODE = 0
    var imageUri: Uri? = null

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        binding = ActivityChatBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        binding.clChatLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        email = intent.getStringExtra("email")?:""
        id = intent.getStringExtra("id")
        setContentView(binding.root)

        setupObserver()
        btnClickListners()
    }

    override fun onStop() {
        super.onStop()
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable)
        }
    }

    override fun onStart() {
        mHandler = Handler()
        super.onStart()
    }

    override fun onResume() {
        if (!id.isNullOrEmpty() && !email.isNullOrEmpty()) {
            mHandler.post(object : Runnable {
                override fun run() {
                    mRunnable = this
                    if (!id.isNullOrEmpty()) {
                        vm.loadChats(id!!)
                    }
                    mHandler.postDelayed(this, 1000)
                }
            })
        } else {
            Functions.makeToast(this, "Email or orderId is empty")
        }
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun btnClickListners() {
        binding.btnSendMessage.setOnClickListener {
            if (!id.isNullOrEmpty() && !email.isNullOrEmpty()) {

                if (imageUri != null) {
                    val file = File(URIPathHelper().getPath(this@ChatActivity,imageUri!!))
                    val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
                    val part = MultipartBody.Part.createFormData("product", file.name, requestBody)
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.uploadChatImage(
                            part, ChatMessage(
                                orderId = id!!,
                                emailId = email!!,
                                message = binding.edMessage.text.toString(),
                                System.currentTimeMillis().toString(),
                                null
                            )
                        )
                    }
                    imageUri = null
                    binding.imgChatSelected.setImageURI(null)
                    binding.imgChatSelected.visibility = View.GONE
                    binding.btnRemoveChatImage.visibility = View.GONE
                    binding.btnAddChatImage.visibility = View.VISIBLE

                } else {
                    vm.addChat(
                        ChatMessage(
                            orderId = id!!,
                            emailId = email!!,
                            message = binding.edMessage.text.toString(),
                            System.currentTimeMillis().toString(),
                            null
                        )
                    ) {

                    }
                }
                binding.edMessage.text = null
            }


        }
        binding.btnChatBack.setOnClickListener {
            finish()
        }
        binding.btnAddChatImage.setOnClickListener {
            chooseImage()
        }
        binding.btnRemoveChatImage.setOnClickListener {
            binding.imgChatSelected.setImageURI(null)
            binding.imgChatSelected.visibility = View.GONE
            binding.btnRemoveChatImage.visibility = View.GONE
            binding.btnAddChatImage.visibility = View.VISIBLE
        }
    }

    fun setupObserver() {
        vm.msg.observe(this) {
            Functions.makeToast(this@ChatActivity, it)
        }
        vm.chats.observe(this) {
            //val count = (binding.rvChats.adapter as ChatAdapter).itemCount
            if (vm.msgSize.value == 0) {
                binding.rvChats.adapter = ChatAdapter(this@ChatActivity, it, email)
                vm.msgSize.postValue(it.size)
                //Functions.makeToast(this@ChatActivity,"In if")
            } else if (it.size > vm.msgSize.value!!) {
                //Functions.makeToast(this@ChatActivity,"In else if")
                binding.rvChats.adapter = ChatAdapter(this@ChatActivity, it, email)

                val pos = (binding.rvChats.adapter as ChatAdapter).itemCount - 1
                binding.rvChats.smoothScroll(pos, 200) {

                }
            }
        }
        vm.isLoading.observe(this){
            if(it){
                binding.imgLoading.visibility = View.VISIBLE
            }else{
                binding.imgLoading.visibility = View.GONE
            }
        }

    }

    fun RecyclerView.smoothScroll(toPos: Int, duration: Int = 500, onFinish: () -> Unit = {}) {
        try {
            val smoothScroller: RecyclerView.SmoothScroller =
                object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_END
                    }

                    override fun calculateTimeForScrolling(dx: Int): Int {
                        return duration
                    }

                    override fun onStop() {
                        super.onStop()
                        onFinish.invoke()
                    }
                }
            smoothScroller.targetPosition = toPos
            layoutManager?.startSmoothScroll(smoothScroller)
        } catch (e: Exception) {
            println("$e")
        }
    }

    fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            //you got the image
            var uri = data?.data
            imageUri = data?.data
            if (uri != null) {
                binding.imgChatSelected.setImageURI(uri)
                binding.imgChatSelected.visibility = View.VISIBLE
                binding.btnRemoveChatImage.visibility = View.VISIBLE
                binding.btnAddChatImage.visibility = View.GONE
            } else {
                binding.imgChatSelected.visibility = View.GONE
                binding.btnRemoveChatImage.visibility = View.GONE
                binding.btnAddChatImage.visibility = View.VISIBLE
            }
        }
    }

}