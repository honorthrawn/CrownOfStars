import com.soywiz.klogger.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.vector.*

class StarsScene(val gs: GalaxyState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val star = resourcesVfs["stars/star1.png"].readBitmap()
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
                    onClick { clickedSector(i,j) } //for some weird reason trying to use nI always results in 40
                }

                uiVerticalStack(cellSize, UI_DEFAULT_PADDING, false) {
                    centerXOn(rect)
                    centerYOn(rect)
                    var starImage = image(star)
                    when(gs.stars[nI]!!.type)
                    {
                        StarType.YELLOW -> starImage.colorMul = Colors.YELLOW
                        StarType.BLUE -> starImage.colorMul = Colors.BLUE
                        StarType.RED -> starImage.colorMul = Colors.RED
                    }
                    text(gs.stars[nI]!!.name, 10.00, Colors.WHITE, font)
                }
                x += cellSize
                nI++
            }
            y += cellHeight
        }
    }

    private suspend fun clickedSector(x: Int, y: Int)
    {
        gs.activePlayerStar = x * 10 + y; sceneContainer.changeTo<PlanetsScene>()
    }
}
