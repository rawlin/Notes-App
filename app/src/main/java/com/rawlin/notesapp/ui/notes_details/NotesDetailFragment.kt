package com.rawlin.notesapp.ui.notes_details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import coil.Coil
import coil.load
import com.rawlin.notesapp.R
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.databinding.FragmentNotesDetailBinding
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.utils.BindingFragment
import com.rawlin.notesapp.utils.Constants.PICK_FROM_GALLARY
import com.rawlin.notesapp.utils.Resource
import com.rawlin.notesapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "NotesDetailFragment"

@AndroidEntryPoint
class NotesDetailFragment : BindingFragment<FragmentNotesDetailBinding>() {

    private val viewModel by viewModels<NotesDetailViewModel>()
    private var note: Note? = null
    private var pinnedNote: PinnedNote? = null
    private var isSharingEnabled = false
    private var shouldBePinned = false
    private var isImageAdded = false

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNotesDetailBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        note = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).note }
        pinnedNote = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).pinnedNote }
        setView()

        binding.apply {
            noteTitleEditText.doAfterTextChanged { titleText ->
                noteTitleTextView.text = titleText.toString()
            }


            saveNoteButton.setOnClickListener {
                val title = noteTitleEditText.text.toString()
                val message = noteMessageEditText.text.toString()
                if (pinnedNote != null) {
                    pinnedNote!!.id?.let { it1 ->
                        viewModel.updatePinnedNote(
                            title = title,
                            message = message,
                            id = it1,
                            imageUri = pinnedNote?.imageUri
                        )
                    }
                    return@setOnClickListener
                }
                if (note != null) {
                    note!!.id?.let { it1 ->
                        viewModel.updateNote(
                            title = title,
                            message = message,
                            id = it1,
                            imageUri = note?.imageUri
                        )
                    }
                    return@setOnClickListener
                }
                viewModel.createNote(title, message, note?.imageUri)
            }

            detailsSettingsButton.setOnClickListener {
                val destination = NotesDetailFragmentDirections.actionGlobalSettingsFragment()
                findNavController().navigateSafely(destination)
            }

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_addImage -> {
                        if (note?.imageUri == null && pinnedNote?.imageUri == null || !isImageAdded) {
                            val gallaryIntent = Intent().apply {
                                action = Intent.ACTION_GET_CONTENT
                                type = "image/*"
                            }
                            Log.d(TAG, "onOptionsItemSelected: Add Image")
                            startActivityForResult(gallaryIntent, PICK_FROM_GALLARY)
                            isImageAdded = true
                        } else {
                            removeImage()
                        }

                        true
                    }
                    R.id.action_delete -> {
                        note?.let { viewModel.deleteNote(it) }
                        pinnedNote?.let { viewModel.deletePinnedNote(it) }
                        Log.d(TAG, "Delete")
                        true
                    }
                    R.id.action_pin -> {
                        shouldBePinned = true
                        note?.let { pinNote(it) }
                        pinnedNote?.let { unPinNote(it) }
                        Log.d(TAG, "Pin")
                        true
                    }
                    R.id.action_share -> {
                        if (isSharingEnabled) {
                            note?.let { shareNote(it.title, it.message, it.imageUri) }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Enable sharing in settings to share",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.d(TAG, "Share")
                        true
                    }
                    else -> true
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.createNoteState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Note Created", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Resource.Loading -> {
                                //Show loading
                            }
                        }
                    }
                }

                launch {
                    viewModel.updateNoteState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Note Updated", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                                Log.d(TAG, "Note Updated")
                            }
                            is Resource.Loading -> Unit
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.isSharingEnabled.collect { isEnabled ->
                        isSharingEnabled = isEnabled
                    }
                }

                launch {
                    viewModel.deleteNoteStatus.collect { deleteStatus ->
                        when (deleteStatus) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    deleteStatus.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> Unit
                        }
                    }
                }

            }
        }


    }

    private fun shareNote(title: String, message: String, imageUri: String?) {

        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
//        if (imageUri != null) {
//            val uri = Uri.parse(imageUri)
//            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        }
        startActivity(Intent.createChooser(sharingIntent, "Share with"))

    }

    private fun unPinNote(pinnedNote: PinnedNote) {
        TODO("Not yet implemented")
    }

    private fun pinNote(note: Note) {
        binding.toolbar.menu.findItem(R.id.action_pin).title = "Unpin Note"
        viewModel.pinNote(note)
    }

    private fun removeImage() {
        binding.imageView.isVisible = false
        note = note?.copy(imageUri = null)
        pinnedNote = pinnedNote?.copy(imageUri = null)
        binding.toolbar.menu.findItem(R.id.action_addImage).title = "Add Image"
    }

    private fun setView() = with(binding) {
        if (pinnedNote != null) {
            noteTitleEditText.setText(pinnedNote?.title)
            noteTitleTextView.text = pinnedNote?.title
            noteMessageEditText.setText(pinnedNote?.message)
            if (pinnedNote?.imageUri != null) {
                setImage(Uri.parse(pinnedNote?.imageUri))
            }
            return@with
        }
        if (note != null) {
            noteTitleEditText.setText(note!!.title)
            noteTitleTextView.text = note?.title
            noteMessageEditText.setText(note!!.message)
            if (note?.imageUri != null) {
                setImage(Uri.parse(note!!.imageUri))
            }
        }

    }

    private fun setImage(imageUri: Uri?) {
        binding.imageView.load(imageUri)
        binding.imageView.isVisible = true
        binding.toolbar.menu.findItem(R.id.action_addImage).title = "Remove Image"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FROM_GALLARY) {
            val imageUri = data?.data ?: return
            updateImage(imageUri)
        }
    }

    private fun updateImage(imageUri: Uri) {
        setImage(imageUri)
        val newNote = note?.copy(imageUri = imageUri.toString())
        note = newNote
        val newPinnedNote = pinnedNote?.copy(imageUri = imageUri.toString())
        pinnedNote = newPinnedNote
    }
}