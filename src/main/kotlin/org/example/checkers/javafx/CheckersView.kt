package org.example.checkers.javafx

import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import org.example.checkers.controller.BasedBoardListener
import org.example.checkers.controller.BasedChipListener
import org.example.checkers.core.*
import tornadofx.*

class CheckersView: View(), BoardListener {
    private val board = Board()

    private val chipListener = BasedChipListener(board)
    private val boardListener = BasedBoardListener(board)

    override val root = BorderPane()

    init {
        title = "Shashki"
        board.registerListener(this)
        updateBoard()
    }

    override fun boardClicked(cell: Cell) {
        TODO("Not yet implemented")
    }

    override fun update() {
        updateBoard()
    }

    private fun updateBoard() {
        val buttons = mutableMapOf<Cell, Button>()
        with(root) {
            center {
                style = "-fx-background-color: brown"
                gridpane {
                    val dimension = Dimension(72.0, Dimension.LinearUnits.px)
                    for (row in 0 until 8) {
                        row {
                            for (column in 0 until 8) {
                                val cell = board.cells[row][column]
                                val button = button {
                                    style {
                                        backgroundColor += board.cells[row][column].color.toJavaFxColor()
                                        minWidth = dimension
                                        minHeight = dimension
                                    }
                                }
                                button.action {
                                    boardListener.boardClicked(cell)
                                }
                                buttons[cell] = button
                                if (board.cells[row][column].chip != null) {
                                    buttons[cell]?.apply {
                                        button {
                                            style = when (board.cells[row][column].color) {
                                                CellColor.BROWN -> "-fx-background-color: brown"
                                                CellColor.RED -> "-fx-background-color: red"
                                                CellColor.PINK -> "-fx-background-color: pink"
                                                else -> "-fx-background-color: yellow"
                                            }
                                            graphic = circle(radius = 20.0) {
                                                fill = board.cells[row][column].chip?.color?.toJavaFx()
                                            }
                                        }.action {
                                            chipListener.chipClicked(cell)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun CellColor.toJavaFxColor():Color{
    return Color(this.r/255.0,this.g/255.0,this.b/255.0,0.5)
}
fun ChipColor.toJavaFx():Color{
    return Color(this.r/255.0,this.g/255.0,this.b/255.0,0.5)
}