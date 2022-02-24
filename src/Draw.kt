
import java.awt.BorderLayout
import javax.swing.JFrame


class Draw() : JFrame() {
    private val canvas = TheCanvas()
    init {
        layout = BorderLayout()
        setSize(1200, 1200 / 16 * 9)
        title = "Vector Space"
        add("Center", canvas)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }

}
