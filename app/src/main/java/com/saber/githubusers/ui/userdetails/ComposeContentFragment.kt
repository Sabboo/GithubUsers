package com.saber.githubusers.ui.userdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.saber.githubusers.databinding.FragmentComposeContentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComposeContentFragment : Fragment() {

    private var _binding: FragmentComposeContentBinding? = null
    private val binding get() = _binding!!
    private val args: ComposeContentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeContentBinding.inflate(inflater, container, false)
        binding.composeContent.setContent {
            UserDetailsScreen(
                userName = args.userNameArg,
                navigationController = { findNavController().navigateUp() }
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}