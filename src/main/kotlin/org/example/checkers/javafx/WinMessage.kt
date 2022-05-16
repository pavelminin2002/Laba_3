package org.example.checkers.javafx

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class WinMessage: Dialog<ButtonType>() {

    fun start(who: String) {
        title = "Shashki"
        with(dialogPane) {
            headerText = "Game over. $who win!"
            buttonTypes.add(ButtonType("Restart Game", ButtonBar.ButtonData.OK_DONE))
        }
    }
}