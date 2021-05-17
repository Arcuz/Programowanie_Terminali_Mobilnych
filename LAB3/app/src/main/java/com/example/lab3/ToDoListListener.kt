package com.example.lab3

interface ToDoListListener{
    fun onItemClick(position: Int)
    fun onItemLongClick(position: Int)
}