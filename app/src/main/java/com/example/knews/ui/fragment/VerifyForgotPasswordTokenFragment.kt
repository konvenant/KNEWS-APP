package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
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
import com.example.knews.repository.UserRepository
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource
import com.google.android.material.snackbar.Snackbar

class VerifyForgotPasswordTokenFragment: Fragment(R.layout.fragment_verify_forgot_password_token) {
    lateinit var viewModel: UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val args by navArgs<VerifyForgotPasswordTokenFragmentArgs>()
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application, userRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnVerifyForgot = view.findViewById<Button>(R.id.btnVerifyForgotSentEmail)
        val pvVerify = view.findViewById<PinView>(R.id.pvForgotEmailToken)
//        val btnResend = view.findViewById<Button>(R.id.btnResendForgotEnail)
        val email = args.user.email
        val status = view.findViewById<TextView>(R.id.tvForgotStatus)

            btnVerifyForgot.setOnClickListener {
                if (pvVerify.text.toString().isNotEmpty()) {
                handleVerifyForgotEmail(email, pvVerify.text.toString())
            } else{
                    Toast.makeText(requireContext(),"Token field is required",Toast.LENGTH_LONG).show()
                }
        }
//        btnResend.setOnClickListener {
////            handleResend(email)
//        }

        status.text = "Verification code sent to $email"
    }

    private fun handleResend(email: String?) {
        viewModel.forgotPassword(email!!)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    viewModel.userDetails.value = null
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(),"Token Sent Successfully to $email",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    viewModel.userDetails.value = null
                    Toast.makeText(requireContext(),"Error sending Token Try Again",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun handleVerifyForgotEmail(email: String?, passwordToken: String) {
            val url = "auth/verifyPassword"
        viewModel.verifyPassword(url, email!!,passwordToken)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    viewModel.userDetails.value = null
                    response.data?.let {userDetails ->
                        val actions = VerifyForgotPasswordTokenFragmentDirections.actionVerifyForgotPasswordTokenFragmentToUpdatePasswordFragment(userDetails.user)
                        findNavController().navigate(actions)
                    }
                    Toast.makeText(requireContext(),"Verification Complete",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error ->{
                    viewModel.userDetails.value = null
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Error")
                    builder.setMessage(response.message.toString())
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                }
            }
        })
    }

}
