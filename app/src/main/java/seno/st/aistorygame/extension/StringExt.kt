package seno.st.aistorygame.extension

import android.os.Build
import android.text.Html
import android.text.Spanned
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun <C, R> C?.ifNullOrEmpty(defaultValue: () -> R): R where R : CharSequence, C : R =
    if (this == null || isEmpty()) defaultValue() else this

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullAndNotEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullAndNotEmpty != null)
    }

    return this != null && isNotEmpty()
}

fun String.fromHtml(): Spanned{
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Html.fromHtml(this)
    } else {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    }
}

fun String.unicodeConvert(): String {
    val sb = java.lang.StringBuilder()
    var ch: Char
    val len = this.length
    var i = 0
    while (i < len) {
        ch = this[i]
        if (ch == '\\' && this[i + 1] == 'u') {
            sb.append(this.substring(i + 2, i + 6).toInt(16).toChar())
            i += 5
            i++
            continue
        }
        sb.append(ch)
        i++
    }
    return sb.toString()
}