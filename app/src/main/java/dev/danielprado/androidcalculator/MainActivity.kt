package dev.danielprado.androidcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import dev.danielprado.androidcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit private var layoutBinding: ActivityMainBinding
    lateinit private var display: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        display = layoutBinding.input
        setContentView(layoutBinding.root)

        // clear the edit on focus if it has the default text
        display.showSoftInputOnFocus = false
        display.setOnClickListener {
            if (getString(R.string.enter_a_value).equals(display.text.toString())) {
                display.setText("")
            }
        }
    }

    private fun addTextToDisplay(newStr: String) {
        val oldStr = display.text.toString()
        val cursorPos = display.selectionStart
        val leftStr = oldStr.substring(0, cursorPos)
        val rightStr = oldStr.substring(cursorPos)

        display.setText(
            if (getString(R.string.enter_a_value).equals(display.text.toString()))
                "$newStr"
            else
                "$leftStr$newStr$rightStr"
        )
        display.setSelection(cursorPos+1)
    }

    public fun numberButtonClicked(view: View) {
        addTextToDisplay((view as AppCompatButton).text.toString())
    }

    public fun clear(view: View) {
        display.setText(R.string.enter_a_value)
    }
}