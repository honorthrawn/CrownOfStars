import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ColonyScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val fileName = when( gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.type) {
            PlanetType.TOXIC -> "landscapes/108.bmp"
            PlanetType.OCEAN -> "landscapes/7.bmp"
            PlanetType.TERRAN -> "landscapes/412.bmp"
            PlanetType.DESSERT -> "landscapes/101.bmp"
            PlanetType.VOLCANIC -> "landscapes/403.bmp"
            PlanetType.BARREN -> "landscapes/914.bmp"
            PlanetType.SUPERTERRAN -> "landscapes/413.bmp"
            PlanetType.TROPICAL -> "landscapes/906.bmp"
        }
        val planetImage = image(resourcesVfs[fileName].readBitmap())
            {
                position(0, 0)
                setSizeScaled(width, height)
            }

        val message = gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.name
        uiVerticalStack {
            scaledWidth = sceneWidth.toDouble()
            scaledHeight = sceneHeight.toDouble()
            text("You started a new colony on", 50.00, Colors.CYAN, font )
            text(message, 50.00, Colors.CYAN, font)
            text("CLOSE", 50.00, Colors.GOLD, font)
            {
                onClick {  sceneContainer.changeTo<PlanetScene>() }
            }
        }
    }
}
