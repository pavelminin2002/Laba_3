package org.example.checkers.controller

import org.example.checkers.core.*


class BasedBoardListener : BoardListener {

    var board = Board()
    var inProcess: Boolean = true

    fun changeTurn() {
        board.changeTurn()
    }

    fun boardClicked(cell: Cell) {
        board.turnMade(cell, inProcess)
    }

    fun chipClicked(cell: Cell) {
        if (cell.chip is Queen) {
            board.makeTurnQueen(cell, inProcess)
        } else {
            board.makeTurn(cell, inProcess)
        }
    }

    override fun update() {}
}