package com.example.knews.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.adapters.GlideImageLoadingListener
import com.example.knews.ui.SharedViewModel

class ImageDialogFragment : DialogFragment(R.layout.fragment_image_dialog) {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = view.findViewById<ImageView>(R.id.ivImage)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar2)
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer { user->
            Glide.with(this)
                .load(user.image)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.avartar))
                .listener(GlideImageLoadingListener(progressBar))
                .into(image)
        })
    }
}