package com.saber.githubusers.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.saber.githubusers.data.User
import com.saber.githubusers.databinding.UserItemBinding


class UsersAdapter(private val onItemClick: (String) -> Unit) :
    PagingDataAdapter<User, UserViewHolder>(USER_COMPARATOR) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position), (position + 1) % 4 == 0)
        holder.itemView.rootView.setOnClickListener { onItemClick.invoke(getItem(position)?.name!!) }
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemBinding: UserItemBinding = UserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemBinding)
    }

    companion object {
        val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean =
                oldItem.id == newItem.id
        }
    }
}
