package org.example.checkers.javafx

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.example.checkers.controller.BasedBoardListener
import org.example.checkers.controller.BoardListener
import org.example.checkers.core.*
import tornadofx.*
import java.lang.Math.abs

class CheckersView : View(), BoardListener {
    private var boardListener = BasedBoardListener()
    private var statusLabel: Label = Label("Start game")

    override val root = BorderPane()

    init {
        title = "Shashki"
        root.setMinSize(1535.0, 750.0)
        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restart()
                            }
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
        boardListener.board.registerListener(this)
        updateBoard()
    }

    private fun restart() {
        boardListener.board = Board()
        boardListener = BasedBoardListener()
        boardListener.board.registerListener(this)
        boardListener.inProcess = true
        updateBoard()
    }

    override fun boardClicked(cell: Cell) {}

    override fun chipClicked(cell: Cell) {}

    override fun update() {
        updateBoard()
    }

    private fun updateBoard() {
        val buttons = mutableMapOf<Cell, Button>()
        with(root) {
            left {
                 vbox {
                     this.setMinSize(375.0, 750.0)
                     for (i in 0 until 12 - boardListener.board.getNumberBlack()) {
                         button {
                             style = "-fx-background-color: brown"
                             graphic = ImageView("/b.png").apply {
                                 fitWidth = 55.0
                                 fitHeight = 55.0
                             }
                         }
                     }
                 }
            }
            right {
                vbox {
                    this.setMinSize(375.0, 750.0)
                    for (i in 0 until 12 - boardListener.board.getNumberWhite()) {
                        button {
                            style = "-fx-background-color: brown"
                            graphic = ImageView("/w.png").apply {
                                fitWidth = 55.0
                                fitHeight = 55.0
                            }
                        }
                    }
                }
            }
            center {
                style = "-fx-background-color: brown"
                gridpane {
                    val dimension = Dimension(96.0, Dimension.LinearUnits.px)
                    for (row in -4 until 4) {
                        row {
                            for (column in -4 until 4) {
                                var pair = row + 4 to column + 4
                                if (boardListener.board.turn == ChipColor.BLACK) pair = abs(row - 3) to abs(column - 3)
                                val cell = boardListener.board.cells[pair.first][pair.second]
                                val button = button {
                                    style {
                                        backgroundColor += when (boardListener.board.cells[pair.first][pair.second].color) {
                                            CellColor.RED -> Color.RED
                                            CellColor.IVORY -> Color.IVORY
                                            else -> Color.BROWN
                                        }

                                        minWidth = dimension
                                        minHeight = dimension
                                    }
                                }
                                button.action {
                                    if (boardListener.inProcess) boardListener.boardClicked(cell)
                                }
                                buttons[cell] = button
                                if (boardListener.board.cells[pair.first][pair.second].chip != null) {
                                    buttons[cell]?.apply {
                                        button {
                                            style = when (boardListener.board.cells[pair.first][pair.second].color) {
                                                CellColor.RED -> "-fx-background-color: red"
                                                else -> "-fx-background-color: brown"
                                            }
                                            if (boardListener.board.cells[pair.first][pair.second].chip !is Queen) {
                                                graphic = if (boardListener.board.cells[pair.first][pair.second].chip?.color == ChipColor.BLACK)
                                                    ImageView("/b.png").apply {
                                                        fitWidth = 65.0
                                                        fitHeight = 65.0
                                                    }
                                                else ImageView("/w.png").apply {
                                                    fitWidth = 65.0
                                                    fitHeight = 65.0
                                                }
                                            } else if (boardListener.board.cells[pair.first][pair.second].chip?.color == ChipColor.WHITE) {
                                                graphic = ImageView("/dw.png").apply {
                                                    fitWidth = 65.0
                                                    fitHeight = 65.0
                                                }
                                            } else {
                                                graphic = ImageView("/db.png").apply {
                                                    fitWidth = 65.0
                                                    fitHeight = 65.0
                                                }
                                            }
                                        }.action {
                                            if (boardListener.inProcess) boardListener.chipClicked(cell)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        when {
            boardListener.board.getNumberWhite() == 0 -> endgame("Blacks")
            boardListener.board.getNumberBlack() == 0 -> endgame("Whites")
            boardListener.board.turn == ChipColor.WHITE -> statusLabel.text = "Game in process: whites turn"
            else -> statusLabel.text = "Game in process: blacks turn"
        }
    }

    private fun endgame(text: String) {
        boardListener.inProcess = false
        statusLabel.text = "Press 'Restart' to continue"
        val message = WinMessage()
        message.start(text)
        message.showAndWait()
        restart()
    }
}

