import Grid.Companion.getCenter
import java.awt.Color
import kotlin.math.*

object Constants {

    @JvmStatic fun line(iStart: Int, jStart: Int, iEnd: Int, jEnd: Int): (Int, Int) -> Boolean = { i: Int, j: Int ->
        (i == (j * (iEnd - iStart)/(jEnd - jStart).toDouble()).roundToInt() || j == (i * (jEnd - jStart)/(iEnd - iStart).toDouble()).roundToInt()) &&
                (i in iStart..iEnd || i in iEnd..iStart) && (j in jStart..jEnd || j in jEnd..jStart)
    }

    // Edge Behavior
    @JvmField val LOOP: (Matrix, Int, Int) -> Double = {grid: Matrix, i: Int, j: Int -> grid.grid[i.mod(grid.rows)][j.mod(grid.columns)] }
    @JvmStatic fun value(that: Double) = { _: Matrix, _: Int, _: Int -> that }
    @JvmStatic fun value(that: Color) = { _: Matrix, _: Int, _: Int -> that}

    // CGoL
    @JvmField val GLIDER = Matrix(arrayOf(
        arrayOf(0.0, 1.0, 0.0),
        arrayOf(0.0, 0.0, 1.0),
        arrayOf(1.0, 1.0, 1.0)
    ))
    @JvmField val SCARAB = Matrix(arrayOf(
        arrayOf(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0),
    ))

    @JvmField val grid = createMatrix(53*4, 98*4) { i: Int, j: Int -> Math.random() }
    @JvmField val colorGrid = colorMatrix(createMatrix(53, 98) {_:Int, _:Int -> 0.0}, createMatrix(53, 98) {_:Int, _:Int -> 0.0}, createMatrix(53, 98) {_:Int, _:Int -> 0.0})
    @JvmField val kernel = Matrix(arrayOf(
        arrayOf(1.0, 1.0, 1.0),
        arrayOf(1.0, 0.0, 1.0),
        arrayOf(1.0, 1.0, 1.0)
    ))

    @JvmField val NEIGHBORS3x3_KERNEL = Matrix(arrayOf(
        arrayOf(1.0, 1.0, 1.0),
        arrayOf(1.0, 0.0, 1.0),
        arrayOf(1.0, 1.0, 1.0)
    ))
    @JvmStatic fun CGoL_FUNCTION(a: Int = 2, b: Int = 3, c: Int = 3, d: Int = 3, e: Double = 0.7) = { kernelVal: Double, currentVal: Double ->
        when {
            (currentVal > e && kernelVal in a.toDouble()..b.toDouble()) || (currentVal <= e && kernelVal in c.toDouble()..d.toDouble()) -> 1.0
            else -> 0.0
        }
    }

    @JvmField val ONE_DIMENSIONAL_KERNEL = Matrix(arrayOf(
        arrayOf(1.0, 0.5, 0.25),
        arrayOf(0.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 0.0)
    ))
    @JvmField val R30_FUNCTION = { kernelVal: Double, currentVal: Double ->
        when (kernelVal) {
            1.75, 1.25, 1.5 -> 0.0
            1.0, 0.75, 0.5, 0.25 -> 1.0
            else -> currentVal
        }
    }
    @JvmField val R90_FUNCTION = { kernelVal: Double, currentVal: Double ->
        when (kernelVal) {
            0.25, 0.75, 1.0, 1.5 -> 1.0
            0.0 -> currentVal
            else -> 0.0
        }
    }

    @JvmField val WIRE_WORLD_KERNEL = Matrix(arrayOf(
        arrayOf(0.0, 1.0, 0.0),
        arrayOf(4.0, 0.0, 16.0),
        arrayOf(0.0, 64.0, 0.0)
    ))
    @JvmField val WIRE_WORLD_FUNCTION = { kernelVal: Double, currentVal: Double ->
        when (currentVal) {
            0.0 -> 0.0
            in 1.1..2.0 -> 1.0
            in 2.0..3.0 -> 2.0
            in 0.0..1.0 -> {
                val a = kernelVal.mod(4.0)
                val b = (kernelVal.mod(16.0) - kernelVal.mod(4.0))/4
                val c = (kernelVal.mod(64.0) - kernelVal.mod(16.0))/16
                val d = (kernelVal.mod(256.0) - kernelVal.mod(64.0))/64
                when {
                    a == 3.0 || b == 3.0 || c == 3.0 || d == 3.0 -> 3.0
                    else -> 1.0
                }
            }
            else -> 0.0

        }
    }

    @JvmStatic fun BLUR_CIRCLE_KERNEL(i: Int) = createMatrix(i, i) {k: Int, l: Int ->
        val center = getCenter(i, i)
        if (k != center[0] && l != center[1]) (1/hypot((k - center[0]).toDouble(), (l - center[1]).toDouble())) else 1.0
    }
    @JvmStatic fun BLUR_SQUARE_KERNEL(i: Int) = createMatrix(i, i) {_: Int, _: Int -> 1.0}
    @JvmStatic fun BLUR_FUNCTION(kernel: Matrix) = { kernelVal: Double, _: Double -> kernelVal/kernel.sum()}

    @JvmField val CAP = { kernelVal: Double, _: Double -> kernelVal.mod(1.0) }
    @JvmField val CAP_COLOR = { kernelVal: Array<Double>, _:Double -> ((kernelVal[0] + kernelVal[1] + kernelVal[2])/400).mod(1.0).toFloat()}

    // Config
    const val SHOW_TRAILS: Boolean = false
    const val TRAIL_LENGTH: Float = 8.0F
    const val ANIMATE: Boolean = true

}