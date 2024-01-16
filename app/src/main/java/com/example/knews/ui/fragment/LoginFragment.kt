package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.knews.R
import com.example.knews.models.Email
import com.example.knews.models.UserDetails
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class LoginFragment: Fragment(R.layout.fragment_login) {

    lateinit var viewModel : UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val btnLoginUser = view.findViewById<Button>(R.id.btnUserLogin)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val signUpDir = view.findViewById<TextView>(R.id.SignUpFrom)
        val forgotDir = view.findViewById<TextView>(R.id.tvForgotDir)



        btnLoginUser.setOnClickListener {
            if (etEmail.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty()){
                handleLogin(etEmail.text.toString(),etPassword.text.toString())
            } else{
                Toast.makeText(requireContext(),"All fields are required",Toast.LENGTH_LONG).show()
            }
        }

        signUpDir.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUp)
        }

        forgotDir.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }




    private fun handleLogin(email: String, password: String) {
             val url = "auth/login"
        viewModel.login(url, email, password)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let {userDetails ->
                      handleNavDirection(userDetails,email,password)
                        sharedViewModel.userData.value = userDetails.user
                    }
                }
                is Resource.Error -> {
                     showDialog(response.message.toString())
                    viewModel.userDetails.value = null
                }
                is Resource.Loading -> {
                      showProgressDialog()
                }

                else -> {

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

    private fun handleNavDirection(userDetails: UserDetails,email: String,password: String) {
        if (userDetails.user.isEmailVerified == true){
            if (userDetails.user.city == "" || userDetails.user.country == "" || userDetails.user.phone == ""){
                val navController = findNavController()
                Log.e("LoginFragment", "${navController.currentDestination}")
                if (navController.currentDestination?.id == R.id.loginFragment){
                    val actions = LoginFragmentDirections.actionLoginFragmentToCompleteSignUpFragment(Email(email))
                    navController.navigate(actions)
                }
            } else{

                val navController = findNavController()
                Log.e("LoginFragment", "${navController.currentDestination}")
                  if(navController.currentDestination?.id == R.id.loginFragment){
                      val actions = LoginFragmentDirections.actionLoginFragmentToBreakingNewsFragment(userDetails.user)
                      navController.navigate(actions)
                  }
                with(sharedPreferences.edit()){
                    putString("email",email)
                    putString("password",password)
                    putBoolean("verified",userDetails.user.isEmailVerified)
                    apply()
                }
            }
        } else{
            val userEmail = Email(email)
            val url = "auth/sendToken"
            saveLoginStatus(email,password)
            viewModel.sendToken(url,email)


            val navController = findNavController()
            Log.e("LoginFragment", "${navController.currentDestination}")
            if (navController.currentDestination?.id == R.id.loginFragment){
                val actions = LoginFragmentDirections.actionLoginFragmentToVerifyEmailFragment(userEmail)
                navController.navigate(actions)
            }

        }
    }

    private fun showProgressDialog() {
        progressDialog.show()
    }

    @SuppressLint("PrivateResource")
    private fun showDialog(message: String) {
        progressDialog.dismiss()
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Close"){ _,_ ->

        }
        builder.setTitle("Login error")
        builder.setMessage(message)
        builder.setIcon(R.drawable.baseline_error_24)
        builder.create().show()

    }
}