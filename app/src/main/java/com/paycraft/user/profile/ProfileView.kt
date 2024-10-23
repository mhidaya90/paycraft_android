package com.paycraft.user.profile

import com.paycraft.base.BaseView

interface ProfileView : BaseView {
    fun onAccountDeleted(message: String)
    fun onAccountDeletionFailed(message: String)
}