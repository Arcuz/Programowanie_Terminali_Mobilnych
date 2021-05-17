package com.example.lab2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.iterator

class SettingsActivity : AppCompatActivity() {
    lateinit var numberFormat: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var numberFormat = intent.getStringExtra(getString(R.string.numberformatkey)) ?: "Float"
        when (numberFormat) {
            "Float" -> findViewById<RadioButton>(R.id.floatSelect).isChecked = true
            "Intiger" -> findViewById<RadioButton>(R.id.intigerSelect).isChecked = true
            else -> findViewById<RadioButton>(R.id.floatSelect).isChecked = true
        }

        val switch = findViewById<SwitchCompat>(R.id.enableSwitch)
        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            toggleEnable(
                isChecked
            )
        })

        val applyButton: Button = findViewById(R.id.applyButton)
        applyButton.setOnClickListener {
            applyChanges()
            finish()
        }
    }

    private fun applyChanges() {
        val returnIntent = Intent()
        returnIntent.putExtra(getString(R.string.numberformatkey),numberFormat)
        setResult(Activity.RESULT_OK, returnIntent)
    }

    private fun toggleEnable(checked: Boolean) {
        val radioGroup = findViewById<RadioGroup>(R.id.optionsGroup)
        for (item in radioGroup) {
            item.isEnabled = checked
        }
        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            numberFormat = findViewById<RadioButton>(id).text.toString()
        }
    }
}
