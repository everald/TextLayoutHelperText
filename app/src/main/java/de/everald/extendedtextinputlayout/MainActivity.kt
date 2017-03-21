package de.everald.extendedtextinputlayout

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ContentView(this))
    }


    class ContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
        val extendedTextInputLayout = ExtendedTextInputLayout(context).apply {
            this@ContentView.addView(this)
        }

        val editText = EditText(context).apply {
            addTextChangedListener(ErrorTextWatcher(extendedTextInputLayout))

            extendedTextInputLayout.addView(this)
        }

        val toogle = Switch(context).apply {
            isChecked = false
            setOnCheckedChangeListener { buttonView, isChecked ->
                extendedTextInputLayout.helperTextEnabled = isChecked
                if (helperText.isNullOrBlank()) {
                    helperText = "Helper text was set for this View."
                    extendedTextInputLayout.helperText = helperText!!
                }
            }
            this@ContentView.addView(this)
        }

        var helperText: CharSequence? = null

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            extendedTextInputLayout.layout(l, t, extendedTextInputLayout.measuredWidth, extendedTextInputLayout.measuredHeight)

            val toogleTop = (extendedTextInputLayout.measuredHeight - toogle.measuredHeight) / 2

            toogle.layout(extendedTextInputLayout.measuredWidth, toogleTop, r, toogleTop + toogle.measuredHeight)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
            val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

            toogle.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val maxViewWidth = measuredWidth - toogle.measuredWidth
            extendedTextInputLayout.measure(MeasureSpec.makeMeasureSpec(maxViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    class ErrorTextWatcher(val textInputLayout: TextInputLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (s.length % 5 == 0) {
                textInputLayout.error = "Found an error I can't explain."
            } else {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
    }
}
