package org.example.checkers.core


var bcn = 12
var wcn = 12

class Board {
    private var bcng = 12
    private var wcng = 12
    private val width = 8
    private val height = 8
    private var turn = ChipColor.WHITE
    var cells = mutableListOf<List<Cell>>()
    private var listener: BoardListener? = null
    val readyEat = mutableListOf<Cell>()
    fun registerListener(listener: BoardListener) {
        this.listener = listener
    }

    init {
        for (row in 0 until width) {
            val list = mutableListOf<Cell>()
            for (column in 0 until height) {
                val c = if (row % 2 == column % 2) CellColor.IVORY else CellColor.BROWN
                val cell = Cell(row, column, c)
                if (bcn != 0 && c == CellColor.BROWN) {
                    cell.chip = Chip(ChipColor.BLACK)
                    bcn--
                }
                if (wcn != 0 && c == CellColor.BROWN && row >= 5) {
                    cell.chip = Chip(ChipColor.WHITE)
                    wcn--
                }
                list.add(cell)
            }
            cells.add(list)
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

    fun makeTurn(cell: Cell) {
        var oneWay: Boolean
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
        listener!!.update()
    }

    fun turnMade(cell: Cell) {
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
                if (turn == ChipColor.WHITE) bcng--
                else wcng--
                makeTurn(cell)
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
        listener!!.update()
    }

    fun checkEat(goalN: Cell, x: Int, y: Int, oneWay: Boolean): Boolean {
        if (goalN.isInside()) {
            if (cells[goalN.x][goalN.y].chip != null && cells[goalN.x][goalN.y].chip?.color != turn) {
                val goal = Cell(goalN.x + x, goalN.y + y, CellColor.BROWN)
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

    fun checkEatAll() {
        readyEat.clear()
        for (i in 0 until width) {
            for (j in 0 until height) {
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
        result = checkEat(Cell(cell.x + 1, cell.y + 1, CellColor.BROWN), 1, 1, result)
        result = checkEat(Cell(cell.x + 1, cell.y - 1, CellColor.BROWN), 1, -1, result)
        result = checkEat(Cell(cell.x - 1, cell.y + 1, CellColor.BROWN), -1, 1, result)
        result = checkEat(Cell(cell.x - 1, cell.y - 1, CellColor.BROWN), -1, -1, result)
        return result
    }

    fun makeTurnQueen(cell: Cell) {
        clearBoard()
        if (turn == cell.chip?.color) {
            checkEatAll()
            cells[cell.x][cell.y].color = CellColor.YELLOW
            if (readyEat.isNotEmpty() && cell in readyEat) {
                checkDiagonalEat(cell, 1, 1)
                checkDiagonalEat(cell, 1, -1)
                checkDiagonalEat(cell, -1, 1)
                checkDiagonalEat(cell, -1, -1)
            } else if (readyEat.isEmpty()) {
                checkDiagonal(cell, 1, 1)
                checkDiagonal(cell, 1, -1)
                checkDiagonal(cell, -1, 1)
                checkDiagonal(cell, -1, -1)
            }
        }
        listener!!.update()
    }

    fun checkAroundQueen(cell: Cell): Boolean {
        var result = false
        var x = 0
        var y = 0
        for (i in 0 until width) {
            x++
            y++
            result = checkEat(Cell(cell.x + x, cell.y + y, CellColor.BROWN), 1, 1, result)
            result = checkEat(Cell(cell.x + x, cell.y - y, CellColor.BROWN), 1, -1, result)
            result = checkEat(Cell(cell.x - x, cell.y + y, CellColor.BROWN), -1, 1, result)
            result = checkEat(Cell(cell.x - x, cell.y - y, CellColor.BROWN), -1, -1, result)
            if (result) return true
        }
        return false
    }

    fun checkDiagonalEat(cell: Cell, x: Int, y: Int) {
        var CellX = 0
        var CellY = 0
        var xx = x
        var yy = y
        var maybeEat = false
        var chipAfter = false
        for (i in 0 until width) {
            val newCell = Cell(cell.x + xx, cell.y + yy, CellColor.BROWN)
            if (!newCell.isInside()) break
            if (cells[newCell.x][newCell.y].chip != null && cells[newCell.x][newCell.y].chip?.color == turn) break
            if (maybeEat) {
                if (cells[newCell.x][newCell.y].chip != null) {
                    if (chipAfter) {
                        cells[CellX][CellY].color = CellColor.BROWN
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
                    CellX = newCell.x
                    CellY = newCell.y
                } else break
            }
            xx += x
            yy += y
        }
    }

    fun checkDiagonal(cell: Cell, x: Int, y: Int) {
        var xx = x
        var yy = y
        for (i in 0 until width) {
            val newCell = Cell(cell.x + xx, cell.y + yy, CellColor.BROWN)
            if (!newCell.isInside()) break
            if (cells[newCell.x][newCell.y].chip != null) break
            cells[newCell.x][newCell.y].color = CellColor.RED
            xx += x
            yy += y
        }
    }
}