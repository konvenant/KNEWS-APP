package com.example.knews.ui.fragment

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.adapters.NewsAdapter
import com.example.knews.models.Email
import com.example.knews.models.Notice
import com.example.knews.repository.NewsRepository
import com.example.knews.repository.UserRepository
import com.example.knews.ui.NewsViewModel
import com.example.knews.ui.NewsViewModelProviderFactory
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Constants.QUERY_PAGE_SIZE
import com.example.knews.util.Resource
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news){
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("notice", Context.MODE_PRIVATE)
    }
    private lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"

    var techPageCount : Int = 1
    var businessPageCount : Int = 1
    var healthPageCount : Int = 1
    var sciencePageCount : Int = 1
    var sportPageCount : Int = 1
    var politicsPageCount : Int = 1
    var entertainmentPageCount : Int = 1

    lateinit var userViewModel : UserViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = NewsRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        val userRepository = UserRepository()
        val userViewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        userViewModel = ViewModelProvider(this,userViewModelProviderFactory)[UserViewModel::class.java]

        setUpRecycleView()
         val icNotice = view.findViewById<ImageButton>(R.id.icNotification)
        val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingsTextView = view.findViewById<TextView>(R.id.greetingsTv)
        val ivProfileImage = view.findViewById<ImageView>(R.id.ivProfileImage)
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer {
            val userName = it.name
            val userImageUrl = it.image
            val greetings = when(currentTime) {
                in 0..11 -> "Good Morning, $userName"
                in 12..15 -> "Good Afternoon, $userName"
                else -> "Good Evening, $userName"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            greetingsTextView.text = greetings

            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.avartar))
                .circleCrop()
                .into(ivProfileImage)
            val email = Email(it.email!!)
            icNotice.setOnClickListener {
                val actions = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToNotificationFragment(email)
                findNavController().navigate(actions)
            }
            userViewModel.getUserNotificationCount(email.email)
        })



        ivProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_breakingNewsFragment_to_accountFragment)
        }




        val tvBreakingCategory = view.findViewById<TextView>(R.id.tvBreakingCategory)
        val tvBusinessCategory = view.findViewById<TextView>(R.id.tvBusinessCategory)
        val tvPoliticsCategory = view.findViewById<TextView>(R.id.tvPoliticsCategory)
        val tvEntertainmentCategory = view.findViewById<TextView>(R.id.tvEntertainmentCategory)
        val tvSportCategory = view.findViewById<TextView>(R.id.tvSportCategory)
        val tvHealthCategory = view.findViewById<TextView>(R.id.tvHealthCategory)
        val tvScienceCategory = view.findViewById<TextView>(R.id.tvScienceCategory)
        val tvTechCategory = view.findViewById<TextView>(R.id.tvTechCategory)


        tvBreakingCategory.setTypeface(null,Typeface.BOLD)

        tvBusinessCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvSportCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvHealthCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvPoliticsCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvScienceCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvTechCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvEntertainmentCategory.setOnClickListener {
            onTextClicked(it)
        }
        tvBreakingCategory.setOnClickListener {
            onTextClicked(it)
        }


        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            Log.e(TAG,"Article : $it")
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }



        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            val rvBreakingNews = view.findViewById<RecyclerView>(R.id.rvBreakingNews)
                            rvBreakingNews?.setPadding(0,0,0,0)
                    }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG,"An error occured: $message")
                        Snackbar.make(view,"An Error Occured: $message", Snackbar.LENGTH_LONG).apply {
                            setAction("Try Again"){
                                val tvBreakingCategory = requireView().findViewById<TextView>(R.id.tvBreakingCategory)
                                val tvBusinessCategory = view.findViewById<TextView>(R.id.tvBusinessCategory)
                                val tvPoliticsCategory = view.findViewById<TextView>(R.id.tvPoliticsCategory)
                                val tvEntertainmentCategory = view.findViewById<TextView>(R.id.tvEntertainmentCategory)
                                val tvSportCategory = view.findViewById<TextView>(R.id.tvSportCategory)
                                val tvHealthCategory = view.findViewById<TextView>(R.id.tvHealthCategory)
                                val tvScienceCategory = view.findViewById<TextView>(R.id.tvScienceCategory)
                                val tvTechCategory = view.findViewById<TextView>(R.id.tvTechCategory)
                               if (tvBreakingCategory.typeface?.style == Typeface.BOLD){
                                   viewModel.getBreakingNews("ng")
                               } else if(tvBusinessCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","business",businessPageCount+1)
                                   businessPageCount++
                               } else if(tvPoliticsCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","politics",politicsPageCount+1)
                                   politicsPageCount++
                               } else if(tvEntertainmentCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","entertainment",entertainmentPageCount+1)
                                   entertainmentPageCount++
                               }
                               else if(tvSportCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","sport",sportPageCount+1)
                                   sportPageCount++
                               } else if(tvHealthCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","health",healthPageCount+1)
                                   healthPageCount++
                               }else if(tvScienceCategory?.typeface?.style == Typeface.BOLD){
                                   viewModel.getCategoriesNews("ng","science",sciencePageCount+1)
                                   sciencePageCount++
                               }else {
                                   viewModel.getCategoriesNews("ng","technology",techPageCount+1)
                                   techPageCount++
                               }

                            }
                            show()
                        }

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {
                    Snackbar.make(view,"An Error Occured", Snackbar.LENGTH_LONG).apply {
                        setAction("Try Again"){
                            viewModel.getBreakingNews("ng")
                        }
                        show()
                    }
                }
            }
        })

        userViewModel.userNotificationCount.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        performNoticeCount(it.notice)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    sharedViewModel.notice.value = null
                    val tvBadge = view.findViewById<TextView>(R.id.tvBadges)
                    tvBadge.visibility = View.GONE
                }
            }
        })
    }



    private fun hideProgressBar(){
        val paginationProgressBar =  view?.findViewById<ProgressBar>(R.id.paginationProgressBar)
        paginationProgressBar?.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        val paginationProgressBar =  view?.findViewById<ProgressBar>(R.id.paginationProgressBar)
        paginationProgressBar?.visibility = View.VISIBLE
        isLoading = true
    }



    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning =firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                val tvBreakingCategory = requireView().findViewById<TextView>(R.id.tvBreakingCategory)
                val tvBusinessCategory = view?.findViewById<TextView>(R.id.tvBusinessCategory)
                val tvPoliticsCategory = view?.findViewById<TextView>(R.id.tvPoliticsCategory)
                val tvEntertainmentCategory = view?.findViewById<TextView>(R.id.tvEntertainmentCategory)
                val tvSportCategory = view?.findViewById<TextView>(R.id.tvSportCategory)
                val tvHealthCategory = view?.findViewById<TextView>(R.id.tvHealthCategory)
                val tvScienceCategory = view?.findViewById<TextView>(R.id.tvScienceCategory)
                val tvTechCategory = view?.findViewById<TextView>(R.id.tvTechCategory)
                if (tvBreakingCategory.typeface?.style == Typeface.BOLD){
                    viewModel.getBreakingNews("ng")
                    isScrolling = false
                } else if(tvBusinessCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","business",businessPageCount+1)
                    isScrolling = false
                    businessPageCount++
                } else if(tvPoliticsCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","politics",politicsPageCount+1)
                    isScrolling = false
                    politicsPageCount++
                } else if(tvEntertainmentCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","entertainment",entertainmentPageCount+1)
                    isScrolling = false
                    entertainmentPageCount++
                }
                else if(tvSportCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","sport",sportPageCount+1)
                    isScrolling = false
                    sportPageCount++
                } else if(tvHealthCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","health",healthPageCount+1)
                    isScrolling = false
                    healthPageCount++
                }else if(tvScienceCategory?.typeface?.style == Typeface.BOLD){
                    viewModel.getCategoriesNews("ng","science",sciencePageCount+1)
                    isScrolling = false
                    sciencePageCount++
                }else {
                    viewModel.getCategoriesNews("ng","technology",techPageCount+1)
                    isScrolling = false
                    techPageCount++
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
    private fun setUpRecycleView() {
        newsAdapter = NewsAdapter()
        val rvBreakingNews =  view?.findViewById<RecyclerView>(R.id.rvBreakingNews)
        rvBreakingNews?.apply {
            this.adapter = newsAdapter
            this.layoutManager = LinearLayoutManager(activity)
           addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }


    private fun performNoticeCount(count:Int) {
        val tvBadges = view?.findViewById<TextView>(R.id.tvBadges)
        if (count == 0){
            tvBadges?.visibility = View.GONE
        } else{
            tvBadges?.text = count.toString()
        }
    }


    fun onTextClicked(view: View) {
        val clickedTextView = view as TextView
        val topBarLayout = requireView().findViewById<LinearLayout>(R.id.topBarLayout)

        for (i in 0 until topBarLayout.childCount) {
            val textView = topBarLayout.getChildAt(i) as TextView
            textView.setTypeface(null,Typeface.NORMAL)
        }

        clickedTextView.setTypeface(null,Typeface.BOLD)


        val category = clickedTextView.text.toString().toLowerCase()
        when(category) {
            "breaking" -> {
                viewModel.getBreakingNews("ng")
            }
            "business" -> {
                viewModel.getCategoriesNews("ng","business",businessPageCount)
            }
            "politics" -> {
                viewModel.getCategoriesNews("ng","politics",politicsPageCount)
            }
            "entertainment" -> {
                viewModel.getCategoriesNews("ng","entertainment",entertainmentPageCount)
            }
            "sport" -> {
                viewModel.getCategoriesNews("ng","sport",sportPageCount)
            }
            "health" -> {
                viewModel.getCategoriesNews("ng","health",healthPageCount)
            }
            "science" -> {
                viewModel.getCategoriesNews("us","science",sciencePageCount)
            }
            "technology" -> {
                viewModel.getCategoriesNews("us","technology",techPageCount)
            }
        }
    }
}







