package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.knews.R
import com.example.knews.models.Email
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class SplashFragment: Fragment(R.layout.fragment_splash_screen) {

    lateinit var viewModel : UserViewModel
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }
    private lateinit var progressDialog: ProgressDialog
    private val sharedViewModel: SharedViewModel by activityViewModels()
    @SuppressLint("ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        checkLoginStatus()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.splashWhite)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun checkLoginStatus() {
        val email = sharedPreferences.getString("email",null)
        val password = sharedPreferences.getString("password",null)
        val verified = sharedPreferences.getBoolean("verified", false)


        if (email != null && password != null){
            if (verified == true){
                performLogin(email,password)
            } else{
                Handler().postDelayed({
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                },3000)
            }
        } else{
            Handler().postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_choiceFragment)
            },3000)
        }

    }

    private fun performLogin(email: String, password: String) {
       val url = "auth/login"
        viewModel.login(url,email,password)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let {userDetails ->
                       if (userDetails.user.phone == "" || userDetails.user.name == ""){
                           Handler().postDelayed({
                               val action =
                                   SplashFragmentDirections.actionSplashFragmentToCompleteSignUpFragment(
                                       Email(email)
                                   )
                               findNavController().navigate(action)
                           },3000)
                       } else {
                           Handler().postDelayed({
                               val action =
                                   SplashFragmentDirections.actionSplashFragmentToBreakingNewsFragment(
                                       userDetails.user
                                   )
                               findNavController().navigate(action)
                               sharedViewModel.userData.value = userDetails.user
                           },3000)
                       }
                    }
                }
                is Resource.Error -> {
                    Handler().postDelayed({
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    progressDialog.dismiss()
                        viewModel.userDetails.value = null
                } ,3000)
                }
                is Resource.Loading -> {
//                 progressDialog.show()
                }
            }
        })
    }
}