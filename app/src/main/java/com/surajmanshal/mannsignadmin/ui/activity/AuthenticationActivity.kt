package com.surajmanshal.mannsignadmin.ui.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View.INVISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import com.surajmanshal.mannsignadmin.utils.auth.DataStore
import com.surajmanshal.mannsignadmin.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsignadmin.MainActivity
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.auth.LoginRequest
import com.surajmanshal.mannsignadmin.data.model.auth.LoginResponse
import com.surajmanshal.mannsignadmin.data.model.auth.User
import com.surajmanshal.mannsignadmin.databinding.ActivityAuthenticationBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.Executor

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthenticationBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var token : String? = null
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog
    var failedPasswordAttempts = 0
    var biometricFailedAttempts = 0
    var maxFailureAttempts = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        d = LoadingScreen(this)
        dd = d.loadingScreen()

        CoroutineScope(Dispatchers.IO).launch{
            val lockedFor = getStringPreferences(Constants.LOCKED_FOR)
            if (lockedFor!=null){
                lockedFor.toLong().apply {
                    var tryAgainInMillies = this - System.currentTimeMillis()
                    if(tryAgainInMillies>0){
                        withContext(Dispatchers.Main){
                            binding.apply {
                                ivFingerPrint.hide()
                                checkBoxRememberMe.hide()
                                tvAttemptLeft.show()
                                btnLogin.apply {
                                    isEnabled = false
                                    setBackgroundResource(R.drawable.disabled_button_bg)
                                }
                            }
                        }
                        do {
                            withContext(Dispatchers.Main){
                                with(binding){
                                    tryAgainInMillies-=1000
                                    val tryAgainInMinutes = tryAgainInMillies/60000
                                    val tryAgainInSeconds = (tryAgainInMillies/1000)%60
                                    tvAttemptLeft.setText("Try again in " +
                                            "$tryAgainInMinutes:${if(tryAgainInSeconds>9) tryAgainInSeconds else "0$tryAgainInSeconds"}")
                                }
                            }
                            delay(1000)
                        }while (tryAgainInMillies>0)
                    }
                    storeStringPreferences(Constants.LOCKED_FOR,"0")
                    withContext(Dispatchers.Main){
                        binding.tvAttemptLeft.hide()
                        binding.btnLogin.apply {
                            isEnabled = true
                            setBackgroundResource(R.drawable.button_bg)
                        }
                    }
                }
            }

            token = getStringPreferences(DataStore.JWT_TOKEN)
            val email = getStringPreferences(Constants.DATASTORE_EMAIL)
            if(token!=null){
                withContext(Dispatchers.Main){
                    showBiometricPrompt()
                    binding.ivFingerPrint.setOnClickListener {
                        biometricPrompt.authenticate(promptInfo)
                    }
                }
            }
            if(email==null){
                withContext(Dispatchers.Main){
                    with(binding){
                        ivFingerPrint.hide()
                        btnLogin.setOnClickListener {
                            if(!isDataFilled(ETEmail))else if(!isDataFilled(ETPassword))else{
                                loginUser(ETEmail.text.toString(),ETPassword.text.toString())
                            }
                        }
                    }
                }
            }
            else{
                withContext(Dispatchers.Main){
                    with(binding){
                        emailInputLayout.apply {
                            visibility =  INVISIBLE
                            isEnabled = false
                        }
                        checkBoxRememberMe.hide()
                        btnLogin.setOnClickListener {
                            if(!isDataFilled(ETPassword))
                                return@setOnClickListener
                            loginUser(email,ETPassword.text.toString())
                        }
                    }
                }
            }
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onLoginSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    biometricFailedAttempts++
                    if(biometricFailedAttempts==maxFailureAttempts){
                        Toast.makeText(applicationContext, "Too many failed attempts",
                            Toast.LENGTH_SHORT)
                            .show()
                        binding.ivFingerPrint.hide()
                        onAuthenticationError(-1,"")
                    }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Mann Sign")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Login with password")
            .build()
    }

    private fun showBiometricPrompt(){
        biometricPrompt.authenticate(promptInfo)
    }

    suspend fun getStringPreferences(key : String) : String? {
        val data = preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }

    suspend fun storeStringPreferences(key: String ,value : String){
        this.preferenceDataStoreAuth.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    suspend fun clearPreferenceKey(key: String){
        this.preferenceDataStoreAuth.edit {
            clearPreferenceKey(key)
        }
    }

    private fun onLoginSuccess(){
        val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
        intent.putExtra(DataStore.JWT_TOKEN,token)
        startActivity(intent)
        finish()
    }

    fun isDataFilled(view: TextView) : Boolean{
        if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            when(view){
                binding.ETEmail -> Snackbar.make(view, "Email is required", 1000).show()
                binding.ETPassword -> Snackbar.make(view, "Password is required", 1000).show()
            }
            return false
        }
        return true
    }

    fun loginUser(email : String,password : String){
        d.toggleDialog(dd) //show

        lifecycleScope.launch {
            var response : LoginResponse? = null
            try {
                response =  NetworkService.networkInstance.loginAdmin(LoginRequest(email,password))
            }catch (e:Exception){
                ExceptionHandler.catchOnContext(this@AuthenticationActivity, getString(R.string.generalErrorMsg))
                d.toggleDialog(dd)
            }
            if(response!=null){
                if(response.simpleResponse.success){
                    onSimpleResponse("Login",response.user)
                    Toast.makeText(this@AuthenticationActivity, response.simpleResponse.message, Toast.LENGTH_SHORT).show()
                }else{
                    failedPasswordAttempts++
                    if(failedPasswordAttempts>=maxFailureAttempts){
                        storeStringPreferences(Constants.LOCKED_FOR,System.currentTimeMillis().plus(900000).toString())
                        d.toggleDialog(dd)
                        return@launch
                    }
                    if(failedPasswordAttempts>maxFailureAttempts-3)
                        binding.tvAttemptLeft.apply {
                            show()
                            setText("$text${maxFailureAttempts-failedPasswordAttempts}")
                        }
                    ExceptionHandler.catchOnContext(this@AuthenticationActivity, response.simpleResponse.message)
                    d.toggleDialog(dd)
                }
            }else Toast.makeText(this@AuthenticationActivity, "Server Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun onSimpleResponse(task : String, user: User){
        d.toggleDialog(dd)  // hide
        CoroutineScope(Dispatchers.IO).launch{
            storeStringPreferences(DataStore.JWT_TOKEN,user.token)
            if(binding.checkBoxRememberMe.isChecked)
                storeStringPreferences(Constants.DATASTORE_EMAIL,user.emailId)
        }
        onLoginSuccess()
    }

}