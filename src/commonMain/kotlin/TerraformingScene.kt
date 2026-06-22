
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class TerraformingScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.getLandscapeImagePath()
        image(resourcesVfs[fileName].readBitmap())  {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val message = gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.name
        val turnsNeeded = "It will take ${gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.turnsLeftTerraform} turns to complete"
        var yPos = 0.00
        val padding = 5.00
        text("You started Terraforming on", 50.00, Colors.CYAN, gameFont ) {
            centerXOnStage()
            y = yPos
            yPos += (height + padding)
        }
        text(message, 50.00, Colors.CYAN, gameFont)  {
            centerXOnStage()
            y = yPos
            yPos += (height + padding)
        }
        text(turnsNeeded, 50.00, Colors.CYAN, gameFont) {
            centerXOnStage()
            y = yPos
            yPos += (height + padding)
        }
        uiButton("CLOSE") {
            centerXOnStage()
            y = yPos
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {  sceneContainer.changeTo<PlanetsScene>() }
        }
    }
}

