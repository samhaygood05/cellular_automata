import Constants.BLUR_CIRCLE_KERNEL
import Constants.BLUR_FUNCTION
import Constants.BLUR_SQUARE_KERNEL
import Constants.NEIGHBORS3x3_KERNEL
import Constants.CGoL_FUNCTION
import Grid.Companion.getCenter
import java.awt.Color
import kotlin.math.pow

typealias Matrix = Grid<Double>
typealias BoolMatrix = Grid<Boolean>
typealias ColorMatrix = Grid<Color>

fun BoolMatrix.toDouble(): Matrix {
    val newGrid = mutableListOf<Array<Double>>()
    for (i in grid) {
        val column = mutableListOf<Double>()
        for (j in i) {
            if (j) column.add(1.0)
            else column.add(0.0)
        }
        newGrid.add(column.toTypedArray())
    }
    return Matrix(newGrid.toTypedArray())
}
fun Matrix.toColor(func: (Double) -> Color = {Color(it.toFloat(), it.toFloat(), it.toFloat())}): ColorMatrix {
    val row = mutableListOf<Array<Color>>()
    for (i in this.grid.indices) {
        val column = mutableListOf<Color>()
        for (j in this.grid[i].indices) {
            column.add(func(grid[i][j]))
        }
        row.add(column.toTypedArray())
    }
    return ColorMatrix(row.toTypedArray())
}
fun colorMatrix(red: Matrix, green: Matrix, blue: Matrix): ColorMatrix {
    require(red.areSameSize(green) && green.areSameSize(blue))
    val row = mutableListOf<Array<Color>>()
    for (i in red.grid.indices) {
        val column = mutableListOf<Color>()
        for (j in red.grid[i].indices) {
            column.add(Color(red.grid[i][j].toFloat(), green.grid[i][j].toFloat(), blue.grid[i][j].toFloat()))
        }
        row.add(column.toTypedArray())
    }
    return ColorMatrix(row.toTypedArray())
}
val ColorMatrix.red get(): Matrix {
    val newGrid = mutableListOf<Array<Double>>()
    for (i in grid) {
        val column = mutableListOf<Double>()
        for (j in i) {
            column.add(j.red/255.0)
        }
        newGrid.add(column.toTypedArray())
    }
    return Matrix(newGrid.toTypedArray())
}
val ColorMatrix.green get(): Matrix {
    val newGrid = mutableListOf<Array<Double>>()
    for (i in grid) {
        val column = mutableListOf<Double>()
        for (j in i) {
            column.add(j.green/255.0)
        }
        newGrid.add(column.toTypedArray())
    }
    return Matrix(newGrid.toTypedArray())
}
val ColorMatrix.blue get(): Matrix {
    val newGrid = mutableListOf<Array<Double>>()
    for (i in grid) {
        val column = mutableListOf<Double>()
        for (j in i) {
            column.add(j.blue/255.0)
        }
        newGrid.add(column.toTypedArray())
    }
    return Matrix(newGrid.toTypedArray())
}
val ColorMatrix.alpha get(): Matrix {
    val newGrid = mutableListOf<Array<Double>>()
    for (i in grid) {
        val column = mutableListOf<Double>()
        for (j in i) {
            column.add(j.alpha/255.0)
        }
        newGrid.add(column.toTypedArray())
    }
    return Matrix(newGrid.toTypedArray())
}

/** Multiplies this value by the other value. */
operator fun Double.times(n: Matrix) = n * this
/** Multiplies this value by the other value. */
operator fun Int.times(n: Matrix) = n * this

/** Divides this value by the other value. */
operator fun Double.div(n: Matrix) = this * n.inv()
/** Divides this value by the other value. */
operator fun Int.div(n: Matrix) = this * n.inv()

/**
 * Returns the submatrix with row [i] and column [j] removed
 */
fun Matrix.subMatrix(i: Int, j: Int): Matrix {
    val subMatrix = Array(rows - 1) { Array(columns - 1) {0.0} }
    var k = 0
    var l = 0
    while (k < rows) {
        if (k != i) {
            var m = 0
            var n = 0
            while (m < columns) {
                if (m != j) {
                    subMatrix[l][n++] = grid[k][m]
                }
                m++
            }
            l++
        }
        k++
    }
    return Matrix(subMatrix)
}

/** Adds the other value to this value. */
operator fun Matrix.plus(that: Matrix): Matrix {
    require(areSameSize(that)) { "A ${rows}x${columns} cannot be added with a ${that.rows}x${that.columns} matrix" }
    val temp = Matrix(rows, columns) { 0.0 }
    for (i in grid.indices) {
        for (j in grid[0].indices) {
            temp.grid[i][j] = grid[i][j] + that.grid[i][j]
        }
    }
    return temp
}

