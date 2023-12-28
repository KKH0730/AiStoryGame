package seno.st.aistorygame.util

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.PopupWindow

class KeyboardHeightProvider(private val activity: Activity) : PopupWindow(activity),
    OnGlobalLayoutListener {
    private val rootView: View = View(activity)
    private var listener: HeightListener? = null
    private var heightMax = 0

    init {
        contentView = rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setBackgroundDrawable(ColorDrawable(0))
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        inputMethodMode = INPUT_METHOD_NEEDED
    }

    fun init(): KeyboardHeightProvider {
        if (!isShowing) {
            try {
                if (!activity.isFinishing && !activity.isDestroyed) {
                    val view: View = activity.window.decorView
                    view.post {
                        showAtLocation(view, Gravity.NO_GRAVITY, 0, 0)
                    }
                }
            } catch (e: Exception) {
            }
        }
        return this
    }

    fun setHeightListener(listener: HeightListener?): KeyboardHeightProvider {
        this.listener = listener
        return this
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom
        }
        val keyboardHeight = heightMax - rect.bottom
        listener?.onKeyboardHeightChanged(keyboardHeight)
    }

    fun release() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        dismiss()
    }

    interface HeightListener {
        fun onKeyboardHeightChanged(keyboardHeight: Int)
    }
}