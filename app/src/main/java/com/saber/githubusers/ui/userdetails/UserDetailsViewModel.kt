package com.saber.githubusers.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saber.githubusers.data.Result
import com.saber.githubusers.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(UserDetailViewState())
    val viewStateFlow: StateFlow<UserDetailViewState> = _viewStateFlow

    fun fetchUserDetails(userName: String) {
        viewModelScope.launch {
            repository.userDetails(userName).collectLatest { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> {
                        _viewStateFlow.value = UserDetailViewState(
                            uiState = if (result.data != null) UIState.CONTENT else UIState.ERROR,
                            user = result.data
                        )
                    }
                    Result.Status.ERROR -> {
                        _viewStateFlow.value = UserDetailViewState(uiState = UIState.ERROR)
                    }
                    Result.Status.LOADING -> {
                        _viewStateFlow.value = UserDetailViewState(uiState = UIState.LOADING)

                    }
                }
            }
        }
    }

    fun saveNote(note: String) {
        viewModelScope.launch {
            _viewStateFlow.value.user?.let {
                it.note = note
                repository.updateUserNote(it) }
        }
    }

}