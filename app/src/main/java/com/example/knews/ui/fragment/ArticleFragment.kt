package com.example.knews.ui.fragment

import android.app.Application
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.knews.R
import com.example.knews.repository.NewsRepository
import com.example.knews.repository.UserRepository
import com.example.knews.ui.NewsViewModel
import com.example.knews.ui.NewsViewModelProviderFactory
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article){

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    lateinit var userViewModel : UserViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = NewsRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        val userRepository = UserRepository()
        val userViewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        userViewModel = ViewModelProvider(this,userViewModelProviderFactory)[UserViewModel::class.java]
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val article = args.article
        Log.e("ArticleFragment","article: $article")
        val webView =  view.findViewById<WebView>(R.id.webView)
        webView?.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }
        val fab =  view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            handleSave()
        }


    }

    private fun handleSave() {
        val article = args.article
        val apiUrl = "article/create"
        val email = sharedViewModel.userData.value?.email!!
        val author =  article.author ?: " "
        val content =  article.content ?: " "
        val description = article.description ?: " "
        val publishedAt =  article.publishedAt ?: " "
        val title =  article.title ?: " "
        val url =  article.url ?: " "
        val urlToImage =  article.urlToImage ?: " "
        val id =  article.id ?: " "
        val sid =   article.source.id.toString() ?: " "
        val name = article.source.name ?: " "

        userViewModel.saveArticle(apiUrl,email,author,content,description,publishedAt,title,url,urlToImage,sid,name,id)
        userViewModel.addArticleMessage.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let {
                        Snackbar.make(requireView(), it.message.toString(),Snackbar.LENGTH_LONG).show()
                        val fab = view?.findViewById<FloatingActionButton>(R.id.fab)
                        fab?.isEnabled = false
                    }
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    userViewModel.articleMessage.value = null
                    progressDialog.dismiss()
                    Log.e("ArticleFraggy",response.message.toString())
                    Snackbar.make(requireView(),response.message.toString(),Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}