/** Subtracts the other value from this value. */
operator fun Matrix.minus(that: Matrix): Matrix = this + -that

/**
 * Returns the dot product of row [i] and column [j] between two matrices
 *
 * Note:
 * - the row length of the first matrix must be equal to the column length of the second matrix
 */
fun Matrix.dot(that: Matrix, i: Int, j: Int): Double {
    require(columns == that.rows) { "A ${rows}x${columns} cannot be multiplied by a ${that.rows}x${that.columns} matrix" }
    var temp = 0.0
    for (k in 0 until columns) {
        temp += grid[i][k] * that.grid[k][j]
    }
    return temp
}

/** Multiplies this value by the other value. */
operator fun Matrix.times(n: Double): Matrix {
    val temp = this
    for (i in grid.indices) {
        for (j in 0 until columns) {
            temp.grid[i][j] *= n
        }
    }
    return temp
}
/** Multiplies this value by the other value. */
operator fun Matrix.times(n: Int): Matrix {
    val temp = this
    for (i in grid.indices) {
        for (j in 0 until columns) {
            temp.grid[i][j] = n * temp.grid[i][j]
        }
    }
    return temp
}
/** Multiplies this value by the other value. */
operator fun Matrix.times(that: Matrix): Matrix {
    val temp = Matrix(rows, that.columns) {0.0}
    for (i in grid.indices) {
        for (j in that.grid[0].indices) {
            temp.grid[i][j] = dot(that, i, j)
        }
    }
    return temp
}

/** Divides this value by the other value. */
operator fun Matrix.div(that: Double): Matrix = this * (1/that)
/** Divides this value by the other value. */
operator fun Matrix.div(that: Int): Matrix = this * (1.0/that)
/** Divides this value by the other value. */
operator fun Matrix.div(that: Matrix): Matrix = this * (1/that)

fun Matrix.transpose(): Matrix {
    val temp = Matrix(columns, rows) {0.0}
    for (i in grid.indices) {
        for (j in grid[0].indices) {
            temp.grid[j][i] = grid[i][j]
        }
    }
    return temp
}
fun Matrix.trace(): Double {
    var temp = 0.0
    require(isSquare) { "Matrix is not a square" }
    for (i in grid.indices) {
        temp += grid[i][i]
    }
    return temp
}

/** Returns this value. */
operator fun Matrix.unaryPlus(): Matrix = this
/** Returns the negative of this value. */
operator fun Matrix.unaryMinus(): Matrix = -1 * this

val Matrix.isInvertible: Boolean get() = det() != 0.0

fun Matrix.cofactor(i: Int,j: Int): Double {
    return (-1.0).pow(i + j) * subMatrix(i, j).det()
}
fun Matrix.det(): Double {
    require(isSquare) { "Matrix is not a square" }
    return when (rows) {
        1 -> grid[0][0]
        2 -> grid[0][0] * grid[1][1] - grid[1][0] * grid[0][1]
        else -> {
            var temp = 0.0
            for (i in grid.indices) {
                temp += grid[i][0] * cofactor(i,0)
            }
            temp
        }
    }
}
fun Matrix.inv(): Matrix {
    require(isInvertible) { "Matrix is non-invertible" }
    val temp = Matrix(rows, columns) {0.0}
    for (i in grid.indices) {
        for (j in grid[0].indices) {
            temp.grid[i][j] = 1/det() * cofactor(i, j)
        }
    }
    return temp
}

fun Matrix.sum(): Double {
    var result = 0.0
    for (row in this.grid) {
        for (index in row) {
            result += index
        }
    }
    return result
}

fun Matrix.modify(operator: (Double) -> Double): Matrix {
    val row = mutableListOf<Array<Double>>()
    for (i in grid.indices) {
        val column = mutableListOf<Double>()
        for (j in grid[i].indices) {
            column.add(operator(grid[i][j]))
        }
        row.add(column.toTypedArray())
    }
    return Grid(row.toTypedArray())
}

