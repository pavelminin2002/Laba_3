package org.example.checkers.core

import org.example.checkers.controller.BoardListener
import kotlin.math.sin


class Board {
    private var bcn = 0 //BlackChipsNumber
    private var wcn = 0 //WhiteChipsNumber
    var turn = ChipColor.WHITE
    var cells = mutableListOf<List<Cell>>()
    private var listener: BoardListener? = null
    private val readyEat = mutableListOf<Cell>()
    fun registerListener(listener: BoardListener) {
        this.listener = listener
    }

    init {
        for (row in 0 until 8) {
            val list = mutableListOf<Cell>()
            for (column in 0 until 8) {
                val c = if (row % 2 == column % 2) CellColor.IVORY else CellColor.BROWN
                val cell = Cell(row, column, c)
                if (bcn != 12 && c == CellColor.BROWN) {
                    cell.chip = Chip(ChipColor.BLACK)
                    bcn++
                }
                if (wcn != 12 && c == CellColor.BROWN && row >= 5) {
                    cell.chip = Chip(ChipColor.WHITE)
                    wcn++
                }
                list.add(cell)
            }
            cells.add(list)
        }
    }

    fun changeTurn() {
        checkEatAll()
        if (readyEat.isEmpty()) {
            turn = turn.opposite()
            listener?.update()
        }
    }

    fun clearBoard() {
        for (i in cells.indices) {
            for (j in cells[i].indices) {
                val c = if (i % 2 == j % 2) CellColor.IVORY else CellColor.BROWN
                cells[i][j].color = c
            }
        }
    }

    fun makeTurn(cell: Cell, sw: Boolean) {
        val oneWay: Boolean
        clearBoard()
        val x = if (turn == ChipColor.WHITE) -1 else 1
        if (turn == cell.chip?.color) {
            checkEatAll()
            cells[cell.x][cell.y].color = CellColor.YELLOW
            if (!(readyEat.isNotEmpty() && cell !in readyEat)) {
                oneWay = checkAround(cell)
                if (!oneWay) {
                    val goal1 = Cell(cell.x + x, cell.y + 1, CellColor.BROWN)
                    val goal2 = Cell(cell.x + x, cell.y - 1, CellColor.BROWN)
                    if (goal1.isInside() && cells[goal1.x][goal1.y].chip == null)
                        cells[goal1.x][goal1.y].color = CellColor.RED
                    if (goal2.isInside() && cells[goal2.x][goal2.y].chip == null)
                        cells[goal2.x][goal2.y].color = CellColor.RED
                }
            }
        }
        if (sw) listener!!.update()
    }

    fun turnMade(cell: Cell, sw: Boolean) {
        if (cell.color == CellColor.RED) {
            var delPink: Cell? = null
            var distance: Int
            var finalDistance = 10
            var maybeChangeTurn = true
            var changeTurn = true
            var createQueen = false
            if ((turn == ChipColor.BLACK && cell.isUp()) || (turn == ChipColor.WHITE && cell.isDown())) {
                cells[cell.x][cell.y].chip = Queen(turn)
                createQueen = true
            }
            for (i in cells.indices) {
                for (j in cells[i].indices) {
                    if (cells[i][j].color == CellColor.YELLOW) {
                        if (!createQueen) {
                            if (cells[i][j].chip is Queen) {
                                cells[cell.x][cell.y].chip = Queen(turn)
                            } else {
                                cells[cell.x][cell.y].chip = Chip(turn)
                            }
                        }
                        cells[i][j].chip = null
                    }
                    if (cells[i][j].color == CellColor.PINK) {
                        distance = cell.distance(cells[i][j])
                        if (distance < finalDistance) {
                            finalDistance = distance
                            delPink = cells[i][j]
                        }
                        maybeChangeTurn = false
                    }
                }
            }
            if (delPink != null) cells[delPink.x][delPink.y].chip = null
            if (!maybeChangeTurn) {
                if (turn == ChipColor.WHITE) bcn--
                else wcn--
                if (cells[cell.x][cell.y].chip is Queen) makeTurnQueen(cell, sw)
                else makeTurn(cell, sw)
                for (i in cells.indices) {
                    for (j in cells[i].indices) {
                        if (cells[i][j].color == CellColor.PINK) {
                            changeTurn = false
                        }
                    }
                }
            }
            if (changeTurn) turn = turn.opposite()
            clearBoard()
        }
        if (sw) listener!!.update()
    }

