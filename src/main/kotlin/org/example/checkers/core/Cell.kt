package org.example.checkers.core

import kotlin.math.sqrt

enum class CellColor {
    BROWN,
    IVORY,
    YELLOW,
    RED,
    PINK;
}

class Cell(var x: Int, var y: Int, var color: CellColor) {

    var chip: Chip? = null

    fun isInside(): Boolean = ((x in 0..7) && y in (0..7))

    fun isUp(): Boolean = (this.x == 7)
    fun isDown(): Boolean = (this.x == 0)
    fun distance(other: Cell): Int =
        sqrt(sqr((this.x - other.x)).toDouble() + sqr((this.y - other.y)).toDouble()).toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Cell) {
            return other.x == this.x && other.y == this.y && other.color == this.color
        }
        return false
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

}

fun sqr(x: Int): Int = x * x