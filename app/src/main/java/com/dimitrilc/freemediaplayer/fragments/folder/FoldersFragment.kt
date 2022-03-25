package com.dimitrilc.freemediaplayer.fragments.folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.dimitrilc.freemediaplayer.adapter.FoldersFullAdapter
import com.dimitrilc.freemediaplayer.databinding.FragmentFoldersFullBinding
import com.dimitrilc.freemediaplayer.entities.ui.ParentPath
import com.dimitrilc.freemediaplayer.entities.ui.ParentPathWithRelativePaths
import com.dimitrilc.freemediaplayer.viewmodel.FoldersViewModel
import com.dimitrilc.freemediaplayer.viewmodel.MediaItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val KEY_FULL_PATH = "key.full.path"
private const val TAG = "FOLDERS_FRAGMENT"

@AndroidEntryPoint
abstract class FoldersFragment : Fragment() {
    private var _binding: FragmentFoldersFullBinding? = null
    private val binding get() = _binding!!

    private val mediaItemsViewModel: MediaItemsViewModel by activityViewModels()
    protected val foldersViewModel: FoldersViewModel by viewModels()

    protected abstract val foldersLiveData: LiveData<List<ParentPathWithRelativePaths>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersFullBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        foldersLiveData.observe(viewLifecycleOwner){ list ->
            binding.recyclerFoldersFull.adapter = FoldersFullAdapter(list){
                onFolderFullClicked(it)
            }
        }
    }

    private fun onFolderFullClicked(fullPathPos: Int){
        foldersLiveData.value?.let { list ->
            val oldData = list[fullPathPos].parentPath
            val newData = ParentPath(
                id = oldData.id,
                isAudio = oldData.isAudio,
                parentPath = oldData.parentPath,
                isExpanded = !oldData.isExpanded
            )

            foldersViewModel.updateParentPath(newData)
        }
    }

    fun onFolderRelativeClicked(fullPathPos: Int, relativePathPos: Int){
        lifecycleScope.launch {
            foldersLiveData.value?.get(fullPathPos)?.let { folderData ->
                val pathParent = folderData.parentPath.parentPath
                val pathRelative = folderData.relativePaths[relativePathPos].relativePath
                val fullPath = "$pathParent/$pathRelative"

                mediaItemsViewModel.updateCurrentFolderFullPath(fullPath)

                val navArgs = bundleOf(KEY_FULL_PATH to fullPath)
                navigateToFolderItems(fullPath, navArgs)
            }
        }
    }

    abstract fun navigateToFolderItems(fullPath: String, navArgs: Bundle)
}