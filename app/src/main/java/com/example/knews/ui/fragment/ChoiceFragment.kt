package com.example.knews.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.knews.R

class ChoiceFragment: Fragment(R.layout.fragment_choice) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignUp = view.findViewById<Button>(R.id.btnSignUpFirst)
        val tvLogin = view.findViewById<TextView>(R.id.tvLoginFirst)

        btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_choiceFragment_to_signUp)
        }

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_choiceFragment_to_loginFragment)
        }
    }
}