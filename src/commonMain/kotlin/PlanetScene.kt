import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korge.ui.*

class PlanetScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {

    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var defenseReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var unassignedReadout: Text
    private lateinit var baseReadout: Text
    private lateinit var notEnoughDialog: RoundRect
    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        println("Active player star: ${ps.activePlayerStar} active player planet: ${ps.activePlayerPlanet}")

        val fileName = when(gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.type) {
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

        val planet = "${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.type} "
        text( planet, 50.00, Colors.CYAN, font)
        {
            alignLeftToLeftOf(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        uiVerticalStack {
            position(300, 0)
            centerXOn(background)
            alignTopToTopOf(planetImage, 12.0)
            text("BACK", 50.00, Colors.GOLD, font)
            {
                onClick { sceneContainer.changeTo<PlanetsScene>() }
            }
            baseReadout = text("BASES: 00", 50.00, Colors.CYAN, font)
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
                unassignedReadout= text("UNASSIGNED: 00", 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                text("WORKER", 50.00, Colors.GOLD, font)
                {
                    onClick { growPopulation() }
                }
                text("SHIPS", 50.00, Colors.GOLD, font)
                {
                    onClick { lauchShip() }
                }
                text("DEF BASE", 50.00, Colors.GOLD, font)
                {
                    onClick { buyBase() }
                }
            }
        }

        updateReadouts()
    }

    private fun updateReadouts()
    {
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

    private suspend fun growPopulation()
    {
        if(es.empires[Allegiance.Player.ordinal]!!.addPopulation())
        {
            gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.addPopulation(1u)
            updateReadouts()
        }
        else
        {
            showNotEnough("Requires at least 50 organics to grow population")
        }
    }

    private suspend fun lauchShip()
    {
        sceneContainer.changeTo<BuyShipScene>()
    }

    private suspend fun buyBase() = if(es.empires[Allegiance.Player.ordinal]!!.buildBase())
    {
        gs.stars[ps.activePlayerStar]!!.planets[ps.activePlayerPlanet]!!.addBase(1u)
        updateReadouts()
    } else
    {
        showNotEnough("Requires at least 50 defense")
    }


    private suspend fun showNotEnough(requirements: String) {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        notEnoughDialog =
            this.sceneContainer.container().roundRect(sceneWidth/2.00, sceneHeight / 4.00, 5.0, 5.0,
                Colors.BLACK)
            {
                centerOnStage()
                uiVerticalStack {
                    width = sceneWidth / 2.00
                    text("Not enough resources", 50.00, Colors.CYAN, font)
                    {
                        autoScaling = true
                    }
                    text(requirements, 50.00, Colors.CYAN, font)
                    text("CLOSE", 50.00, Colors.GOLD, font)
                    {
                        autoScaling = true
                        onClick { closeMessage() }
                    }
               }
        }
    }

    private fun closeMessage()
    {
        notEnoughDialog.removeFromParent()
    }

}

