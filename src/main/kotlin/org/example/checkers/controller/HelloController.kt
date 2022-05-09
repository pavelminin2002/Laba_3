package org.example.checkers.controller

import org.example.checkers.core.*

class BasedChipListener(board: Board) : ChipListener {

    private var board: Board? = board

    override fun chipClicked(cell: Cell) {
        board?.makeTurn(cell)
    }
}

class BasedBoardListener(board: Board) : BoardListener {

    private var board: Board? = board

    override fun boardClicked(cell: Cell) {
        board?.turnMade(cell)
    }
    override fun update() {}
}