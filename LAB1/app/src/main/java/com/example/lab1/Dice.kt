package com.example.lab1

import kotlin.random.Random

class Dice(private val numSides: Int = 6) {
    fun roll(isRangesRandom: Boolean = true): Int{
        if(isRangesRandom){
            return (1..numSides).random()
        }else{
            return Random.nextInt() + 1
        }
    }
}