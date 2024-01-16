package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.knews.R

class SettingsFragment: Fragment() {

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }


    private val themePref by lazy {
        requireActivity().getSharedPreferences("theme", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings,container,false)
    }
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val icBack = view.findViewById<ImageButton>(R.id.icBack)
        val btnLogout = view.findViewById<Button>(R.id.btnSettingLogout)

        btnLogout.setOnClickListener {
            with(sharedPreferences.edit()){
                remove("email")
                remove("password")
                remove("verified")
                apply()
            }


            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }


        icBack.setOnClickListener {
            findNavController().navigateUp()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val isDarkTheme = themePref.getBoolean("dark_theme", false)
        val themeSwitch = view.findViewById<Switch>(R.id.themeSwitch)
        themeSwitch.isChecked = isDarkTheme

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
           if (isChecked){
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
               with(themePref.edit()){
                   putBoolean("dark_theme",true)
                   apply()
               }
               activity?.recreate()

           } else{
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
               with(themePref.edit()){
                   putBoolean("dark_theme",false)
                   apply()
               }
               activity?.recreate()
           }

        }


    }
}