package com.example.lab1

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(localClassName, "onCreate")
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.rollButton)

        button.setOnClickListener { Toast.makeText(this, getString(R.string.Toast_msg), Toast.LENGTH_SHORT).show() }
        button.setOnClickListener {
            rollDice()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(localClassName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(localClassName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(localClassName, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(localClassName, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(localClassName, "onDestroy")
    }

    private fun rollDice2(b: Boolean) {
        val dice = Dice()
        val roll = dice.roll(b)
        val rollResultText = findViewById<TextView>(R.id.rollResultText)
        rollResultText.text = "${getString(R.string.rolled_msg)} ${roll}"
    }

    fun rollDice() {
        val dice = Dice()
        val roll = dice.roll()
        val roll2 = dice.roll()
        updateImg(roll, roll2)
        updateText(roll, roll2)
    }

    fun updateText(roll: Int, roll2: Int) {
        val rollResultText = findViewById<TextView>(R.id.rollResultText)
        rollResultText.text = "${getString(R.string.rolled_msg)} ${roll} & ${roll2}"
        val r: Int = (0..255).random()
        val g: Int = (((roll + roll2) / 12.0) * 255).toInt()
        val b: Int = (((roll * roll2) / 36.0) * 255).toInt()
        rollResultText.setTextColor(Color.rgb(r, g, b))
        Log.i(localClassName, "Text color: R:$r, G:$g, B:$b")
    }

    fun updateImg(roll: Int, roll2: Int) {
        val dice1Img: ImageView = findViewById(R.id.dice1Img)
        val dice2Img: ImageView = findViewById(R.id.dice2Img)

        dice1Img.setImageResource(resolveDrawable(roll))
        dice2Img.setImageResource(resolveDrawable(roll2))
    }

    fun resolveDrawable(value: Int): Int {
        return when (value) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1
        }
    }
}