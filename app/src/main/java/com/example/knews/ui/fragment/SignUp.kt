package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.knews.util.Resource
import com.example.knews.R
import com.example.knews.models.Email
import com.example.knews.repository.UserRepository
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory

class SignUp: Fragment(R.layout.fragment_sign_up) {
    lateinit var viewModel : UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnSignUp = view.findViewById<Button>(R.id.btnAddUser)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val signUpFromLogin = view.findViewById<TextView>(R.id.LoginFromSignUpScreen)

      btnSignUp.setOnClickListener {
         if (etEmail.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty()){
             handleSignUp(etEmail.text.toString(),etPassword.text.toString())
         } else{
             Toast.makeText(requireContext(),"All fields are required", Toast.LENGTH_LONG).show()
         }
      }

        signUpFromLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUp_to_loginFragment)
        }


    }

    @SuppressLint("SuspiciousIndentation")
    private fun handleSignUp(email: String, password: String) {
     val url = "auth/signup"
        viewModel.signUp(url, email, password)
        viewModel.message.observe(viewLifecycleOwner, Observer {message ->
            when(message){
                is Resource.Success -> {
                    viewModel.message.value = null
                    progressDialog.dismiss()
                    message.data?.let {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setPositiveButton("Continue"){ _,_ ->

                        }
                        builder.setTitle("Signup successful")
                        builder.setMessage(it.message)
                        builder.setIcon(R.drawable.baseline_person_add_24)
                        builder.create().show()
                    }
                    saveLoginStatus(email, password)
                    val navController = findNavController()
                    if (navController.currentDestination?.id == R.id.signUp){
                        val actions = SignUpDirections.actionSignUpToVerifyEmailFragment(Email(email))
                        navController.navigate(actions)
                    }
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Error")
                    builder.setMessage(message.message.toString())
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                    viewModel.message.value = null
                }
            }
        })
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveLoginStatus(email: String, password: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString("email",email)
//        editor.putString("password", password)
//        editor.putBoolean("verified", false)

        with(sharedPreferences.edit()){
            putString("email",email)
            putString("password",password)
            putBoolean("verified",false)
            apply()
        }
    }
}