import java.awt.Color
import java.lang.StringBuilder
import kotlin.math.ceil
import kotlin.math.max

class Grid<T> {
    var grid: Array<Array<T>>
    var rows: Int
    var columns: Int

    constructor(grid: Array<Array<T>>) {
        val size: Int = grid[0].size
        for (i in 1 until grid.size) {
            require(grid[i].size == size) { "Grid has missing elements" }
        }
        this.grid = grid
        rows = grid.size
        columns = grid[0].size
    }
    @Suppress("UNCHECKED_CAST") constructor(i: Int, j: Int, populate: (Int) -> T) {
        rows = i
        columns = j
        val grid2: Array<Array<Any?>> = Array(i) { Array(j, populate) }
        grid = grid2 as Array<Array<T>>
    }

    fun areSameSize(that: Grid<T>): Boolean = rows == that.rows && columns == that.columns
    val isSquare: Boolean get() = rows == columns

    @Suppress("UNCHECKED_CAST") infix fun concatenate(that: Grid<Any?>): Grid<Any?> {
        require(rows == that.rows) { "Grids are not Combinable" }
        val row = mutableListOf<Array<Any?>>()
        for (i in 0 until rows) {
            val column =  mutableListOf<Any?>()
            column.addAll(grid[i])
            column.addAll(that.grid[i])
            row.add(column.toTypedArray())
        }
        return Grid(row.toTypedArray())
    }

    private fun longestTerm(j: Int): Int {
        var temp = 0
        for (i in grid.indices) {
            temp = max(temp, grid[i][j].toString().length)
        }
        return temp
    }
    override fun toString(): String {
        val temp = StringBuilder()
        val maxLengthPerColumn: MutableList<Int> = mutableListOf()
        for (j in grid[0].indices) {
            maxLengthPerColumn.add(longestTerm(j))
        }
        for (i in grid.indices) {
            if (i == 0) temp.append("┌ ") else if (i == rows - 1) temp.append("└ ") else temp.append("│ ")
            for (j in 0 until grid[i].size) {
                if (grid[i][j] == 0.0) temp.append("0.0")
                else temp.append(grid[i][j])
                for (k in 1..maxLengthPerColumn[j] - grid[i][j].toString().length) temp.append(" ")
                if (j != columns - 1) temp.append("   ")
            }
            if (i == 0) temp.append(" ┐\n") else if (i == rows - 1) temp.append(" ┘") else temp.append(" │\n")
        }
        return temp.toString()
    }

    companion object {
        @JvmStatic fun divider(i: Int, color: Color) = Grid(i, 1) { color }
        @JvmStatic fun <T> createGrid(i: Int, j: Int, populate: (Int, Int) -> T): Grid<T> {
            val row = mutableListOf<Array<Any?>>()
            for (k in 0 until i) {
                val column = mutableListOf<Any?>()
                for (l in 0 until j) {
                    column.add(populate(k, l))
                }
                row.add(column.toTypedArray())
            }
            return Grid(row.toTypedArray()) as Grid<T>
        }
        @JvmStatic fun getCenter(rows: Int, columns: Int) = arrayListOf(ceil(rows/2.0).toInt(), ceil(columns/2.0).toInt())
    }
}

