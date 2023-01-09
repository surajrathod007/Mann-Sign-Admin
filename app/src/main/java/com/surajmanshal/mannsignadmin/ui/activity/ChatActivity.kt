package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ChatAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityChatBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel
    var id: String? = null
    var email: String? = null

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

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
                vm.addChat(
                    ChatMessage(
                        orderId = id!!,
                        emailId = email!!,
                        message = binding.edMessage.text.toString(),
                        System.currentTimeMillis().toString(),
                        null
                    )
                ){

                }
                binding.edMessage.text = null
            }


        }
        binding.btnChatBack.setOnClickListener {
            finish()
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

}