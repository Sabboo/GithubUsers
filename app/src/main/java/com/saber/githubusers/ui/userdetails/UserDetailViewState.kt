package com.saber.githubusers.ui.userdetails

import com.saber.githubusers.data.User

data class UserDetailViewState(
    val uiState: UIState = UIState.IDLE,
    val user: User? = null
)