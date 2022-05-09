package org.example.checkers.core

enum class CellColor(var r:Int,var g:Int,var b:Int) {
    BROWN(160, 82, 45),
    IVORY(255, 222, 173),
    YELLOW(255, 255, 0),
    RED(255, 0, 0),
    PINK(255, 192, 203);
}

class Cell(var x: Int, var y: Int, var color: CellColor) {

    var chip: Chip? = null

    fun isInside(): Boolean = ((x in 0..7) && y in (0..7))

    fun isUp(): Boolean = (this.x == 7)
    fun isDown(): Boolean = (this.x == 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Cell) {
            return other.x == this.x && other.y == this.y
        }
        return false
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

}