fun Matrix.kernel(kernel: Matrix, overflow: (Matrix, Int, Int) -> Double, operator: (Double, Double) -> Double): Matrix {
    val center = getCenter(kernel.rows, kernel.columns)
    val row = mutableListOf<Array<Double>>()
    for (i in this.grid.indices) {
        val column = mutableListOf<Double>()
        for (j in this.grid[i].indices) {
            var kernelVal = 0.0
            for (k in kernel.grid.indices) {
                val kCenter = k - center[0] + 1
                for (l in kernel.grid[k].indices) {
                    val lCenter = l - center[1] + 1
                    kernelVal += kernel.grid[k][l] * if (i + kCenter < 0 || i + kCenter >= grid.size || j + lCenter < 0 || j + lCenter >= grid[i].size) overflow(this, i + kCenter, j + lCenter)
                    else grid[i + kCenter][j + lCenter]
                }
            }
            column.add(operator(kernelVal, grid[i][j]))
        }
        row.add(column.toTypedArray())
    }
    return Matrix(row.toTypedArray())
}
fun ColorMatrix.kernel(Kernels: Array<Array<Matrix>>, overflow: (Matrix, Int, Int) -> Double, operators: Array<(Array<Double>, Double) -> Float>): ColorMatrix {
    val center = arrayListOf(getCenter(Kernels[0][0].rows, Kernels[0][0].columns), getCenter(Kernels[1][0].rows, Kernels[1][0].columns), getCenter(Kernels[2][0].rows, Kernels[2][0].columns))
    val row = mutableListOf<Array<Color>>()
    for (i in this.grid.indices) {
        val column = mutableListOf<Color>()
        for (j in this.grid[i].indices) {
            val redKernelVal = mutableListOf<Double>()
            val greenKernelVal = mutableListOf<Double>()
            val blueKernelVal = mutableListOf<Double>()
            for (kernel in Kernels.indices) {
                var kernelValRed = 0.0
                var kernelValGreen = 0.0
                var kernelValBlue = 0.0
                for (k in Kernels[kernel][0].grid.indices) {
                    val kCenter = k - center[kernel][0] + 1
                    for (l in Kernels[kernel][0].grid[k].indices) {
                        val lCenter = l - center[kernel][1] + 1
                        kernelValRed += Kernels[kernel][0].grid[k][l] * if (i + kCenter < 0 || i + kCenter >= grid.size || j + lCenter < 0 || j + lCenter >= grid[i].size) overflow(red, i + kCenter, j + lCenter)
                        else red.grid[i + kCenter][j + lCenter]
                        kernelValGreen += Kernels[kernel][1].grid[k][l] * if (i + kCenter < 0 || i + kCenter >= grid.size || j + lCenter < 0 || j + lCenter >= grid[i].size) overflow(green, i + kCenter, j + lCenter)
                        else green.grid[i + kCenter][j + lCenter]
                        kernelValBlue += Kernels[kernel][2].grid[k][l] * if (i + kCenter < 0 || i + kCenter >= grid.size || j + lCenter < 0 || j + lCenter >= grid[i].size) overflow(blue, i + kCenter, j + lCenter)
                        else blue.grid[i + kCenter][j + lCenter]
                    }
                }
                redKernelVal.add(kernelValRed)
                greenKernelVal.add(kernelValGreen)
                blueKernelVal.add(kernelValBlue)
            }
            column.add(Color(operators[0](redKernelVal.toTypedArray(), red.grid[i][j]), operators[1](greenKernelVal.toTypedArray(), green.grid[i][j]), operators[2](blueKernelVal.toTypedArray(), blue.grid[i][j])))
        }
        row.add(column.toTypedArray())
    }
    return ColorMatrix(row.toTypedArray())
}

fun Matrix.CGoL(overflow: (Matrix, Int, Int) -> Double, a: Int = 2, b: Int = 3, c: Int = 3, d: Int = 3, e: Double = 0.7) = kernel(NEIGHBORS3x3_KERNEL, overflow, CGoL_FUNCTION(a, b, c, d, e))

fun Matrix.BLUR_SQUARE(i: Int, overflow: (Matrix, Int, Int) -> Double) = kernel(BLUR_SQUARE_KERNEL(i), overflow, BLUR_FUNCTION(BLUR_SQUARE_KERNEL(i)))
fun Matrix.BLUR_CIRCLE(i: Int, overflow: (Matrix, Int, Int) -> Double) = kernel(BLUR_CIRCLE_KERNEL(i), overflow, BLUR_FUNCTION(BLUR_SQUARE_KERNEL(i)))

fun createMatrix(i: Int, j: Int, populate: (Int, Int) -> Double): Matrix {
    val row = mutableListOf<Array<Double>>()
    for (k in 0 until i) {
        val column = mutableListOf<Double>()
        for (l in 0 until j) {
            column.add(populate(k, l))
        }
        row.add(column.toTypedArray())
    }
    return Grid(row.toTypedArray())
}
fun insertSubMatrix(i: Int, j: Int, k: Int, l: Int, matrix: Matrix, populate: (Int, Int) -> Double): Matrix {
    return createMatrix(i, j) { y: Int, x: Int ->
        if (y in k until (k + matrix.rows) && x in l until (l + matrix.columns)) matrix.grid[y - k][x - l] else populate(y, x)
    }
}


