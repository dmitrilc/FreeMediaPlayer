package com.example.freemediaplayer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.freemediaplayer.adapter.FoldersFullAdapter
import com.example.freemediaplayer.databinding.FragmentFoldersFullBinding
import com.example.freemediaplayer.viewmodel.AllFoldersViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FOLDERS_FRAGMENT"

@AndroidEntryPoint
abstract class FoldersFullFragment : Fragment() {
    private var _binding: FragmentFoldersFullBinding? = null
    private val binding get() = _binding!!

    protected val allFoldersViewModel: AllFoldersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersFullBinding.inflate(inflater, container, false)
        prepareRecycler()
        return binding.root
    }

    private fun prepareRecycler(){
        allFoldersViewModel.allFoldersLiveData.observe(viewLifecycleOwner){
            binding.recyclerFoldersFull.adapter = FoldersFullAdapter(it)
        }

        getFolderDataFromViewModel()
    }

    fun onFolderRelativeClicked(fullPathPos: Int, relativePathPos: Int){
        val folder = allFoldersViewModel.allFoldersLiveData.value?.get(fullPathPos)

        folder?.let { folderData ->
            val pathParent = folderData.parentPath
            val pathRelative = folderData.relativePaths[relativePathPos]
            val fullPath = "$pathParent/$pathRelative"

            navigateToFolderItems(fullPath)
        }
    }

    fun onFolderFullCardViewClicked(position: Int){
        allFoldersViewModel.refreshAllFoldersLiveData(position)
    }

    protected abstract fun navigateToFolderItems(fullPath: String)
    protected abstract fun getFolderDataFromViewModel()
}