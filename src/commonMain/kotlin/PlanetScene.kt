import com.soywiz.korge.component.docking.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.onClick
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.text.*
import com.soywiz.korma.geom.*

class PlanetScene(val gs: GalaxyState) : Scene() {

    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var defenseReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var unassignedReadout: Text

    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        println("Active player star: ${gs.activePlayerStar} active player planet: ${gs.activePlayerPlanet}")

        val fileName = when(gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.type) {
            PlanetType.TOXIC -> "planets/planet1.png"
            PlanetType.OCEAN -> "planets/planet2.png"
            PlanetType.TERRAN -> "planets/planet3.png"
            PlanetType.DESSERT -> "planets/planet4.png"
            PlanetType.VOLCANIC -> "planets/planet5.png"
            PlanetType.BARREN -> "planets/planet6.png"
            PlanetType.SUPERTERRAN -> "planets/planet7.png"
            PlanetType.TROPICAL -> "planets/planet10.png"
        }
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
        }

        text( gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.name, 50.00, Colors.CYAN, font)
        {
            centerXOn(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        text("BACK", 50.00,Colors.GOLD, font)
        {
            position(300, 0)
            centerXOn(background)
            onClick { sceneContainer.changeTo<PlanetsScene>() }
            alignTopToTopOf(planetImage, 12.0)
        }

        uiVerticalStack(400.00, UI_DEFAULT_PADDING){
            position(000.00, 300.00)
            uiHorizontalStack {
                padding = 10.00
                text(" ADD ", 50.00,Colors.GOLD, font)
                {
                    onClick { onWorkerUp(WorkerType.FARMING) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onWorkerDown(WorkerType.FARMING) }
                }
                farmerReadout = text("FARMING: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                text(" ADD ", 50.00,Colors.GOLD, font)
                {
                    onClick { onWorkerUp(WorkerType.SHIPS) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onWorkerDown(WorkerType.SHIPS) }
                }
                shipsReadout = text("SHIPS:   00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                text(" ADD ", 50.00,Colors.GOLD, font)
                {
                    onClick { onWorkerUp(WorkerType.DEFENSE) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onWorkerDown(WorkerType.DEFENSE) }
                }
                defenseReadout = text("DEFENSE: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                text(" ADD ", 50.00,Colors.GOLD, font)
                {
                    onClick { onWorkerUp(WorkerType.SCIENCE) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onWorkerDown(WorkerType.SCIENCE) }
                }
                scienceReadout = text("SCIENCE: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                unassignedReadout= text("UNASSIGNED: 00", 50.00, Colors.GOLD, font)
            }
        }

        updateReadouts()
    }

    private fun updateReadouts()
    {
        val farmerReadoutString = "FARMING: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.farmers}"
        farmerReadout.text = farmerReadoutString
        val shipsReadoutString = "SHIPS: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.shipbuilders}"
        shipsReadout.text = shipsReadoutString
        val defenseReadoutString = "DEFENSE: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.defworkers}"
        defenseReadout.text = defenseReadoutString
        val scienceReadoutString = "SCIENCE: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.scientists}"
        scienceReadout.text = scienceReadoutString
        val unassignedReadoutString = "UNASSIGNED: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.workerPool}"
        unassignedReadout.text = unassignedReadoutString
    }

    private fun onWorkerUp(workertype: WorkerType) {
        gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.increaseWorker(workertype)
        updateReadouts()
    }

    private fun onWorkerDown(workertype: WorkerType) {
        gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.decreaseWorker(workertype)
        updateReadouts()
    }


}
