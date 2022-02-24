import java.awt.Color
import java.awt.Graphics
import java.awt.color.ColorSpace

fun main() {

    println(grid)
    val fr = Draw()
}

fun color(that: Any?): Color {
    return when (that) {
        is Color -> that
        is Double -> Color(that.toFloat(), that.toFloat(), that.toFloat())
        else -> Color.BLACK
    }
}

fun draw(g: Graphics, that: Array<Array<Any?>>, blockSize: Int) {
    for (y in that.indices) {
        for (x in that[y].indices) {
            g.color = color(that[y][x])
            g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize)
        }
    }
}

fun computeAndDraw(g: Graphics, that: Array<Array<Any?>>, blockSize: Int) {
    while (true) {
        draw(g, grid.grid as Array<Array<Any?>>, blockSize)

        Thread.sleep(125)

        grid = grid.kernel(kernel, LOOP) { ij: Double, item: Double ->
            when {
                (item > 0.0 && ij in 2.0..3.0) || (item <= 0.0 && ij == 3.0) -> 1.0
                else -> 0.0
            }
        }
    }
}

val LOOP: (Matrix, Int, Int) -> Double = {grid: Matrix, i: Int, j: Int -> grid.grid[i.mod(grid.rows)][j.mod(grid.columns)] }