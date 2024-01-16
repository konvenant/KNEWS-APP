package com.example.knews.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel

class AccountDetailsFragment: Fragment(R.layout.fragment_account_details) {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val icBack = view.findViewById<ImageButton>(R.id.icBack)
        val icUpdate = view.findViewById<ImageButton>(R.id.icUpdate)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val tvUserCity = view.findViewById<TextView>(R.id.tvUserCity)
        val tvUserCountry = view.findViewById<TextView>(R.id.tvUserCountry)
        val tvUserPhone = view.findViewById<TextView>(R.id.tvUserPhone)

        sharedViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
            Glide.with(this)
                .load(user.image)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.avartar))
                .into(profileImage)
            tvEmail.text = user.email
            tvPhone.text = user.phone
            tvUserName.text = user.name
            tvUserEmail.text = user.email
            tvUserCity.text = user.city
            tvUserCountry.text = user.country
            tvUserPhone.text = user.phone
        })
        profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_accountDetailsFragment_to_updateUserFragment2)
        }
        icBack.setOnClickListener {
            findNavController().navigateUp()
        }
        icUpdate.setOnClickListener {
            findNavController().navigate(R.id.action_accountDetailsFragment_to_updateUserFragment2)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.blue)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}