package com.example.knews.ui.fragment

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.models.Email
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory

class AccountFragment: Fragment(R.layout.fragment_account) {
    lateinit var viewModel : UserViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    private val noticePreference by lazy {
        requireActivity().getSharedPreferences("notice", Context.MODE_PRIVATE)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]

        val emailTv = view.findViewById<TextView>(R.id.tvEmail)
        val phoneTv = view.findViewById<TextView>(R.id.tvPhone)
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val tvNotification = view.findViewById<TextView>(R.id.tvNotification)
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer {
            emailTv.text = it.email
            phoneTv.text = it.phone
            Glide.with(this)
                .load(it.image)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.avartar))
                .into(profileImage)

            val icUpdate = view.findViewById<ImageButton>(R.id.icUpdate)
            val email = it.email!!
            icUpdate.setOnClickListener {
                val actions = AccountFragmentDirections.actionAccountFragmentToNotificationFragment(
                    Email(email)
                )
                findNavController().navigate(actions)
            }
            tvNotification.setOnClickListener {
                val actions = AccountFragmentDirections.actionAccountFragmentToNotificationFragment(
                    Email(email)
                )
                findNavController().navigate(actions)
            }
        })


        val tvProfile = view.findViewById<TextView>(R.id.tvProfile)
        val icSettings = view.findViewById<ImageButton>(R.id.icSettings)
        val icBack = view.findViewById<ImageButton>(R.id.icBack)
        val icProfile = view.findViewById<ImageButton>(R.id.icProfile)
        val tvSetting = view.findViewById<TextView>(R.id.tvSettings)
        val tvHelp = view.findViewById<TextView>(R.id.tvHelp)

        profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_accountDetailsFragment)
        }

        tvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_accountDetailsFragment)
        }

        icSettings.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_settingsFragment)
        }

        icBack.setOnClickListener {
            findNavController().navigateUp()
        }

        icProfile.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_accountDetailsFragment)
        }

        tvSetting.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_settingsFragment)
        }

        tvHelp.setOnClickListener {
            Toast.makeText(requireContext(),"Help Currently unavailable, Contact K-news@gmail.com for help",Toast.LENGTH_LONG).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.blue)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


    }
}