package org.example.checkers.controller

import org.example.checkers.core.Cell

interface ChipListener {
    fun chipClicked(cell: Cell)
}