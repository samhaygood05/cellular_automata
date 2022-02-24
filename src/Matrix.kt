import Constants.CGoL_KERNEL
import Constants.CGoL_FUNCTION
import kotlin.math.pow

typealias Matrix = Grid<Double>
typealias BoolMatrix = Grid<Boolean>

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
    val temp = Matrix(rows, columns) {0.0}
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
    return if (rows == 1) grid[0][0]
    else if (rows == 2) grid[0][0] * grid[1][1] - grid[1][0] * grid[0][1]
    else {
        var temp = 0.0
        for (i in grid.indices) {
            temp += grid[i][0] * cofactor(i,0)
        }
        temp
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

fun Matrix.kernel(kernel: Matrix, overflow: (Matrix, Int, Int) -> Double, operator: (Double, Double) -> Double): Matrix {
    val center = kernel.getCenter()
    val row = mutableListOf<Array<Double>>()
    for (i in this.grid.indices) {
        val column = mutableListOf<Double>()
        for (j in this.grid[i].indices) {
            var ij = 0.0
            for (k in kernel.grid.indices) {
                val kCenter = k - center[0] + 1
                for (l in kernel.grid[k].indices) {
                    val lCenter = l - center[1] + 1
                    ij += kernel.grid[k][l] * if (i + kCenter < 0 || i + kCenter >= grid.size || j + lCenter < 0 || j + lCenter >= grid[i].size) overflow(this, i + kCenter, j + lCenter)
                    else grid[i + kCenter][j + lCenter]
                }
            }
            column.add(operator(ij, grid[i][j]))
        }
        row.add(column.toTypedArray())
    }
    return Matrix(row.toTypedArray())
}

fun Matrix.CGoL(overflow: (Matrix, Int, Int) -> Double) = kernel(CGoL_KERNEL, overflow, CGoL_FUNCTION)


