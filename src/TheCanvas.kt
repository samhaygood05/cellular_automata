import Constants.ANIMATE
import Constants.colorGrid
import Constants.grid
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics


class TheCanvas(): Canvas() {

    override fun paint(g: Graphics) {
        if (ANIMATE) computeAndDraw(g, grid, 3)
        else draw(g, colorGrid.grid as Array<Array<Any?>>, createMatrix(53, 98) {_:Int, _:Int -> 0.0}.grid as Array<Array<Any?>>, 12)
    }
}

