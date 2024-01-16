package com.example.knews.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.knews.R
import com.example.knews.repository.NewsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    private val themePref by lazy {
        getSharedPreferences("theme", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)


        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]
        val bottomNavigationItemView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
        val navController = fragment?.findNavController()
        bottomNavigationItemView.setupWithNavController(fragment!!.findNavController())

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment ||
                destination.id == R.id.signUp ||
                destination.id == R.id.loginFragment ||
                destination.id == R.id.completeSignUpFragment ||
                destination.id == R.id.verifyEmailFragment ||
                destination.id == R.id.verifyForgotPasswordTokenFragment ||
                destination.id == R.id.notificationFragment ||
                destination.id == R.id.updatePasswordFragment ||
                destination.id == R.id.choiceFragment ||
                destination.id == R.id.forgotPasswordFragment ||
                        destination.id == R.id.accountDetailsFragment ||
                        destination.id == R.id.updateUserPasswordFragment ||
                        destination.id == R.id.updateUserFragment2 ||
                        destination.id == R.id.settingsFragment
            ){
                bottomNavigationItemView.visibility = View.GONE
            } else{
                bottomNavigationItemView.visibility = View.VISIBLE
            }
        }

        val isDarkTheme = themePref.getBoolean("dark_theme", false)
        if (isDarkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
        val navController = navHostFragment?.findNavController()

        if (navController?.currentDestination?.id == R.id.breakingNewsFragment ||
            navController?.currentDestination?.id == R.id.savedNewsFragment ||
            navController?.currentDestination?.id == R.id.searchNewsFragment ||
            navController?.currentDestination?.id == R.id.accountFragment ||
            navController?.currentDestination?.id == R.id.choiceFragment ||
                    navController?.currentDestination?.id == R.id.loginFragment ||

            navController?.currentDestination?.id == R.id.signUp
        ){
            showExitAppDialog()
        } else{
            super.onBackPressed()
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun showExitAppDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit_app, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnExit =  dialogView.findViewById<Button>(R.id.btnExit)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

}