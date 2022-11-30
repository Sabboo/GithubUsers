package com.saber.githubusers

import com.saber.githubusers.data.User

object MockUsersFactory {
    fun dummyUser() = User(
        1,
        "Dummy",
        "Type",
        "Path",
        10,
        20,
        "Note"
    )

    fun dummyUsers() = listOf<User>(
        User(
            1,
            "Dummy 1",
            "Type 1",
            "Path 1",
            10,
            20,
            "Note 1"
        ), User(
            2,
            "Dummy 2",
            "Type 2",
            "Path 2",
            10,
            20,
            "Note 2"
        ), User(
            3,
            "Dummy 3",
            "Type 3",
            "Path 3",
            10,
            20,
            "Note 3"
        )
    )
}