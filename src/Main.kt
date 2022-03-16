import Constants.LOOP
import Constants.ONE_DIMENSIONAL_KERNEL
import Constants.R90_FUNCTION
import Constants.SHOW_TRAILS
import Constants.TRAIL_LENGTH
import java.awt.Color
import java.awt.Graphics

fun main() {

    val fr = Draw()
}

fun color(that: Any?, trails: Boolean): Color {
    return when (that) {
        is Color -> that
        is Double -> when (that) {
            in 0.0..1.0 -> Color(
                that.toFloat(),
                that.toFloat(),
                that.toFloat(),
                if (that <= 0.0 && trails) 1 / TRAIL_LENGTH else 1.0f
            )
            in -1.0..0.0 -> Color(-that.toFloat(), 0.0f, 0.0f)
            else -> Color.getHSBColor(that.toFloat(), 1.0f, 1.0f)
        }
        else -> Color.BLACK
    }
}

fun draw(g: Graphics, that: Array<Array<Any?>>, prev: Array<Array<Any?>>, blockSize: Int, trails: Boolean = true) {
    for (y in that.indices) {
        for (x in that[y].indices) {
            if (color(that[y][x], trails).alpha != 0 || color(that[y][x], trails) != color(prev[y][x], trails)){
                g.color = color(that[y][x], trails)
                g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize)
            }
        }
    }
}
@Suppress("UNCHECKED_CAST") fun draw(g: Graphics, that: Array<Array<Double>>, prev: Array<Array<Double>>, blockSize: Int, trails: Boolean) {
    draw(g, that as Array<Array<Any?>>, prev as Array<Array<Any?>>, blockSize, trails)
}

fun computeAndDraw(g: Graphics, that: Matrix, blockSize: Int) {
    var frame = that
    val applyKernel: (Matrix, Int) -> Matrix = {matrix: Matrix, frameC: Int ->
        matrix.BLUR_CIRCLE(10, LOOP) * 4
    }
    draw(g, frame.grid, frame.grid, blockSize, false)
    var frameCount: Int = 0
    while (true) {
        val frame2 = applyKernel(frame, frameCount)

        Thread.sleep(0)

        draw(g, frame2.grid, frame.grid, blockSize, SHOW_TRAILS)
        frame = frame2
        frameCount++

    }
}
//fun computeAndDraw1(g: Graphics, that: ColorMatrix, blockSize: Int) {
//    var frame = that
//    draw(g, frame.grid as Array<Array<Any?>>, frame.grid as Array<Array<Any?>>, blockSize, false)
//    while (true) {
//        val frame2 = frame.kernel(CGoL3D_KERNEL,
//        value(1.0), CGoL3D_FUNCTION)
//
//        Thread.sleep(50)
//
//        draw(g, frame2.grid as Array<Array<Any?>>, frame.grid as Array<Array<Any?>>, blockSize, SHOW_TRAILS)
//        frame = frame2
//
//    }
//}