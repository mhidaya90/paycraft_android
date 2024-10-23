package com.paycraft.network

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No internet connection found!"
    // You can send any message whatever you want from here.
}