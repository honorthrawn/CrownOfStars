import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class PlanetsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {

    private val direction = mutableListOf<Boolean>()
    private lateinit var notEnoughDialog: RoundRect

    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        println("ACTIVE STAR: ${ps.activePlayerStar}")

        val startx = 200
        var starty = 600

        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }

        for((i, planet) in gs.stars[ps.activePlayerStar]!!.planets.values.withIndex())
        {
            val fileName = when( planet.type) {
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
                scale(0.5)
                position(startx, starty)
            }
            direction.add(i, false)
            planetImage.addUpdater { updatePlanet(planetImage,i) }
            planetImage.onClick { planetClicked(i, font)  }

            val planetTextColor = when(planet.ownerIndex)
            {
                Allegiance.Unoccupied -> Colors.WHITE
                Allegiance.Player -> Colors.CYAN
                Allegiance.Enemy -> Colors.RED
            }

            var turnCounter = gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform
            val planetTxt: String
            if(turnCounter == -1) {
                planetTxt = "${planet.name} - ${planet.type}"
            } else
            {
                planetTxt = "${planet.name} - ${planet.type} ($turnCounter)"
            }
            text( planetTxt, 50.00, planetTextColor, font)
            {
                centerXOn(planetImage)
                alignTopToTopOf(planetImage, 12.0)
            }
            starty -= 200
          }

        val fileName = when( gs.stars[ps.activePlayerStar]!!.type) {
            StarType.YELLOW -> "stars/Star cK gK eg9.bmp"
            StarType.BLUE -> "stars/Star B supeg5.bmp"
            StarType.RED -> "stars/Star M supeg5.bmp"
        }
        val starImage = image(resourcesVfs[fileName].readBitmap()) {
            scale(0.5)
            position( width/2, 800.00)
        }
        text( gs.stars[ps.activePlayerStar]!!.name, 50.00, Colors.CYAN, font)
        {
            centerXOn(starImage)
            alignTopToTopOf(starImage, 12.0)
        }

        uiHorizontalStack {
            position(300, 0)
            padding = 15.00
            text("BACK", 50.00, Colors.GOLD, font)
            {
                onClick { sceneContainer.changeTo<StarsScene>() }
            }
            text("COLONIZE", 50.00, Colors.GOLD, font)
            {
                onClick { ps.operation = operationType.COLONIZE }
            }
            text("TERRAFORM", 50.00, Colors.GOLD, font)
            {
                onClick { ps.operation = operationType.TERRAFORM }
            }
        }
    }

    private suspend fun Container.planetClicked(index: Int, font: Font)
    {
        when(ps.operation) {
            operationType.SELECTION -> selectPlanet(index, font)
            operationType.MOVINGFLEET -> showNoGo("Invalid Mode") //shouldn't happen
            operationType.COLONIZE -> colonizePlanet(index, font)
            operationType.TERRAFORM -> terraformPlanet(index, font)
        }
    }

    private suspend fun terraformPlanet(index: Int, font: Font)
    {
        val message = "turns left: ${gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform}"
        println(message)
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet!!.getTerraformersCount() >= 1) {
                if(gs.stars[ps.activePlayerStar]!!.planets[index]!!.type != PlanetType.SUPERTERRAN) {
                    if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform == -1) {
                        ps.terraformingIndex = index
                        gs.stars[ps.activePlayerStar]!!.planets[ps.terraformingIndex]!!.startTerraforming()
                        gs.stars[ps.activePlayerStar]!!.playerFleet!!.removeShipFromFleet(shipType.TERRAFORMATTER_HUMAN)
                        sceneContainer.changeTo<terraformingScene>()
                    } else
                    {
                        showNoGo("This world is already being terraformed!")
                    }
                }
                else
                {
                    showNoGo("This world is as good as it gets!")
                }
            }
            else
            {
                showNoGo("You must have at least one Terraformer in system to terraform the world")
            }
        } else {
            showNoGo("Planet must be unoccupied to terraform")
        }
        ps.operation = operationType.SELECTION
    }

    private suspend fun selectPlanet(index: Int, font: Font)
    {
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Player) {
            ps.activePlayerPlanet = index; sceneContainer.changeTo<PlanetScene>()
        } else {
            showNoGo("Not Your World")
        }
    }

    private suspend fun colonizePlanet(index: Int, font: Font)
    {
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet!!.getColonyShipCount() >= 1) {
                ps.activePlayerPlanet = index
                gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex = Allegiance.Player
                gs.stars[ps.activePlayerStar]!!.planets[index]!!.farmers = 1u
                gs.stars[ps.activePlayerStar]!!.playerFleet!!.removeShipFromFleet(shipType.COLONY_HUMAN)
                ps.operation = operationType.SELECTION
                sceneContainer.changeTo<ColonyScene>()
            } else {
                showNoGo("At least one colony ship in system to establish colony")
            }
        } else
        {
            showNoGo("Planet must be unoccupied to establish colony")
        }
        ps.operation = operationType.SELECTION
    }


    private fun updatePlanet(planet: Image, index: Int)
    {
        if(planet.x >= sceneWidth - planet.width) {
            direction[index] = true
        }
        if(planet.x <= 0 )
        {
            direction[index] = false
        }
        if(direction[index])
        {
            planet.x -= (index + 1)
        } else
        {
            planet.x += (index + 1)
        }
    }

    private suspend fun showNoGo(requirements: String) {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        notEnoughDialog =
            this.sceneContainer.container().roundRect(sceneWidth/2.00, sceneHeight / 4.00, 5.0, 5.0,
                Colors.BLACK)
            {
                centerOnStage()
                uiVerticalStack {
                    width = sceneWidth / 2.00
                    //text("Not enough resources", 50.00, Colors.CYAN, font)
                    //{
                    //    autoScaling = true
                    //}
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
