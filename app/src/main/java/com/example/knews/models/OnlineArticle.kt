package com.example.knews.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class OnlineArticle(
    val _id: String,
    val __v: Int,
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: Source,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null
) : Serializable