import Constants.LOOP
import Constants.SHOW_TRAILS
import Constants.TRAIL_LENGTH
import Constants.value
import java.awt.Color
import java.awt.Graphics

fun main() {

    val fr = Draw()
}

fun color(that: Any?, trails: Boolean): Color {
    return when (that) {
        is Color -> that
        is Double -> Color(that.toFloat(), that.toFloat(), that.toFloat(), if (that <= 0.0 && trails) 1 / TRAIL_LENGTH else 1.0f)
        else -> Color.BLACK
    }
}

fun draw(g: Graphics, that: Array<Array<Any?>>, blockSize: Int, trails: Boolean = true) {
    for (y in that.indices) {
        for (x in that[y].indices) {
            g.color = color(that[y][x], trails)
            g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize)
        }
    }
}
@Suppress("UNCHECKED_CAST") fun draw(g: Graphics, that: Array<Array<Double>>, blockSize: Int, trails: Boolean) {
    draw(g, that as Array<Array<Any?>>, blockSize, trails)
}

fun computeAndDraw(g: Graphics, that: Matrix, blockSize: Int) {
    var frame = that
    draw(g, frame.grid, blockSize, false)
    while (true) {
        frame = frame.CGoL(LOOP)

        Thread.sleep(100)

        draw(g, frame.grid, blockSize, SHOW_TRAILS)

    }
}