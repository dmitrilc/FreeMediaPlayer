package com.example.freemediaplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.freemediaplayer.AdapterChildThumbnailLoad
import com.example.freemediaplayer.databinding.FolderItemsFragmentBinding
import com.example.freemediaplayer.viewmodel.VideoFolderItemsViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FOLDER_ITEMS_FRAGMENT"

@AndroidEntryPoint
abstract class FolderItemsFragment : Fragment() {
    private var _binding: FolderItemsFragmentBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FolderItemsFragmentBinding.inflate(inflater, container, false)
        prepareRecycler()
        getFolderItemsFromViewModel()
        return binding.root
    }

    abstract fun prepareRecycler()
    abstract fun getFolderItemsFromViewModel()
    abstract fun onFolderItemClicked(position: Int)

}