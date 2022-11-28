package com.saber.githubusers.ui.userslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.saber.githubusers.R
import com.saber.githubusers.databinding.FragmentUsersListBinding
import com.saber.githubusers.ui.adapter.LoadStateAdapter
import com.saber.githubusers.ui.adapter.UsersAdapter
import com.saber.githubusers.ui.showSnackBarWithAction
import com.saber.githubusers.utils.NetworkStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException


@AndroidEntryPoint
class UsersListFragment : Fragment(), (String) -> Unit {

    private var _binding: FragmentUsersListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsersViewModel by viewModels()
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSwipeToRefresh()
        observerNetworkConnectivity()
        handleSearchQueries()
    }

    private fun initAdapter() {
        adapter = UsersAdapter(this)

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter),
            footer = LoadStateAdapter(adapter)
        )

        lifecycleScope.launchWhenStarted {
            viewModel.users.collectLatest {
                adapter.submitData(it)
            }
        }

        handleRefreshState()
        handleInitialNotLoadingState()
        handleInitialNetworkErrorState()
    }

    private fun handleRefreshState() {
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { loadStates ->
                binding.swipeRefresh.isRefreshing =
                    loadStates.mediator?.refresh is LoadState.Loading
            }
        }
    }

    private fun handleInitialNotLoadingState() {
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }
    }

    private fun handleInitialNetworkErrorState() {
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter {
                    it.refresh is LoadState.Error &&
                            (it.refresh as LoadState.Error).error is IOException
                }
                .collect {
                    showNetworkError()
                }
        }
    }

    private fun initSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
    }

    private fun observerNetworkConnectivity() {
        NetworkStateManager.getNetworkConnectivityStatus()
            .observe(viewLifecycleOwner, activeNetworkStateObserver)
    }

    private val activeNetworkStateObserver: Observer<Boolean?> = Observer<Boolean?> { status ->
        if (status) retryFailedRequests() else showNetworkError()
    }

    private fun retryFailedRequests() {
        adapter.retry()
    }

    private fun showNetworkError() {
        binding.root.showSnackBarWithAction(
            getString(R.string.no_internet_connection),
            getString(R.string.retry)
        ) { retryFailedRequests() }
    }

    private fun handleSearchQueries() {
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.Default) {
                binding.searchInput.addTextChangedListener {
                    lifecycleScope.launchWhenStarted {
                        viewModel.searchUsers(it.toString())
                            .collectLatest { adapter.submitData(it) }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /* Navigate upon user click */
    override fun invoke(userName: String) {
        val action =
            UsersListFragmentDirections.actionUsersListFragmentToComposeContentFragment(userName)
        findNavController().navigate(action)
    }
}