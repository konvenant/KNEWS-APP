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
import com.example.knews.R
import com.example.knews.repository.UserRepository
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class ForgotPasswordFragment: Fragment(R.layout.fragment_forgot) {
    lateinit var viewModel : UserViewModel

    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnForgot = view.findViewById<Button>(R.id.btnForgotContinue)
        val etForgotEmail = view.findViewById<EditText>(R.id.etForgotEmail)
        val tvLogin= view.findViewById<TextView>(R.id.LoginFromForgotPassword)


            btnForgot.setOnClickListener {
                if (etForgotEmail.text.toString().isNotEmpty()) {
                    handleForgotPassword(etForgotEmail.text.toString())
                } else{
                    Toast.makeText(requireContext(),"Email field is required",Toast.LENGTH_LONG).show()
                }
            }

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }
    }

    private fun handleForgotPassword(email: String) {
       viewModel.forgotPassword(email)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressDialog.dismiss()
                   response.data?.let { userDetails ->
                       val actions = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToVerifyForgotPasswordTokenFragment(userDetails.user)
                       findNavController().navigate(actions)
                   Toast.makeText(requireContext(),"token sent to $email",Toast.LENGTH_LONG).show()
                   }
                    viewModel.userDetails.value = null
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    viewModel.userDetails.value = null
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