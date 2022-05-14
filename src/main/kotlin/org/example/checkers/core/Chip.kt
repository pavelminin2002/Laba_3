package org.example.checkers.core

open class Chip(var color: ChipColor)

class Queen(color: ChipColor) : Chip(color)

enum class ChipColor{
    BLACK,
    WHITE;

    fun opposite(): ChipColor {
        return if (this == BLACK) WHITE else BLACK
    }
}