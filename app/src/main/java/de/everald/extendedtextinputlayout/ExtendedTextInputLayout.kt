package de.everald.extendedtextinputlayout

import android.content.Context
import android.content.res.ColorStateList
import android.support.design.widget.TextInputLayout
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.EditText
import android.widget.TextView

open class ExtendedTextInputLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextInputLayout(context, attrs, defStyleAttr) {

    private var errorTextEnabled = false

    private var _helperText: CharSequence = ""
    var helperText: CharSequence
        get() = _helperText
        set(value) {
            if (_helperText == value) {
                return
            }
            _helperText = value
            setHelperTextOnHelperView(value)
        }

    private var _helperTextAppearance = R.style.HelperTextAppearance
    var helperTextAppearance: Int
        get() = _helperTextAppearance
        set(value) {
            _helperTextAppearance = value
            helperTextView.setTextAppearance(value)
        }

    private var _helperTextColor: ColorStateList? = null
    var helperTextColor: ColorStateList?
        get() = _helperTextColor
        set(value) {
            _helperTextColor = value
            helperTextView.setTextColor(value)
        }

    private var _helperTextEnabled = false
    var helperTextEnabled: Boolean
        get() = _helperTextEnabled
        set(value) {
            if (_helperTextEnabled == value) return
            _helperTextEnabled = value
            switchHelperText()
        }

    private var helperTextView = TextView(context).apply {
        setTextAppearance(_helperTextAppearance)

        this@ExtendedTextInputLayout.addView(this)
    }


    override fun addView(child: View?) {
        super.addView(child)
        if (child is EditText) {
            helperTextView.setPaddingRelative(child.paddingStart, 0, child.paddingEnd, child.paddingBottom)
        }
    }

    override fun setErrorEnabled(enabled: Boolean) {
        if (errorTextEnabled == enabled) return
        errorTextEnabled = enabled

        if (errorTextEnabled && helperTextEnabled) { // hide helper text when error is shown
            switchHelperText()
        }

        super.setErrorEnabled(enabled)

        if (!errorTextEnabled) {
            switchHelperText()
        }
    }

    fun setHelperTextOnHelperView(text: CharSequence?) {
        helperTextAnimator?.cancel()
        if (text.isNullOrBlank()) {
            helperTextView.visibility = View.GONE
            if (helperTextView.text == text) return
            helperTextAnimator = helperTextView.animate()
                    .setInterpolator(Interpolator)
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        helperTextView.text = null
                    }

        } else {
            helperTextView.visibility = View.VISIBLE
            helperTextView.text = text
            helperTextAnimator = helperTextView.animate()
                    .setInterpolator(Interpolator)
                    .alpha(1f)
                    .setDuration(200)
        }
        helperTextAnimator?.start()
    }

    var helperTextAnimator: ViewPropertyAnimator? = null

    private fun switchHelperText() {
        if (errorTextEnabled || !_helperTextEnabled) {
            setHelperTextOnHelperView(null)
        } else if (!errorTextEnabled && _helperTextEnabled) { // show helper text
            if (helperText.isNotBlank()) {
                setHelperTextOnHelperView(helperText)
            } else {
                setHelperTextOnHelperView(null)
            }
        }
    }

    companion object {
        val Interpolator = FastOutSlowInInterpolator()
    }
}