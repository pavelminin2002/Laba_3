package org.example.checkers.javafx

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App

class AppCheckers : App(CheckersView::class) {
    override fun start(stage: Stage) {
        stage.show()
        super.start(stage)
    }
}


fun main(args: Array<String>) {
    Application.launch(AppCheckers::class.java, *args)
}