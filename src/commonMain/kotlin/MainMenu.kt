import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*

class MainMenu(val gs: GalaxyState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        text("Crown of Stars", 60.00, Colors.GOLD, font)
        {
            position(width/2, 100.00)
            centerXOnStage()
        }

        text("New Game",50.00, Colors.WHITE, font)
        {
            position(width/2, 200.00)
            centerXOnStage()
            onClick { gs.rollGalaxy(); sceneContainer.changeTo<StarsScene>() }
        }

        text("Continue Game",50.00, Colors.WHITE, font)
        {
            position(width/2, 300.00)
            centerXOnStage()
            onClick { gs.load(); sceneContainer.changeTo<PlanetsScene>() }
        }

        text("Quit Game",50.00, Colors.WHITE, font)
        {
            position(width/2, 400.00)
            centerXOnStage()
            onClick { views.gameWindow.close() }
        }


    }
}
