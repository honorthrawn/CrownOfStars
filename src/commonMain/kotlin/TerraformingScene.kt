
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class TerraformingScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.getLandscapeImagePath()
        val planetImage = image(resourcesVfs[fileName].readBitmap())  {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val message = gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.name
        val turnsNeeded = "It will take ${gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.turnsLeftTerraform} turns to complete"
        uiVerticalStack {
            scaledWidth = sceneWidth.toDouble()
            scaledHeight = sceneHeight.toDouble()
            text("You started Terraforming on", 50.00, Colors.CYAN, font )
            text(message, 50.00, Colors.CYAN, font)
            text(turnsNeeded, 50.00, Colors.CYAN, font)
            uiButton("CLOSE") {
                textColor = Colors.GOLD
                textFont = font
                onClick {  sceneContainer.changeTo<PlanetsScene>() }
            }
        }
    }
}

