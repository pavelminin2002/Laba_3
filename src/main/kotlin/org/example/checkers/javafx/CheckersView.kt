package org.example.checkers.javafx

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.example.checkers.controller.BasedBoardListener
import org.example.checkers.controller.BasedChipListener
import org.example.checkers.controller.BoardListener
import org.example.checkers.core.*
import tornadofx.*

class CheckersView : View(), BoardListener {
    private var board = Board()
    private var inProcess: Boolean = true
    private var chipListener = BasedChipListener(board)
    private var boardListener = BasedBoardListener(board)
    private lateinit var statusLabel: Label

    override val root = BorderPane()

    init {
        title = "Shashki"
        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restart()
                            }
                            separator()
                            item("Exit").action {
                                this@CheckersView.close()
                            }
                        }
                    }
                    spacer(Priority.ALWAYS)
                }
            }
            bottom {
                statusLabel = label("")
            }
        }
        board.registerListener(this)
        updateBoard()
    }

    private fun restart() {
        board = Board()
        boardListener = BasedBoardListener(board)
        board.registerListener(this)
        chipListener = BasedChipListener(board)
        inProcess = true
        updateBoard()
    }

    override fun boardClicked(cell: Cell) {
        TODO("Not yet implemented")
    }

    override fun update() {
        updateBoard()
    }

    private fun updateBoard() {
        statusLabel.text = when {
            board.getNumberWhite() == 0 -> {
                inProcess = false
                "Blacks win! Press 'Restart' to continue"
            }
            board.getNumberBlack() == 0 -> {
                inProcess = false
                "Whites win! Press 'Restart' to continue"
            }
            board.turn == ChipColor.WHITE -> "Game in process: whites turn"
            else -> "Game in process: blacks turn"
        }
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
                                        backgroundColor += when (board.cells[row][column].color) {
                                            CellColor.PINK->Color.PINK
                                            CellColor.YELLOW->Color.YELLOW
                                            CellColor.RED -> Color.RED
                                            CellColor.IVORY -> Color.IVORY
                                            else -> Color.BROWN
                                        }

                                        minWidth = dimension
                                        minHeight = dimension
                                    }
                                }
                                button.action {
                                    if (inProcess) boardListener.boardClicked(cell)
                                }
                                buttons[cell] = button
                                if (board.cells[row][column].chip != null) {
                                    buttons[cell]?.apply {
                                        button {
                                            style = when (board.cells[row][column].color) {
                                                CellColor.RED -> "-fx-background-color: red"
                                                else -> "-fx-background-color: brown"
                                            }
                                            if (board.cells[row][column].chip !is Queen) {
                                                graphic = if (board.cells[row][column].chip?.color == ChipColor.BLACK)
                                                    ImageView("/b.png").apply {
                                                        fitWidth = 40.0
                                                        fitHeight = 40.0
                                                    }
                                                else ImageView("/w.png").apply {
                                                    fitWidth = 40.0
                                                    fitHeight = 40.0
                                                }
                                            } else if (board.cells[row][column].chip?.color == ChipColor.WHITE) {
                                                graphic = ImageView("/dw.png").apply {
                                                    fitWidth = 45.0
                                                    fitHeight = 45.0
                                                }
                                            } else {
                                                graphic = ImageView("/db.png").apply {
                                                    fitWidth = 45.0
                                                    fitHeight = 45.0
                                                }
                                            }
                                        }.action {
                                            if (inProcess) chipListener.chipClicked(cell)
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

