package com.example.knews.ui.fragment

import android.app.Application
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knews.R
import com.example.knews.adapters.NewsAdapter
import com.example.knews.models.Article
import com.example.knews.models.Source
import com.example.knews.repository.NewsRepository
import com.example.knews.repository.UserRepository
import com.example.knews.ui.NewsViewModel
import com.example.knews.ui.NewsViewModelProviderFactory
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news){
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
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

         setUpRecycleView()
        handleGetArticle()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                handleDelete(article)
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            val rvSavedNews = view.findViewById<RecyclerView>(R.id.rvSavedNews)
            attachToRecyclerView(rvSavedNews)
        }
    }

    private fun handleGetArticle() {
        userViewModel.getUserSavedArticle(sharedViewModel.userData.value?.email!!)
        userViewModel.userSavedArticle.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let {
                       val articles: List<Article> =  it.map { article ->
                            Article(article._id,article.author,article.content,article.description,article.publishedAt,
                                Source(article.source.id,article.source.name),article.title,article.url,article.urlToImage
                            )
                        }

                        newsAdapter.differ.submitList(articles)
                    }
                }
                is Resource.Loading ->{
                    progressDialog.show()
                }
                is Resource.Error -> {
                    userViewModel.userSavedArticle.value = null
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun handleDelete(article: Article){
        userViewModel.deleteSavedArticle(sharedViewModel.userData.value?.email!!,article.id.toString())
        userViewModel.articleMessage.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressDialog.dismiss()
                    response.data?.let {
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                userViewModel.articleMessage.value = null
                                val apiUrl = "article/create"
                                userViewModel.saveArticle(
                                    apiUrl,
                                    sharedViewModel.userData.value!!.email!!,
                                    article.author!!, article.content!!,article.description!!,
                                    article.publishedAt!!, article.title!!,article.url!!,
                                    article.urlToImage!!,article.source.id.toString(),
                                    article.source.name.toString(),article.id.toString()
                                )
                                handleGetArticle()
                            }
                                .show()
                        }
                    }
                    handleGetArticle()
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    userViewModel.articleMessage.value = null
                    progressDialog.dismiss()
                    Snackbar.make(requireView(),response.message.toString(),Snackbar.LENGTH_LONG).show()
                }
            }
        })

        userViewModel.addArticleMessage.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success -> {
                    Toast.makeText(requireContext(),"Article Added Back!!",Toast.LENGTH_LONG).show()
                    handleGetArticle()
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                    userViewModel.addArticleMessage.value = null
                }
            }
        })
    }

    private fun setUpRecycleView() {
        newsAdapter = NewsAdapter()
        val rvSavedNews =  view?.findViewById<RecyclerView>(R.id.rvSavedNews)
        rvSavedNews.apply {
            this?.adapter = newsAdapter
            this?.layoutManager = LinearLayoutManager(activity)

        }
    }
}