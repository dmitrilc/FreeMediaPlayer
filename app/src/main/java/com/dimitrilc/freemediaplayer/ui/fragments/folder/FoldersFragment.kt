package com.dimitrilc.freemediaplayer.ui.fragments.folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dimitrilc.freemediaplayer.databinding.FragmentFoldersFullBinding
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter
import com.dimitrilc.freemediaplayer.ui.viewmodel.FoldersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val KEY_FULL_PATH = "key.full.path"
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
        foldersViewModel.getFoldersUiStateMutable(isAudio()).observe(viewLifecycleOwner){ list ->
            binding.recyclerFoldersFull.adapter = FoldersFullAdapter(list){
                onFolderFullClicked(it)
            }
        }
    }

    private fun onFolderFullClicked(fullPathPos: Int){
        foldersViewModel.switchExpandedState(fullPathPos)
    }

    fun onFolderRelativeClicked(fullPathPos: Int, relativePathPos: Int){
        lifecycleScope.launch {
            val foldersUiState = foldersViewModel.foldersUiStateMutableLiveData.value!![fullPathPos]
            val pathParent = foldersUiState.parentPath
            val pathRelative = foldersUiState.relativePaths[relativePathPos]
            val fullPath = "$pathParent/$pathRelative"

            val navArgs = bundleOf(KEY_FULL_PATH to fullPath)
            navigateToFolderItems(fullPath, navArgs)
        }
    }

    override fun onDestroyView() {
        foldersViewModel.saveState()
        super.onDestroyView()
    }

    abstract fun navigateToFolderItems(fullPath: String, navArgs: Bundle)
    abstract fun isAudio(): Boolean
}