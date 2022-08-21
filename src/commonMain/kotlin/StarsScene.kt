import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.vector.*

class StarsScene(val gs: GalaxyState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val cellSize = views.virtualWidth / 10.0
        val cellHeight = views.virtualHeight / 10.0
        var x = 0.00
        var y = 0.00
        var nI = 0
        for (i in 0..3) {
            x = 0.00
            for (j in 0..9) {
                var rect = roundRect(cellSize, cellHeight, 5.0, 5.0, Colors.BLACK, Colors.WHITE, 5.00)
                {
                    position(x, y)
                }
                text(gs.stars[nI]!!.name, 10.00)
                {
                    centerXOn(rect)
                    centerYOn(rect)
                }
                x += cellSize
                nI++
            }
            y += cellHeight
        }
    }
}
