package com.example.knews.models

import java.io.Serializable


data class User(
    val __v: Int?,
    val _id: String?,
    val dateAdded: String?,
    val email: String?,
    val image: String?,
    val isEmailVerified: Boolean?,
    val city :String?,
    val country: String?,
    val phone: String?,
    val name: String?,
    val password: String?,
    val token: Int?,
    val forgotPasswordToken :String?
) : Serializable