package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ChatAdapter
import com.surajmanshal.mannsignadmin.databinding.ActivityChatBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsignadmin.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel
    lateinit var email : String
    lateinit var id : String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch{
            email = getStringPreferences(Constants.DATASTORE_EMAIL)?:""
            withContext(Dispatchers.Main){
                setupObserver()
                btnClickListners()
            }
        }
        id =  intent.getStringExtra("id")?:""

    }

    override fun onResume() {
        if (id.isNotEmpty()/* && email.isNotEmpty()*/){
            recursiveCalls()
        }else{
            Functions.makeToast(this,"Email or orderId is empty")
        }
        setContentView(binding.root)
        super.onResume()
    }

    fun btnClickListners() {
        binding.btnSendMessage.setOnClickListener {
            if (id.isNotEmpty() && email.isNotEmpty()) {
                vm.addChat(
                    ChatMessage(
                        orderId = id,
                        emailId = email,
                        message = binding.edMessage.text.toString(),
                        System.currentTimeMillis().toString(),
                        null
                    )
                )
                binding.edMessage.text = null
            }
        }
        binding.btnChatBack.setOnClickListener {
            finish()
        }
    }

    fun recursiveCalls(){
        vm.loadChats(id)
        Handler().postDelayed({
            recursiveCalls()
        },1000)
    }

    fun setupObserver() {
        vm.msg.observe(this) {
            Functions.makeToast(this@ChatActivity, it)
        }
        vm.chats.observe(this){
            binding.rvChats.adapter = ChatAdapter(this@ChatActivity,it,email)
            val pos = (binding.rvChats.adapter as ChatAdapter).itemCount-1
            binding.rvChats.smoothScroll(pos,200){

            }
        }
    }

    fun RecyclerView.smoothScroll(toPos: Int, duration: Int = 500, onFinish: () -> Unit = {}) {
        try {
            val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
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

        }
    }

    fun firstVisiblePostion() : Int{
        val manager = binding.rvChats.layoutManager
        if(manager is LinearLayoutManager){
            return (manager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        return 0
    }

    suspend fun getStringPreferences(key : String) : String? {
        val data = preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }
}