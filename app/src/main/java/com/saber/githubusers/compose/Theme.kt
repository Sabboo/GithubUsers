package com.saber.githubusers.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun OurAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val ourColorScheme = if (darkTheme) ourDarkColorScheme else ourLightColorScheme

    MaterialTheme(
        content = content,
        colorScheme = ourColorScheme
    )
}