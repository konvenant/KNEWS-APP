package com.example.knews.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.knews.models.Notice
import com.example.knews.models.User

class SharedViewModel : ViewModel() {
    val userData: MutableLiveData<User> = MutableLiveData()
    val notice: MutableLiveData<List<Notice>> = MutableLiveData()
}