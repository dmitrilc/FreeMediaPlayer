package com.dimitrilc.freemediaplayer.ui.fragments.folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dimitrilc.freemediaplayer.databinding.FragmentFoldersFullBinding
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter
import com.dimitrilc.freemediaplayer.ui.viewmodel.FoldersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "FOLDERS_FRAGMENT"

@AndroidEntryPoint
abstract class FoldersFragment : Fragment() {
    private var _binding: FragmentFoldersFullBinding? = null
    private val binding get() = _binding!!

    private val foldersViewModel by viewModels<FoldersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersFullBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        foldersViewModel.isAudio = isAudio()

        foldersViewModel.navigator = { fullPath, navArgs ->
            navigateToFolderItems(fullPath, navArgs)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                foldersViewModel.uiState.collect {
                    binding.recyclerFoldersFull.adapter = FoldersFullAdapter(it)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        foldersViewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        foldersViewModel.saveState()
        super.onDestroyView()
    }

    abstract fun navigateToFolderItems(fullPath: String, navArgs: Bundle)
    abstract fun isAudio(): Boolean
}