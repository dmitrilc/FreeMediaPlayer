package com.dimitrilc.freemediaplayer.ui.fragments.folderitems

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.dimitrilc.freemediaplayer.ui.adapter.MediaFolderItemAdapter
import com.dimitrilc.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.viewmodel.FolderItemsViewModel
import com.dimitrilc.freemediaplayer.ui.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "FOLDER_ITEMS_FRAGMENT"

@AndroidEntryPoint
abstract class FolderItemsFragment : Fragment() {
    private var _binding: FolderItemsFragmentBinding? = null
    private val binding get() = _binding!!

    private val folderItemsViewModel: FolderItemsViewModel by viewModels()
    private val appViewModel by activityViewModels<AppViewModel>()

    private val currentPath by lazy {
        requireArguments().getString(KEY_FULL_PATH)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FolderItemsFragmentBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    protected open fun prepareRecycler(){
        folderItemsViewModel.isAudio = isAudio()
        folderItemsViewModel.location = currentPath

        folderItemsViewModel.folderItemsUiState.observe(viewLifecycleOwner){
            binding.recyclerFolderItems.adapter = MediaFolderItemAdapter(it)
        }
    }

    fun onFolderItemClicked(position: Int) {
        val workUUID = appViewModel.generateGlobalPlaylistAndActiveItem(
            currentPath,
            position,
            isAudio()
        )

        folderItemsViewModel.getUpdateActiveMediaWorkInfoObservable("$workUUID").observe(viewLifecycleOwner){
            if (it.state.isFinished){
                navigateToPlayer()
            }
        }
    }

    override fun onDestroyView() {
        folderItemsViewModel.saveState()
        super.onDestroyView()
    }

    abstract fun isAudio(): Boolean
    abstract fun navigateToPlayer()
}