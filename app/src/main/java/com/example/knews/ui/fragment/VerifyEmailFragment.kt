package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chaos.view.PinView
import com.example.knews.R
import com.example.knews.models.Email
import com.example.knews.repository.UserRepository
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class VerifyEmailFragment: Fragment(R.layout.fragment_verify) {
    lateinit var viewModel : UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }
    private val args by  navArgs<VerifyEmailFragmentArgs>()
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnVerifyEmail = view.findViewById<Button>(R.id.btnVerifyEmail)
        val btnResendToken = view.findViewById<Button>(R.id.btnResendToken)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val pvToken = view.findViewById<PinView>(R.id.pvEmailToken)


        tvStatus.text = "Verification Token sent to ${args.email.email}"
        btnVerifyEmail.setOnClickListener {
        val token = pvToken.text.toString().toInt()
           if (token.toString().isNotEmpty()){
               verifyEmail(args.email.email,token)
           } else{
               Toast.makeText(requireContext(),"Token fields is required", Toast.LENGTH_LONG).show()
           }
        }
        btnResendToken.setOnClickListener {
            resendToken(args.email.email)
        }
    }

    @SuppressLint("PrivateResource")
    private fun resendToken(email: String) {
        val url = "auth/sendToken"
         viewModel.sendToken(url,email)
        viewModel.sentTokenMessage.observe(viewLifecycleOwner,Observer{ message->
            when(message) {
                is Resource.Success -> {
                    viewModel.sentTokenMessage.value = null
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("close"){ _,_ ->

                    }
                    builder.setTitle("Token Sent")
                    builder.setMessage("Verification token sent to $email")
                    builder.setIcon(R.drawable.baseline_email_24)
                    builder.create().show()
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    viewModel.sentTokenMessage.value = null
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Resend Token Failed")
                    builder.setMessage(message.message)
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                }
            }
        })
    }

    @SuppressLint("PrivateResource", "SuspiciousIndentation")
    private fun verifyEmail(email: String, token: Int) {
      val url = "auth/verify"
        viewModel.verifyEmail(url,email,token)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    viewModel.userDetails.value = null
                    progressDialog.dismiss()
                    response.data?.let { userDetails ->
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setPositiveButton("Continue"){ _,_ ->

                        }
                        builder.setTitle("Verification Successful")
                        builder.setMessage("User Verified, continue to signup")
                        builder.setIcon(R.drawable.baseline_verified_24)
                        builder.create().show()
                        val navController = findNavController()
                        if (navController.currentDestination?.id == R.id.verifyEmailFragment){
                            val actions = VerifyEmailFragmentDirections.actionVerifyEmailFragmentToCompleteSignUpFragment2(Email(email))
                            navController.navigate(actions)
                        }
                        saveLoginStatus(email)
                    }
                }
                is Resource.Loading -> {
              progressDialog.show()
                }
                is  Resource.Error -> {
                progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Verification Failed")
                    builder.setMessage(response.message.toString())
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                    viewModel.userDetails.value = null
                }
            }
        })
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveLoginStatus(email: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString("email",email)
//        editor.putBoolean("verified", true)

        with(sharedPreferences.edit()){
            putString("email",email)
//            putString("password",password)
            putBoolean("verified",true)
            apply()
        }
    }
}