
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class EnemyPlanetScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var defenseReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var unassignedReadout: Text
    private lateinit var baseReadout: Text
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()
        println("Active player star: ${ps.activePlayerStar} active player planet: ${ps.bombardIndex}")

        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getImageCloseUpPath()
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
        }

        val planet = "${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.type} "
        val planetText = text( planet, 50.00, Colors.RED, gameFont) {
            alignLeftToLeftOf(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        uiVerticalStack {
            alignLeftToRightOf(planetImage)
            alignTopToBottomOf(planetText, 12.0)
            baseReadout = text("BASES: 00", 50.00, Colors.RED, gameFont)
        }

        uiVerticalStack(400.00, UI_DEFAULT_PADDING){
            position(000.00, 300.00)
            padding = 10.00
            farmerReadout = text("FARMING: 00", 50.00, Colors.RED, gameFont)
            shipsReadout = text("SHIPS:   00", 50.00, Colors.RED, gameFont)
            defenseReadout = text("DEFENSE: 00", 50.00, Colors.RED, gameFont)
            scienceReadout = text("SCIENCE: 00", 50.00, Colors.RED, gameFont)
            unassignedReadout= text("UNASSIGNED: 00", 50.00, Colors.RED, gameFont)
            uiButton("BACK") {
               textColor = Colors.GOLD
               textFont = gameFont
               onClick { sceneContainer.changeTo<PlanetsScene>() }
            }
        }
        updateReadouts()
    }
    private fun updateReadouts() {
        val farmerReadoutString = "FARMING: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.farmers}"
        farmerReadout.text = farmerReadoutString
        val shipsReadoutString = "SHIPS: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.shipbuilders}"
        shipsReadout.text = shipsReadoutString
        val defenseReadoutString = "DEFENSE: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defworkers}"
        defenseReadout.text = defenseReadoutString
        val scienceReadoutString = "SCIENCE: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.scientists}"
        scienceReadout.text = scienceReadoutString
        val unassignedReadoutString = "UNASSIGNED: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.workerPool}"
        unassignedReadout.text = unassignedReadoutString
        val baseReadoutString = "BASES: ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases}"
        baseReadout.text = baseReadoutString
    }
}
