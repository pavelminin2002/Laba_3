package org.example.checkers.core

interface BoardListener {
    fun update()
    fun boardClicked(cell: Cell)
}