import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*

class ColonyScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val message = gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.name
        uiVerticalStack {
            width = sceneWidth.toDouble()
            height = sceneHeight.toDouble()
            text("You started a new colony on", 50.00, Colors.CYAN, font )
            text(message, 50.00, Colors.CYAN, font)
            text("CLOSE", 50.00, Colors.GOLD, font)
            {
                onClick {  sceneContainer.changeTo<PlanetScene>() }
            }
        }
    }
}
