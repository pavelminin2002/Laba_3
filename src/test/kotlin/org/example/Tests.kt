package org.example

import org.example.checkers.core.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.Test

class Tests {

    //Chip tests
    @Test
    fun chipTests() {
        val c: Chip
        c = Queen(ChipColor.BLACK)
        assertEquals(ChipColor.WHITE, c.color.opposite())
    }

    //Cell tests
    @Test
    fun isInside() {
        assertEquals(true, Cell(0, 0, CellColor.RED).isInside())
        assertEquals(false, Cell(-2, 2, CellColor.RED).isInside())
    }

    @Test
    fun isUp() {
        assertEquals(true, Cell(7, 2, CellColor.RED).isUp())
        assertEquals(false, Cell(-2, 2, CellColor.RED).isUp())
    }

    @Test
    fun isDown() {
        assertEquals(true, Cell(0, 2, CellColor.RED).isDown())
        assertEquals(false, Cell(-2, 2, CellColor.RED).isDown())
    }

    @Test
    fun distance() {
        val x = Cell(0, 2, CellColor.RED)
        val y = Cell(2, 0, CellColor.RED)
        assertEquals(x.distance(y), y.distance(x))
    }

    @Test
    fun equals() {
        val x = Cell(0, 2, CellColor.RED)
        val y = Cell(2, 0, CellColor.RED)
        assertEquals(true, x == Cell(0, 2, CellColor.RED))
        assertEquals(false, x == y)
    }

    @Test
    fun hC() {
        val x = Cell(0, 2, CellColor.RED)
        assertEquals(2, x.hashCode())
    }

    //Board tests

    @Test
    fun getNumber() {
        val board = Board()
        assertEquals(12, board.getNumberBlack())
        assertEquals(12, board.getNumberWhite())
    }

    @Test
    fun checkAround() {
        val board = Board()
        board.turn = ChipColor.BLACK
        assertEquals(false, board.checkAround(Cell(4, 3, CellColor.BROWN)))
        board.cells[3][2].chip = Chip(ChipColor.WHITE)
        assertEquals(true, board.checkAround(Cell(2, 1, CellColor.BROWN)))
    }

    @Test
    fun checkEat() {
        val board = Board()
        board.turn = ChipColor.BLACK
        assertEquals(false, board.checkEat(Cell(4, 3, CellColor.BROWN), 1, 1 , false))
        board.cells[3][2].chip = Chip(ChipColor.WHITE)
        assertEquals(true, board.checkAroundQueen(Cell(2, 1, CellColor.BROWN)))
    }

    @Test
    fun checkAroundQueen() {
        val board = Board()
        board.turn = ChipColor.BLACK
        assertEquals(false, board.checkAroundQueen(Cell(4, 3, CellColor.BROWN)))
        board.cells[2][1].chip = Queen(ChipColor.BLACK)
        board.cells[6][5].chip = null
        assertEquals(true, board.checkAroundQueen(Cell(2, 1, CellColor.BROWN)))
    }

    @Test
    fun clearBoard() {
        val board = Board()
        board.cells[3][4].color = CellColor.RED
        assertEquals(CellColor.RED, board.cells[3][4].color)
        board.clearBoard()
        assertEquals(CellColor.BROWN, board.cells[3][4].color)
    }

    @Test
    fun checkDiagonal() {
        val board = Board()
        board.turn = ChipColor.BLACK
        board.checkDiagonal(Cell(2,1,CellColor.BROWN), 1,1)
        assertEquals(CellColor.RED, board.cells[3][2].color)
        assertEquals(CellColor.RED, board.cells[4][3].color)
    }

    @Test
    fun checkDiagonalEat() {
        val board = Board()
        board.turn = ChipColor.BLACK
        board.cells[6][5].chip = null
        board.checkDiagonalEat(Cell(2,1,CellColor.BROWN), 1,1)
        assertEquals(CellColor.PINK, board.cells[5][4].color)
        assertEquals(CellColor.RED, board.cells[6][5].color)
        assertEquals(CellColor.BROWN, board.cells[7][6].color)
        board.cells[7][6].chip = null
        board.checkDiagonalEat(Cell(2,1,CellColor.BROWN), 1,1)
        assertEquals(CellColor.PINK, board.cells[5][4].color)
        assertEquals(CellColor.RED, board.cells[6][5].color)
        assertEquals(CellColor.RED, board.cells[7][6].color)
    }
}
