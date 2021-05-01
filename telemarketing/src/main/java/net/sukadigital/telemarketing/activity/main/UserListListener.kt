package net.sukadigital.telemarketing.activity.main

import net.sukadigital.telemarketing.model.UserItem

interface UserListListener {
    fun onClickUser(item: UserItem)
}
