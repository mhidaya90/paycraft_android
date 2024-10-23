package com.paycraft.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SpManager private constructor() {

    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context, name: String) {
        sharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                name,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
    }

    fun editor(): SharedPreferences.Editor {
        return sharedPreferences!!.edit()
    }

    fun preference(): SharedPreferences? {
        return sharedPreferences
    }

    fun remove(key: String) {
        sharedPreferences!!.edit().remove(key).apply()
    }

    operator fun contains(key: String): Boolean {
        return sharedPreferences!!.contains(key)
    }

    fun clear() {
        sharedPreferences!!.edit().clear().apply()
    }

    companion object {

        private val ourInstance = SpManager()

        fun instance(): SpManager {
            return ourInstance
        }
    }

}