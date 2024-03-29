
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class PlanetScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
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

        println("Active player star: ${ps.activePlayerStar} active player planet: ${ps.activePlayerPlanet}")

        val fileName = gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.getImagePath()
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
        }

        val planet = "${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.type} "
        val planetText = text( planet, 50.00, Colors.CYAN, font) {
            alignLeftToLeftOf(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        uiVerticalStack {
            alignLeftToRightOf(planetImage)
            alignTopToBottomOf(planetText, 12.0)
            baseReadout = text("BASES: 00", 50.00, Colors.CYAN, font)
        }

        uiVerticalStack(400.00, UI_DEFAULT_PADDING){
            position(000.00, 300.00)
            uiHorizontalStack {
                padding = 10.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerUp(WorkerType.FARMING) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerDown(WorkerType.FARMING) }
                }
                farmerReadout = text("FARMING: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerUp(WorkerType.SHIPS) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerDown(WorkerType.SHIPS) }
                }
                shipsReadout = text("SHIPS:   00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                uiButton("ADD")  {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerUp(WorkerType.DEFENSE) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerDown(WorkerType.DEFENSE) }
                }
                defenseReadout = text("DEFENSE: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerUp(WorkerType.SCIENCE) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onWorkerDown(WorkerType.SCIENCE) }
                }
                scienceReadout = text("SCIENCE: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                unassignedReadout= text("UNASSIGNED: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                 uiButton("POPULATION") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { growPopulation() }
                }
                uiButton("SHIPS") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { lauchShip() }
                }
                uiButton("DEF BASE") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buyBase() }
                }
                uiButton("BACK") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { sceneContainer.changeTo<PlanetsScene>() }
                }
            }

            //TODO: Add a stores readout here

        }
        updateReadouts()
    }

    private fun updateReadouts() {
        val farmerReadoutString = "FARMING: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.farmers}"
        farmerReadout.text = farmerReadoutString
        val shipsReadoutString = "SHIPS: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.shipbuilders}"
        shipsReadout.text = shipsReadoutString
        val defenseReadoutString = "DEFENSE: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.defworkers}"
        defenseReadout.text = defenseReadoutString
        val scienceReadoutString = "SCIENCE: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.scientists}"
        scienceReadout.text = scienceReadoutString
        val unassignedReadoutString = "UNASSIGNED: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.workerPool}"
        unassignedReadout.text = unassignedReadoutString
        val baseReadoutString = "BASES: ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.defenseBases}"
        baseReadout.text = baseReadoutString
    }

    private fun onWorkerUp(workertype: WorkerType) {
        gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.increaseWorker(workertype)
        updateReadouts()
    }

    private fun onWorkerDown(workertype: WorkerType) {
        gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.decreaseWorker(workertype)
        updateReadouts()
    }

    private suspend fun growPopulation()  {
        if(gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.canGrowPopulation()) {
            if (es.empires[Allegiance.Player.ordinal]!!.addPopulation()) {
                gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.addPopulation(1u)
                updateReadouts()
            } else {
                showNoGo("Requires at least 50 organics to grow population")
            }
        } else {
            showNoGo("This world cannot support more people")
        }
    }

    private suspend fun lauchShip() {
        sceneContainer.changeTo<BuyShipScene>()
    }

    private suspend fun buyBase() = if(es.empires[Allegiance.Player.ordinal]!!.buildBase())  {
        gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.addBase(1u)
        updateReadouts()
    } else {
        showNoGo("Requires at least 50 defense")
    }
}

