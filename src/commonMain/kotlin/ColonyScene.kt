
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ColonyScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.getLandscapeImagePath()
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val message = gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.name
        uiVerticalStack {
            scaledWidth = sceneWidth.toDouble()
            scaledHeight = sceneHeight.toDouble()
            text("You started a new colony on", 50.00, Colors.CYAN, gameFont )
            text(message, 50.00, Colors.CYAN, gameFont)
            uiButton("CLOSE")  {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {  sceneContainer.changeTo<PlanetScene>() }
            }
        }
    }

}
