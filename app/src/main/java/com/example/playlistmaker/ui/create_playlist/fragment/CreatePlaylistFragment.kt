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
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.create_playlist.NavigateToCreatePlaylistFrom
import com.example.playlistmaker.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.ui.library.fragments.LibraryFragment
import com.example.playlistmaker.ui.library.fragments.PlaylistsFragment
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreatePlaylistFragment : Fragment() {

    companion object {
        private const val ARGS = "args"
        const val FILES_PATH = "playlistPictures"
        const val PLAYLIST_CREATED_KEY = "playlist_created_key"
        const val PLAYLIST_NAME_ARG_KEY = "playlist_name_key"
        fun createArgs(args: Playlist): Bundle = bundleOf(ARGS to args)
    }

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel<CreatePlaylistViewModel>()

    private var onBackPressedCallback: OnBackPressedCallback? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var args: Playlist? = null

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

        arguments?.let {
            args = it.getParcelable(ARGS)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolBar)
        binding.toolBar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        prepareEditTextFields()

        binding.artwork.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        viewModel.observeState().observe(viewLifecycleOwner){
            binding.apply {
                setImageArtwork(it.artwork, artwork)

                inputName.editText?.let { field ->
                    if (field.text.toString() != it.name) {
                        field.setText(it.name)
                    }
                }

                inputDesc.editText?.let { field ->
                    if (field.text.toString() != it.desc) {
                        field.setText(it.desc)
                    }
                }

                if (inputName.editText?.text?.isNotEmpty() == true && inputName.editText?.text?.isNotBlank() == true)
                    createPlaylistButton.isEnabled = true
                else createPlaylistButton.isEnabled = false
            }
        }

        if (args == null){
            activateCreatePlaylistMode()
        }
        else{
            activateEditPlaylistMode(args!!)
        }

    }

    private fun activateCreatePlaylistMode(){
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

        viewModel.observeFInishCreation().observe(viewLifecycleOwner){
            val messageRes = if (it.isSuccess) {
                R.string.playlist_created
            } else {
                R.string.playlist_creation_error
            }
            val message = getString(messageRes, it.playlistName)

            parentFragmentManager.setFragmentResult(
                PLAYLIST_CREATED_KEY,
                bundleOf(PLAYLIST_NAME_ARG_KEY to message)
            )

            findNavController().navigateUp()
        }

        binding.createPlaylistButton.setOnClickListener {
            viewModel.onCreateClicked()
        }
    }

    private fun activateEditPlaylistMode(playlist: Playlist){
        viewModel.fillStateWithData(playlist)

        binding.apply {
            toolBar.title = getString(R.string.edit)
            createPlaylistButton.text = getString(R.string.save)
            createPlaylistButton.setOnClickListener {
                viewModel.onUpdateClicked(playlist) { findNavController().popBackStack() }
            }
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

        viewModel.onArtworkChanged(file.toString())
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
        binding.apply {
            inputName.editText?.doOnTextChanged { text, start, before, count ->
                viewModel.onNameInputChanged(text.toString())
            }

            inputDesc.editText?.doOnTextChanged { text, start, before, count ->
                viewModel.onDescriptionInputChanged(text.toString())
            }
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