package com.example.knews.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.adapters.GlideImageLoadingListener
import com.example.knews.repository.UserRepository
import com.example.knews.ui.SharedViewModel
import com.example.knews.ui.UserViewModel
import com.example.knews.ui.UserViewModelProviderFactory
import com.example.knews.util.Resource
import java.io.File

class UpdateUserFragment: Fragment(R.layout.fragment_update_user) {
    lateinit var viewModel : UserViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog
   private lateinit var profileEmail: String
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val application: Application = requireActivity().application
        val viewModelProviderFactory = UserViewModelProviderFactory(application,userRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[UserViewModel::class.java]
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")

        val profileImage = view.findViewById<ImageView>(R.id.imageView)
        val updateImage = view.findViewById<Button>(R.id.tvEmailAddress)
        val updatePassword = view.findViewById<TextView>(R.id.tvChangePassword)
        val email = view.findViewById<Button>(R.id.btnChange)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdateUserDetails)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            Glide.with(this)
                .load(user.image)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.avartar))
                .listener(GlideImageLoadingListener(progressBar))
                .into(profileImage)

            email.text = user.email
            etName.setText(user.name)
            etPhone.setText(user.phone)
            etCity.setText(user.city)
            profileEmail = user.email.toString()

            btnUpdate.setOnClickListener {
                if (etName.text.toString().isNotEmpty()&& etCity.text.toString().isNotEmpty()&& etCity.text.toString().isNotEmpty()){
                  val url = "user/update"
                    viewModel.updateUser(url,user.email.toString(),etName.text.toString(),etPhone.text.toString(),etCity.text.toString(),user.country.toString())
                } else {
                    Toast.makeText(requireContext(),"All Fields Are Required",Toast.LENGTH_LONG).show()
                }
            }

            profileImage.setOnClickListener {
                val imageDialog = ImageDialogFragment()
                imageDialog.show(parentFragmentManager,"ImageDialog")
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        viewModel.userDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                     findNavController().navigate(R.id.action_updateUserFragment2_to_accountDetailsFragment)
                    Toast.makeText(requireContext(),"User Details Updated Successfully",Toast.LENGTH_LONG).show()
                    response.data?.let {
                        sharedViewModel.userData.value = it.user
                    }
                    viewModel.userDetails.value = null

                }
                is Resource.Loading -> {
                      progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    viewModel.userDetails.value = null
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Error Updating User")
                    builder.setMessage(response.message.toString())
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                }
            }
        })

        viewModel.updateUserDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(),"User Details Updated Successfully",Toast.LENGTH_LONG).show()
                    response.data?.let {
                        sharedViewModel.userData.value = it.user
                    }
                    viewModel.updateUserDetails.value = null
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    viewModel.updateUserDetails.value = null
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton("Try Again"){ _,_ ->

                    }
                    builder.setTitle("Error Updating User Image")
                    builder.setMessage(response.message.toString())
                    builder.setIcon(R.drawable.baseline_error_24)
                    builder.create().show()
                }
            }
        })


        updatePassword.setOnClickListener {
            findNavController().navigate(R.id.action_updateUserFragment2_to_updateUserPasswordFragment)
        }

        updateImage.setOnClickListener {
            if (hasStoragePermission()){
                selectImage()
            } else{
                requestStoragePermission()
            }
        }
    }



    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK && data != null){
            val selectedImageUri : Uri? = data.data
            val contentResolver = requireContext().contentResolver
            selectedImageUri?.let {
                val selectedImageFie = createFileFromUri(it)
                val url = "user/update-image/$profileEmail"
                val fileType = contentResolver.getType(selectedImageUri)
                if (fileType == "image/jpeg" || fileType == "image/png"){
                    viewModel.updateImage(url,selectedImageFie)
                } else{
                    Toast.makeText(requireContext(),"Only Accept jpeg and png",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createFileFromUri(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri,filePathColumn,null,null,null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex ?:0)
        cursor?.close()

        return File(filePath ?: "")
    }

    private fun hasStoragePermission(): Boolean{
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_PERMISSION_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode,permission,grantResults)

        if (requestCode == REQUEST_PERMISSION_STORAGE){
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ){
                selectImage()
            } else{
                Toast.makeText(requireContext(),"Storage permission denied, cannot select image",Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object{
        private const val REQUEST_PERMISSION_STORAGE = 1
        private const val REQUEST_IMAGE_PICKER = 2
    }





}