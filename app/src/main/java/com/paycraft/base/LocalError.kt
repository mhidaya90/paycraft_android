package com.paycraft.base

class LocalError(
    var message: String,
    var showMessage: Boolean = true,
    val navigateBack: Boolean = false
)