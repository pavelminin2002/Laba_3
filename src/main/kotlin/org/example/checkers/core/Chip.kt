package org.example.checkers.core

open class Chip(var color: ChipColor)

class Queen(color: ChipColor) : Chip(color)

fun Chip.toQueen(): Queen {
    return Queen(this.color)
}

enum class ChipColor(var r: Int, var g: Int, var b: Int) {
    BLACK(0, 0, 0),
    WHITE(255, 255, 255);

    fun opposite(): ChipColor {
        return if (this == BLACK) WHITE else BLACK
    }
}