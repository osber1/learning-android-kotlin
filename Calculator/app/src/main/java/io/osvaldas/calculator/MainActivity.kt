package io.osvaldas.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null
    var lastNumeric = false
    var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInput = findViewById(R.id.tv_input)
    }

    fun onDigit(view: View) {
        tvInput?.append((view as Button).text)
        lastNumeric = true
        lastDot = false
    }

    fun onClear(view: View) {
        tvInput?.text = ""
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !lastDot) {
            tvInput?.append(".")
            lastDot = true
            lastNumeric = false
        }
    }

    fun onOperator(view: View) {
        tvInput?.text?.let {
            if (lastNumeric && !isOperatorAdded(it.toString())) {
                tvInput?.append((view as Button).text)
                lastNumeric = false
                lastDot = false
            }
        }
    }

    fun onEqual(view: View) {
        if (lastNumeric) {
            var prefix = ""
            var tvValue = tvInput?.text.toString()
            try {
                if (tvValue.startsWith("-")) {
                    prefix = "-"
                    tvValue = tvValue.substring(1)
                }

                when {
                    tvValue.contains("-") -> {
                        calculate(tvValue, prefix, "-")
                    }
                    tvValue.contains("+") -> {
                        calculate(tvValue, prefix, "+")
                    }
                    tvValue.contains("*") -> {
                        calculate(tvValue, prefix, "*")
                    }
                    else -> {
                        calculate(tvValue, prefix, "/")
                    }
                }
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun calculate(tvValue: String, prefix: String, action: String) {
        val splitValue = tvValue.split(action)
        var one = splitValue[0]
        val two = splitValue[1]
        if (prefix.isNotEmpty()) {
            one = prefix + one
        }
        when {
            action == "-" -> {
                tvInput?.text = removeZeroAfterDot((one.toDouble() - two.toDouble()).toString())
            }
            action == "+" -> {
                tvInput?.text = removeZeroAfterDot((one.toDouble() + two.toDouble()).toString())
            }
            action == "*" -> {
                tvInput?.text = removeZeroAfterDot((one.toDouble() * two.toDouble()).toString())
            }
            action == "/" -> {
                tvInput?.text = removeZeroAfterDot((one.toDouble() / two.toDouble()).toString())
            }
        }
    }

    private fun removeZeroAfterDot(result: String): String {
        var value = result
        if (value.contains(".0"))
            value = result.substring(0, result.length - 2)
        return value
    }

    private fun isOperatorAdded(value: String): Boolean {
        return if (value.startsWith("-")) {
            false
        } else {
            value.contains("/")
                    || value.contains("*")
                    || value.contains("+")
                    || value.contains("-")
        }
    }

}