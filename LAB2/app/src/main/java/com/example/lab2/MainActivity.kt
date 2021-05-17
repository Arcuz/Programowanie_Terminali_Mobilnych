package com.example.lab2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {
    private var currentOperation: String = " "
    var result: Float = 0f
    private var format: String = "Float"
    private val settingsRequestCode = 0x01
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.operationSpinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, p1: View?, position: Int, p3: Long
            ) {
                val opt1Button = findViewById<Button>(R.id.firstOperationButton)
                val opt2Button = findViewById<Button>(R.id.secondOperationButton)
                when (position) {
                    0 -> {
                        opt1Button.text = "+"
                        opt2Button.text = "-"
                    }
                    1 -> {
                        opt1Button.text = "*"
                        opt2Button.text = "/"
                    }
                    else -> {
                        opt1Button.text = "*"
                        opt2Button.text = "/"
                    }
                }
            }
        }
        val calcButton: Button = findViewById(R.id.calcButton)
        calcButton.setOnClickListener(this)
        calcButton.setOnLongClickListener(this)


        val fab = findViewById<FloatingActionButton>(R.id.clearButton)
        fab.setOnClickListener() {
            clearCalculator()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settingsItem -> startSettingsActivity()
            R.id.shareItem -> shareResult()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareResult() {
        val shareIntent: Intent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getExpressionResult())
        if(shareIntent.resolveActivity(packageManager) != null ){
            startActivity(shareIntent)
        }
    }

    private fun getExpressionResult(): String? {
        val firstVal = findViewById<EditText>(R.id.firstNumber).text.toString().toFloatOrNull() ?: 0f
        val secondVal = findViewById<EditText>(R.id.secondNumber).text.toString().toFloatOrNull() ?: 0f
        val equation = "${firstVal} $currentOperation ${secondVal} = "
        return when (currentOperation){
            "+" -> "$equation ${firstVal + secondVal}"
            "-" -> "$equation ${firstVal - secondVal}"
            "*" -> "$equation ${firstVal * secondVal}"
            "/" -> "$equation ${firstVal / secondVal}"
            else -> "error"
        }
    }

    private fun startSettingsActivity() {
        val intent: Intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(getString(R.string.numberformatkey), format)
        startActivityForResult(intent, settingsRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == settingsRequestCode && resultCode == Activity.RESULT_OK){
            format = data?.getStringExtra(getString(R.string.numberformatkey)) ?: "Float"
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun clearCalculator() {
        currentOperation = " "
        result = 0f
        updateOperation(currentOperation)
        updateResult(getString(R.string.result))
        findViewById<EditText>(R.id.firstNumber).text.clear()
        findViewById<EditText>(R.id.secondNumber).text.clear()
        Snackbar.make(
            findViewById(R.id.mainContainer),
            getString(R.string.clear_msg),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun selectOperation(view: View) {
        currentOperation = (view as Button).text.toString()
        updateOperation(currentOperation)
    }

    private fun updateOperation(currentOperation: String) {
        val operationTxt = findViewById<TextView>(R.id.operatorSymbol)
        operationTxt.text = currentOperation
    }

    override fun onClick(v: View?) {
        val firstVal =
            (findViewById<EditText>(R.id.firstNumber).text).toString().toFloatOrNull() ?: 0f
        val secondVal =
            (findViewById<EditText>(R.id.secondNumber).text).toString().toFloatOrNull() ?: 0f
        val resultStr: String = getResult(firstVal, secondVal)
        updateResult(resultStr)
    }

    private fun updateResult(resultStr: String) {
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        resultTextView.text = resultStr
    }

    private fun getResult(
        firstVal: Float,
        secondVal: Float,
        updateResult: Boolean = false
    ): String {
        val prevResult = if (updateResult) {
            result
        } else {
            if (currentOperation.equals("/") || currentOperation.equals("*")) 1f else 0f
        }
        result = when (currentOperation) {
            "+" -> prevResult + firstVal + secondVal
            "-" -> prevResult - firstVal - secondVal
            "*" -> prevResult * firstVal * secondVal
            "/" -> prevResult / firstVal / secondVal
            else -> 0f
        }
        return if (format == "Intiger") "${getString(R.string.result)} ${result.toInt()}"
        else "${getString(R.string.result)} $result"
    }

    override fun onLongClick(v: View?): Boolean {
        val firstVal =
            (findViewById<EditText>(R.id.firstNumber).text).toString().toFloatOrNull() ?: 0f
        val secondVal =
            (findViewById<EditText>(R.id.secondNumber).text).toString().toFloatOrNull() ?: 0f
        val resultStr: String = getResult(firstVal, secondVal, true)
        updateResult(resultStr)
        return true
    }
}