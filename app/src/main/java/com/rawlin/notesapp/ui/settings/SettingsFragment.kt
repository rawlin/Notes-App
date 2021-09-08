package com.rawlin.notesapp.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.rawlin.notesapp.databinding.FragmentSettingsBinding
import com.rawlin.notesapp.utils.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSettingsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apply {

                    launch {
                        pinMode.collect {
                            binding.enablePinModeSwitch.isChecked = it
                            Log.d(TAG, "1: $it")
                        }
                    }

                    launch {
                        showNewBottom.collect {
                            binding.enableBottomSwitch.isChecked = it
                            Log.d(TAG, "2: $it")
                        }
                    }

                    launch {
                        sharingMode.collect {
                            binding.enableSharingSwitch.isChecked = it
                            Log.d(TAG, "3: $it")
                        }
                    }

                    launch {
                        sortByCreatedTime.collect {
                            binding.sortByTimeCreatedSwitch.isChecked = it

                        }
                    }


                }
            }
        }

        binding.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            enablePinModeSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPinMode(isChecked)
            }
            enableBottomSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setShowNewBottom(isChecked)
                if (isChecked) {
                    viewModel.setSortByCreatedTime(false)
                }
            }
            enableSharingSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSharingMode(isChecked)
            }
            sortByTimeCreatedSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSortByCreatedTime(isChecked)
                if (isChecked) {
                    viewModel.setShowNewBottom(false)
                }
            }

        }
    }
}