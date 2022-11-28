package com.saber.githubusers.utils

import android.content.Context
import com.saber.githubusers.data.User
import java.io.File

fun User.checkCachedAvatarExists(context: Context): Boolean {
    return File(context.cacheDir.absolutePath + "/" + id.toString() + "-" + name + ".png").exists()
}

fun User.getCachedAvatarPath(context: Context): String {
    return context.cacheDir.absolutePath + "/" + id.toString() + "-" + name + ".png"
}