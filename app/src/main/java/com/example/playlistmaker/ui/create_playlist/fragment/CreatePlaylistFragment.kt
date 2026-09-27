package com.example.playlistmaker.ui.create_playlist.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreatePlaylistFragment : Fragment() {

    companion object {
        const val FILES_PATH = "playlistPictures"
    }

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel<CreatePlaylistViewModel>()

    private var onBackPressedCallback: OnBackPressedCallback? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    setImageArtwork(uri.toString(), binding.artwork)
                    saveImageToPrivateStorage(uri, "${UUID.randomUUID()}")
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolBar)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolBar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        viewModel.observeState().observe(viewLifecycleOwner){


            val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILES_PATH)
            val file = File(filePath, it.artwork)
            binding.apply {
                setImageArtwork(file.toUri().toString(), artwork)

                if (inputName.editText?.text?.isNotEmpty() == true)
                    createPlaylistButton.isEnabled = true
                else createPlaylistButton.isEnabled = false

            }

            prepareEditTextFields()

            onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true){
                if (viewModel.hasUnsavedChanges()){
                    MaterialAlertDialogBuilder(requireContext(), R.style.LightAlertDialog)
                        .setTitle(R.string.finish_playlist_creation_name)
                        .setMessage(R.string.finish_playlist_creation_desc)
                        .setNegativeButton(R.string.cancel) { dialog, which ->
                        }
                        .setPositiveButton(R.string.finish) { dialog, which ->
                            findNavController().popBackStack()
                        }
                        .show()
                }
                else findNavController().popBackStack()
            }
        }

        binding.artwork.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, name: String) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILES_PATH)

        val fileName = "${name}.jpg"
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)

        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use{outputStream ->
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }

        viewModel.onArtworkChanged(fileName)
    }


    private fun setImageArtwork(loadFrom: String, loadTo: ImageButton){
        val roundedCorners = dpToPx(8f, requireContext())
        Glide
            .with(this@CreatePlaylistFragment)
            .load(loadFrom)
            .placeholder(R.drawable.ic_add_photo)
            .error(R.drawable.ic_add_photo)
            .transform(CenterCrop(), RoundedCorners(roundedCorners))
            .into(loadTo)
    }
    private fun prepareEditTextFields(){
        val inputNameTextWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameInputChanged(s.toString())
            }
        }

        val inputDescTextWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onDescriptionInputChanged(s.toString())
            }
        }

        binding.apply {
            inputName.editText?.addTextChangedListener(inputNameTextWatcher)
            inputDesc.editText?.addTextChangedListener(inputDescTextWatcher)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}