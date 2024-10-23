package com.paycraft.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.text.InputFilter
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.gms.common.internal.Preconditions.checkMainThread
import com.paycraft.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@Suppress("NOTHING_TO_INLINE")
inline fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

@Suppress("NOTHING_TO_INLINE")
inline infix fun Activity.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()


@Suppress("NOTHING_TO_INLINE")
inline infix fun Fragment.toast(text: String) =
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()

fun Activity.dialog(
    message: String,
    yesButtonName: String,
    noButtonName: String,
    yesAction: DialogInterface.OnClickListener
) {
    dialog(this, message, yesButtonName, noButtonName, yesAction)
}

fun Fragment.dialog(
    message: String,
    yesButtonName: String,
    noButtonName: String,
    yesAction: DialogInterface.OnClickListener
) {
    context?.let { dialog(it, message, yesButtonName, noButtonName, yesAction) }
}

fun dialog(
    context: Context, message: String,
    yesButtonName: String,
    noButtonName: String,
    yesAction: DialogInterface.OnClickListener
) {
    val dialog = AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton(yesButtonName, yesAction)
        .setNegativeButton(noButtonName, null)
        .show()

    (dialog.findViewById(android.R.id.message) as TextView?)?.typeface =
        ResourcesCompat.getFont(context, R.font.black)
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).typeface =
        ResourcesCompat.getFont(context, R.font.bold)
    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).typeface =
        ResourcesCompat.getFont(context, R.font.bold)
}

fun Activity.isEmulator(): Boolean {
    return Build.MANUFACTURER.contains("Genymotion")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.toLowerCase().contains("droid4x")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.HARDWARE == "goldfish"
            || Build.HARDWARE == "vbox86"
            || Build.HARDWARE.toLowerCase().contains("nox")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.PRODUCT == "sdk"
            || Build.PRODUCT == "google_sdk"
            || Build.PRODUCT == "sdk_x86"
            || Build.PRODUCT == "vbox86p"
            || Build.PRODUCT.toLowerCase().contains("nox")
            || Build.BOARD.toLowerCase().contains("nox")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.toRupee() = "${SessionManager.instance().getCurrency()} $this"


fun String.isValidatePassword(): String {
    return if (this.isEmpty()) {
        return "Password is empty!"
    } else if (!this.matches(Regex(".*[0-9].*"))) {
        return "Password should contain at least 1 digit"
    } else if (!this.matches(Regex(".*[a-z].*"))) {
        return "Password should contain at least 1 lower case letter"
    } else if (!this.matches(Regex(".*[A-Z].*"))) {
        return "Password should contain at least 1 upper case letter"
    } else if (!this.matches(Regex(".*[a-zA-Z].*"))) {
        return "Password should contain a letter"
    } else if (!this.matches(Regex(".{8,}"))) {
        return "Password should contain 8 characters"
    } else {
        ""
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

@Suppress("NOTHING_TO_INLINE")
inline fun CharSequence?.isValidPhone() = !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()

@JvmSynthetic
inline fun ImageView.loadBase64(base64: String) {
    val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
    val decodedByte =
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    load(decodedByte)
}

fun View.disable() {
    alpha = 0.5f
    isEnabled = false
}

fun View.enable() {
    alpha = 1.0f
    isEnabled = true
}

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        checkMainThread("")
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun EditText.restrictDecimalTo(count: Int) {
    val decimalPattern = "\\d*\\.?\\d{0,${count}}"
    val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val inputText = dest.toString() + source.toString()
        if (inputText.matches(decimalPattern.toRegex())) {
            null // Accept the input
        } else {
            "" // Reject the input
        }
    }
    val filters = this.filters.toMutableList()
    filters.add(inputFilter)
    this.filters = filters.toTypedArray()
}

fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            result = when (type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }

        }
    }
    return result
}