    fun checkEat(goalN: Cell, x: Int, y: Int, oneWay: Boolean): Boolean {
        if (goalN.isInside()) {
            if (cells[goalN.x][goalN.y].chip != null && cells[goalN.x][goalN.y].chip?.color != turn) {
                val goal = Cell(goalN.x + x, goalN.y + y, CellColor.BROWN)
                val goalbef = Cell(goalN.x - x, goalN.y - y, CellColor.BROWN)
                if (cells[goalbef.x][goalbef.y].chip != null && cells[goalbef.x][goalbef.y].chip?.color != turn)
                    if (!oneWay) return false
                if (goal.isInside() && cells[goal.x][goal.y].chip == null) {
                    cells[goalN.x][goalN.y].color = CellColor.PINK
                    cells[goal.x][goal.y].color = CellColor.RED
                    return true
                }
            }
        }
        if (!oneWay) return false
        return true
    }

    private fun checkEatAll() {
        readyEat.clear()
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                if (cells[i][j].chip != null && cells[i][j].chip?.color == turn) {
                    if (cells[i][j].chip !is Queen) {
                        val result = checkAround(cells[i][j])
                        if (result) readyEat.add(cells[i][j])
                    } else {
                        val result = checkAroundQueen(cells[i][j])
                        if (result) readyEat.add(cells[i][j])
                    }
                }
            }
        }
        clearBoard()
    }

    fun checkAround(cell: Cell): Boolean {
        var result = false
        for (i in 0..3) {
            var par = chooseDirection(i)
            result = checkEat(
                Cell(cell.x + par.first, cell.y + par.second, CellColor.BROWN),
                par.first,
                par.second,
                result
            )
        }
        return result
    }

    private fun chooseDirection(i: Int): Pair<Int, Int> {
        val sings: Pair<Int, Int> = when (i) {
            0 -> {
                1 to 1
            }
            1 -> {
                1 to -1
            }
            2 -> {
                -1 to 1
            }
            else -> {
                -1 to -1
            }
        }
        return sings
    }

    fun makeTurnQueen(cell: Cell, sw: Boolean) {
        clearBoard()
        if (turn == cell.chip?.color) {
            checkEatAll()
            cells[cell.x][cell.y].color = CellColor.YELLOW
            if (readyEat.isNotEmpty() && cell in readyEat) {
                for (i in 0..3) {
                    var par = chooseDirection(i)
                    checkDiagonalEat(cell, par.first, par.second)
                }
            } else if (readyEat.isEmpty()) {
                for (i in 0..3) {
                    var par = chooseDirection(i)
                    checkDiagonal(cell, par.first, par.second)
                }
            }
        }
        if (sw) listener!!.update()
    }

    fun checkAroundQueen(cell: Cell): Boolean {
        var result = false
        var x = 0
        var y = 0
        for (i in 0 until 8) {
            x++
            y++
            for (i in 0..3) {
                var par = chooseDirection(i)
                result = checkEat(
                    Cell(cell.x + x * par.first, cell.y + y * par.second, CellColor.BROWN),
                    par.first,
                    par.second,
                    result
                )
            }
            if (result) return true
        }
        return false
    }

    fun checkDiagonalEat(cell: Cell, x: Int, y: Int) {
        var cellX = 0
        var cellY = 0
        var xx = x
        var yy = y
        var maybeEat = false
        var chipAfter = false
        for (i in 0 until 8) {
            val newCell = Cell(cell.x + xx, cell.y + yy, CellColor.BROWN)
            if (!newCell.isInside()) break
            if (cells[newCell.x][newCell.y].chip != null && cells[newCell.x][newCell.y].chip?.color == turn) break
            if (maybeEat) {
                if (cells[newCell.x][newCell.y].chip != null) {
                    if (chipAfter) {
                        cells[cellX][cellY].color = CellColor.BROWN
                    }
                    break
                }
                cells[newCell.x][newCell.y].color = CellColor.RED
                chipAfter = false
            }
            if (cells[newCell.x][newCell.y].chip != null && cells[newCell.x][newCell.y].chip?.color != turn) {
                if (newCell.x != 0 && newCell.x != 7 && newCell.y != 0 && newCell.y != 7) {
                    cells[newCell.x][newCell.y].color = CellColor.PINK
                    maybeEat = true
                    chipAfter = true
                    cellX = newCell.x
                    cellY = newCell.y
                } else break
            }
            xx += x
            yy += y
        }
    }

    fun checkDiagonal(cell: Cell, x: Int, y: Int) {
        var xx = x
        var yy = y
        for (i in 0 until 8) {
            val newCell = Cell(cell.x + xx, cell.y + yy, CellColor.BROWN)
            if (!newCell.isInside()) break
            if (cells[newCell.x][newCell.y].chip != null) break
            cells[newCell.x][newCell.y].color = CellColor.RED
            xx += x
            yy += y
        }
    }

    fun getNumberBlack(): Int = bcn

    fun getNumberWhite(): Int = wcn

}