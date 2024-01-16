package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knews.R
import com.example.knews.adapters.NotitificationAdapter
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource

class NotificationFragment: Fragment(R.layout.fragment_notification) {
    lateinit var viewModel : UserViewModel
    lateinit var notificationAdaper: NotitificationAdapter
    private val args by navArgs<NotificationFragmentArgs>()
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Getting Notification...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val icBack = view.findViewById<ImageButton>(R.id.icBack)
        icBack.setOnClickListener {
            findNavController().navigateUp()
        }
        setUpRecycleView()
        viewModel.getUserNotification(args.email.email)
        viewModel.userNotification.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let { notice ->
                        notificationAdaper.differ.submitList(notice.notices.toList())
                    }
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    viewModel.userNotification.value = null
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("try again"){ _,_ ->
                        viewModel.getUserNotification(args.email.email)
                    }
                    builder.setTitle("Error")
                    builder.setMessage(response.message)
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                }
            }
        })
    }

    private fun setUpRecycleView() {
        notificationAdaper = NotitificationAdapter()
        val rvNotification =  view?.findViewById<RecyclerView>(R.id.rvNotification)
        rvNotification?.apply {
            this.adapter = notificationAdaper
            this.layoutManager = LinearLayoutManager(activity)
        }
    }

}