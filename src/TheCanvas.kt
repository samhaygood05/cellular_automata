import Constants.grid
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics


class TheCanvas(): Canvas() {

    override fun paint(g: Graphics) {
        computeAndDraw(g, grid, 25)
    }
}

