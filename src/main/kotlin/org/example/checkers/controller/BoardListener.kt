package org.example.checkers.controller

import org.example.checkers.core.Cell

interface BoardListener {
    fun update()
    fun boardClicked(cell: Cell)
    fun chipClicked(cell: Cell)
}