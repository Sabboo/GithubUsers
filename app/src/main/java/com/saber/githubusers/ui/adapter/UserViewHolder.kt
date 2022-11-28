package com.saber.githubusers.ui.adapter

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import com.saber.githubusers.data.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.saber.githubusers.R
import com.saber.githubusers.databinding.UserItemBinding
import com.saber.githubusers.glide.InverseTransformation
import com.saber.githubusers.utils.checkCachedAvatarExists


class UserViewHolder(private val itemBinding: UserItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    private val cacheDirPath = itemBinding.root.context.cacheDir.absolutePath

    fun bind(user: User?, imageInverted: Boolean) {
        user?.let {
            itemBinding.apply {
                userNameTV.text = user.name.toString()
                userDetailsTV.text = itemBinding.root.context.getString(
                    R.string.user_details_tmp,
                    user.id,
                    user.type
                )
                noteImageView.visibility = if (user.note != null) VISIBLE else GONE
                Glide.with(userImageView.context)
                    .load(
                        if (user.checkCachedAvatarExists(userImageView.context))
                            "$cacheDirPath/${user.id}-${user.name}$IMAGE_FILE_EXTENSION"
                        else user.avatar
                    )
                    .apply(
                        if (imageInverted) CIRCLE_CROP_INVERSE_TRANSFORMATION else CIRCLE_CROP_TRANSFORMATION
                    )
                    .diskCacheStrategy(RESOURCE)
                    .into(userImageView)
            }
        }
    }

    companion object {
        val CIRCLE_CROP_INVERSE_TRANSFORMATION = RequestOptions()
            .transform(
                MultiTransformation(
                    InverseTransformation(),
                    CircleCrop()
                )
            )

        val CIRCLE_CROP_TRANSFORMATION = RequestOptions().transform(
            CircleCrop()
        )

        const val IMAGE_FILE_EXTENSION = ".png"
    }

}