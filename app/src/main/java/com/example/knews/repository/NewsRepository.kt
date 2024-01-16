package com.example.knews.repository

import com.example.knews.api.RetrofitInstance
import com.example.knews.models.Article

class NewsRepository(

) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun getCategoryNews(countryCode: String,pageNumber: Int,category: String) =
        RetrofitInstance.api.getCategoryNews(countryCode,category,pageNumber)

}