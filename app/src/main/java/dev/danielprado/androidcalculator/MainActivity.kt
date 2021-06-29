package dev.danielprado.androidcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.snackbar.Snackbar
import dev.danielprado.androidcalculator.databinding.ActivityMainBinding
import java.lang.ArithmeticException
import java.util.*
import kotlin.math.min

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

        // up to 8 digits
        if (oldStr.length == 8)
            return;

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
        val displayText = layoutBinding.display.text.toString()
        if (displayText.isEmpty())
            display.setText(R.string.enter_a_value)
        else
            display.setText(displayText.substring(0, getOperatorIndex(displayText)).trim())

        layoutBinding.display.text = ""
    }

    public fun clearAll(view: View) {
        layoutBinding.apply {
            input.setText(R.string.enter_a_value)
            display.text = ""
        }
    }

    public fun onInvertSignalClick(view: View) {
        val inputText = layoutBinding.input.text.toString()
        if (getString(R.string.enter_a_value).equals(inputText))
            return

        val newInputText = if (inputText[0] == '-') inputText.substring(1) else "-$inputText"
        layoutBinding.input.apply {
            setText(newInputText)
            setSelection(newInputText.length)
        }
    }

    private fun getOperatorIndex(value: String): Int {
        return value.indexOfAny(charArrayOf('+', '-', '÷'), 1)
    }

    private fun calculate(): String? {
        val displayText = layoutBinding.display.text.toString()
        val operatorIdx = getOperatorIndex(displayText)
        if (operatorIdx > -1) {
            val num1 = displayText.substring(0, operatorIdx).trim().toFloat()
            val num2 = layoutBinding.input.text.toString().toFloat()
            val result = when(displayText[operatorIdx]) {
                '+' -> (num1 + num2).toString()
                '-' -> (num1 - num2).toString()
                else -> {
                    if (num2.equals(0f))
                            throw ArithmeticException("Divisão por zero")
                    (num1 / num2).toString()
                }
            }

            return if (result[0] == '-')
                result.substring(0, min(result.length, 9))
            else result.substring(0, min(result.length, 8))
        }
        else
            return null
    }

    public fun operationClicked(view: View) {
        val inputValue = layoutBinding.input.text.toString()
        if (getString(R.string.enter_a_value).equals(inputValue))
            return

        val operation = (view as AppCompatButton).text.toString()
        try {
            val newDisplayText = "${calculate() ?: inputValue} $operation"
            layoutBinding.display.text = newDisplayText
            layoutBinding.input.setText(R.string.enter_a_value)
        }
        catch (e: ArithmeticException) {
            Snackbar.make(layoutBinding.root, "${e.message}", Snackbar.LENGTH_SHORT).show()
        }
    }

    public fun equalOperatorClicked(view: View) {
        val inputValue = layoutBinding.input.text.toString()
        val displayValue = layoutBinding.display.text.toString()

        // não há nenhuma operação a ser exibida
        if (displayValue.isEmpty())
            return
        // O input não tem nenhum número pra ser operado
        else if (getString(R.string.enter_a_value).equals(inputValue)) {
            layoutBinding.apply {
                val newInputValue = displayValue.substring(0, getOperatorIndex(displayValue)).trim()
                input.setText(newInputValue)
                display.text = ""
                input.setSelection(newInputValue.length)
            }
        }
        // O display tem uma operação pendente e há um número no input
        else {
            try {
                var calcResult = calculate()!!
                layoutBinding.input.setText(calcResult)
                layoutBinding.display.text = ""
                layoutBinding.input.setSelection(calcResult.length)
            }
            catch (e: ArithmeticException) {
                Snackbar.make(layoutBinding.root, "${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}