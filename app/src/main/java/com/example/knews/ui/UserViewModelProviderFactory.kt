package com.example.knews.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.knews.NewsApplication
import com.example.knews.repository.UserRepository


class UserViewModelProviderFactory(
    val app : Application,
    val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(app,userRepository) as T
    }
}