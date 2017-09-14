package com.example.kb.calculator

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var calcResult: String = "0"
    private var resultNumeric: Double = 0.0
    private var entirePart: String = "0"
    private var decimalPart: String = "0"
    private var isDecimalInput: Boolean = false
    private var cachedValue: Double = 0.0

    private val resultBox: EditText by bind(R.id.resultBox)
    private val bttnAC: Button by bind(R.id.bttnAC)
    private val bttnDivide: Button by bind(R.id.divide)
    private val bttnMultiply: Button by bind(R.id.multiply)
    private val bttnPlusMinus: Button by bind(R.id.plusMinus)
    private val bttnPlus: Button by bind(R.id.plus)
    private val bttnMinus: Button by bind(R.id.minus)
    private val bttnDot: Button by bind(R.id.dot)
    private val bttnEqual: Button by bind(R.id.equal)
    private val bttnPercentage: Button by bind(R.id.percentage)
    private var lastOperation: String = Operator.NONE

    private val buttons: Array<Lazy<Button>> = bind(arrayOf(R.id.bttn0, R.id.bttn1, R.id.bttn2,
            R.id.bttn3, R.id.bttn4, R.id.bttn5, R.id.bttn6, R.id.bttn7, R.id.bttn8, R.id.bttn9))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNumbersListener()
        setButtonsWhichNeedCache()
        setOtherButtons()
        refreshResult()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setButtonsWhichNeedCache() {
        bttnPlus.setOnClickListener {
            performLastOperation()
            cachedValue = resultNumeric
            resetResultNumeric()
            lastOperation = Operator.PLUS
        }
        bttnEqual.setOnClickListener {
            performLastOperation()
            resetResultNumeric()
            lastOperation = Operator.NONE
        }
    }

    private fun setOtherButtons() {
        bttnAC.setOnClickListener { resetResults() }
        bttnPlusMinus.setOnClickListener {
            if (resultNumeric != 0.0) {
                resultNumeric *= -1
            }
            calcResult = resultNumeric.toString()
            refreshResult()
        }
        bttnDot.setOnClickListener {
            if (resultNumeric % 1 == 0.0) {
                isDecimalInput = true
                refreshResult()
            }
        }
    }

    private fun setNumbersListener() {
        buttons.forEachIndexed { index, lazy ->
            lazy.value.setOnClickListener({
                appendNumber(index)
                refreshResult()
            })
        }
    }

    private fun appendNumber(number: Int) {
        if (isDecimalInput) {
            appendDecimalNumber(number)
        } else {
            entirePart += number.toString()
        }
        calcResult = entirePart + "." + decimalPart
        Log.v("My class: ", "is decimal input:${isDecimalInput}$entirePart . $decimalPart")
        try {
            resultNumeric = calcResult.toDouble()
            calcResult = resultNumeric.toString()
        } catch (e: NumberFormatException) {
            calcResult.dropLast(1)
            Toast.makeText(this, R.string.reached_max_number, Toast.LENGTH_SHORT).show()
        }
    }

    private fun appendDecimalNumber(number: Int) {
        val decimalNumber = decimalPart.toLong()
        if (decimalNumber == 0L) {
            decimalPart = number.toString()
        } else {
            decimalPart += number.toString()
        }
    }

    private fun refreshResult() {
        resultBox.setText(calcResult)
    }

    private fun resetResultNumeric() {
        isDecimalInput = false
        resultNumeric = 0.0
        entirePart = "0"
        decimalPart = "0"
    }

    private fun resetResults() {
        resultNumeric = 0.0
        cachedValue = 0.0
        resetResultNumeric()
        calcResult = resultNumeric.toString()
        resultBox.setText(calcResult)
    }

    private fun performLastOperation() {
        when (lastOperation) {
            Operator.NONE -> {
                resultNumeric = calcResult.toDouble()
                return
            }
            Operator.PLUS -> {
                resultNumeric += cachedValue
            }
            Operator.MINUS -> {
                resultNumeric = cachedValue - resultNumeric
            }
            Operator.MULTIPLY -> {
                resultNumeric *= cachedValue
            }
            Operator.DIVIDE -> {
                if (resultNumeric != 0.0) {
                    resultNumeric = cachedValue / resultNumeric
                } else {
                    Toast.makeText(this, R.string.divide_zero, Toast.LENGTH_SHORT).show()
                }
            }
        }
        lastOperation = Operator.NONE
        cachedValue = 0.0
        calcResult = resultNumeric.toString()
        refreshResult()
    }

    private fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
    }

    private fun <T : View> Activity.bind(@IdRes resources: Array<Int>): Array<Lazy<T>> {
        return resources.map { bind<T>(it) }.toTypedArray()
    }
}




