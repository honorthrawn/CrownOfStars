
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class EnemyPlanetScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var defenseReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var unassignedReadout: Text
    private lateinit var baseReadout: Text
    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        println("Active player star: ${ps.activePlayerStar} active player planet: ${ps.bombardIndex}")

        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getImagePath()
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
        }

        val planet = "${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.type} "
        val planetText = text( planet, 50.00, Colors.RED, font) {
            alignLeftToLeftOf(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        uiVerticalStack {
            alignLeftToRightOf(planetImage)
            alignTopToBottomOf(planetText, 12.0)
            baseReadout = text("BASES: 00", 50.00, Colors.RED, font)
        }

        uiVerticalStack(400.00, UI_DEFAULT_PADDING){
            position(000.00, 300.00)
            padding = 10.00
            farmerReadout = text("FARMING: 00", 50.00, Colors.RED, font)
            shipsReadout = text("SHIPS:   00", 50.00, Colors.RED, font)
            defenseReadout = text("DEFENSE: 00", 50.00, Colors.RED, font)
            scienceReadout = text("SCIENCE: 00", 50.00, Colors.RED, font)
            unassignedReadout= text("UNASSIGNED: 00", 50.00, Colors.RED, font)
            uiButton("BACK") {
               textColor = Colors.GOLD
               textFont = font
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
