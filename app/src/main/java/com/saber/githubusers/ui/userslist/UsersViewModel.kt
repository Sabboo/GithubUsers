package com.saber.githubusers.ui.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.saber.githubusers.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val repository: UsersRepository) :
    ViewModel() {

    val users = repository.users(0)
        .cachedIn(viewModelScope)

    fun searchUsers(query: String) = repository.searchUsers(query)

}