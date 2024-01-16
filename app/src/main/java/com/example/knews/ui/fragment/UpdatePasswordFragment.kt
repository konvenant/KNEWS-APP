package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.repository.UserRepository
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class UpdatePasswordFragment: Fragment(R.layout.fragment_update_password) {
    lateinit var viewModel: UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val args by navArgs<UpdatePasswordFragmentArgs>()
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application, userRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnUpdatePassword = view.findViewById<Button>(R.id.btnUpdatePassword)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmailAddress)
        val etPassword = view.findViewById<EditText>(R.id.etPasswordChange)
        val ivImage = view.findViewById<ImageView>(R.id.ivProfileChange)
        val email = args.user.email
        val url = args.user.image



            btnUpdatePassword.setOnClickListener {
                if (etPassword.text.toString().isNotEmpty()){
             handleUpdatePassword(email,etPassword.text.toString())
            } else{
                    Toast.makeText(requireContext(),"All fields are required",Toast.LENGTH_LONG).show()
                }
        }
        val emailText = "Email: ${args.user.email}"
        tvEmail.text = emailText
        Glide.with(this)
            .load(url)
            .circleCrop()
            .apply(RequestOptions.placeholderOf(R.drawable.avartar))
            .into(ivImage)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun handleUpdatePassword(email: String?, password:String) {
        val url = "user/updatePassword"
          viewModel.updatePassword(url,email!!,password)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressDialog.dismiss()
                    findNavController().navigate(R.id.action_updatePasswordFragment_to_loginFragment)
                    Toast.makeText(requireContext(),"password changed, try login",Toast.LENGTH_LONG).show()
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