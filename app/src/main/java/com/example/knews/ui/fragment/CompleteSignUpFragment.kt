package com.example.knews.ui.fragment

import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.knews.R
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class CompleteSignUpFragment: Fragment(R.layout.fragment_complete_sign_up) {

    lateinit var viewModel : UserViewModel

    private lateinit var progressDialog: ProgressDialog

    private val args by  navArgs<CompleteSignUpFragmentArgs>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val btnCompleteSignUp = view.findViewById<Button>(R.id.btnCompleteSignUp)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etPhone= view.findViewById<EditText>(R.id.etPhone)
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val country = "Nigeria"
        val email = args.email.email

        btnCompleteSignUp.setOnClickListener {
            if (
                etName.text.toString().isNotEmpty() &&
                        etCity.text.toString().isNotEmpty() &&
                        etPhone.text.toString().isNotEmpty()
            ){
                completeSignUp(etName.text.toString(),etPhone.text.toString(),etCity.text.toString(),country,email)
            } else{
                Toast.makeText(requireContext(),"All fields are required",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun completeSignUp(name: String, phone: String, city: String, country: String,email: String) {
        val url = "user/update"
       viewModel.updateUser(url, email, name, phone, city, country)
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let { userDetails ->
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setPositiveButton("Finish"){ _,_ ->

                        }
                        builder.setTitle("Registration Completed")
                        builder.setMessage("User Registration Completed, click dashboard to access unique service")
                        builder.setIcon(R.drawable.baseline_person_add_24)
                        builder.create().show()
                        val navController = findNavController()
                        if (navController.currentDestination?.id == R.id.completeSignUpFragment){
                            val action = CompleteSignUpFragmentDirections.actionCompleteSignUpFragmentToBreakingNewsFragment(userDetails.user)
                            navController.navigate(action)
                        }
                        sharedViewModel.userData.value = userDetails.user
                    }
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("try again"){ _,_ ->

                    }
                    builder.setTitle("Error")
                    builder.setMessage(response.message)
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                    viewModel.userDetails.value = null
                }
            }
        })
    }
}