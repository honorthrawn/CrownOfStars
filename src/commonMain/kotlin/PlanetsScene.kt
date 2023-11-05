
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
    private lateinit var selectOperationDialog: RoundRect
    private var planetTexts = mutableListOf<Text>()

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

        for ((i, planet) in gs.stars[ps.activePlayerStar]!!.planets.values.withIndex()) {
            val fileName = when (planet.type) {
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

            val planetTextColor = when (planet.ownerIndex) {
                Allegiance.Unoccupied -> Colors.WHITE
                Allegiance.Player -> Colors.CYAN
                Allegiance.Enemy -> Colors.RED
            }

            val turnCounter = gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform
            val planetTxt: String = if (turnCounter == -1) {
                "${planet.name} - ${planet.type}"
            } else {
                "${planet.name} - ${planet.type} $turnCounter"
            }
            planetTexts.add(i, text(planetTxt, 50.00, planetTextColor, font)
            {
                centerXOnStage()
                alignTopToTopOf(planetImage, 12.0)
            }
            )
            planetImage.addUpdater { updatePlanet(planetImage, i) }
            planetImage.onClick { planetClicked(i) }
            starty -= 200
        }

        val fileName = when (gs.stars[ps.activePlayerStar]!!.type) {
            StarType.YELLOW -> "stars/Star cK gK eg9.bmp"
            StarType.BLUE -> "stars/Star B supeg5.bmp"
            StarType.RED -> "stars/Star M supeg5.bmp"
        }
        val starImage = image(resourcesVfs[fileName].readBitmap()) {
            scale(0.5)
            position(width / 2, 800.00)
        }
        text(gs.stars[ps.activePlayerStar]!!.name, 50.00, Colors.CYAN, font)
        {
            centerXOn(starImage)
            alignTopToTopOf(starImage, 12.0)
        }
        uiButton("BACK")
        {
            alignBottomToBottomOf(background)
            textColor = Colors.GOLD
            textFont = font
            onClick { sceneContainer.changeTo<StarsScene>() }
        }
}

    private suspend fun planetClicked(index: Int) {
        //TODO: need to update for enemy held worlds
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Player) {
            ps.activePlayerPlanet = index; sceneContainer.changeTo<PlanetScene>()
        } else {
            val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
            selectOperationDialog =
                this.sceneContainer.container().roundRect(
                    sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                    Colors.BLACK
                )
                {
                    centerOnStage()
                    uiVerticalStack {
                        scaledWidth = sceneWidth / 2.00
                        uiButton("COLONIZE")
                        {
                            textColor = Colors.GOLD
                            textFont = font
                            onClick { colonizePlanet(index) }
                        }
                        uiButton("TERRAFORM")
                        {
                            textColor = Colors.GOLD
                            textFont = font
                            onClick { terraformPlanet(index) }
                        }
                    }
                }
        }
    }

    private suspend fun terraformPlanet(index: Int) {
        //val message = "turns left: ${gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform}"
        //println(message)
        selectOperationDialog.removeFromParent()
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformersCount() >= 1) {
                if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.type != PlanetType.SUPERTERRAN) {
                    if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform == -1) {
                        ps.terraformingIndex = index
                        gs.stars[ps.activePlayerStar]!!.planets[ps.terraformingIndex]!!.startTerraforming()
                        gs.stars[ps.activePlayerStar]!!.playerFleet.destroyShip(shipType.TERRAFORMATTER_HUMAN)
                        sceneContainer.changeTo<terraformingScene>()
                    } else {
                        showNoGo("This world is already being terraformed!")
                    }
                } else {
                    showNoGo("This world is as good as it gets!")
                }
            } else {
                showNoGo("You must have at least one Terraformer in system to terraform the world")
            }
        } else {
            showNoGo("Planet must be unoccupied to terraform")
        }
        ps.operation = operationType.SELECTION
    }

    private suspend fun colonizePlanet(index: Int) {
        selectOperationDialog.removeFromParent()
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyShipCount() >= 1) {
                ps.activePlayerPlanet = index
                gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex = Allegiance.Player
                gs.stars[ps.activePlayerStar]!!.planets[index]!!.farmers = 1u
                gs.stars[ps.activePlayerStar]!!.playerFleet.destroyShip(shipType.COLONY_HUMAN)
                ps.operation = operationType.SELECTION
                sceneContainer.changeTo<ColonyScene>()
            } else {
                showNoGo("At least one colony ship in system to establish colony")
            }
        } else {
            showNoGo("Planet must be unoccupied to establish colony")
        }
        ps.operation = operationType.SELECTION
    }


    private fun updatePlanet(planet: Image, index: Int) {
        if (planet.x >= sceneWidth - planet.width) {
            direction[index] = true
        }
        if (planet.x <= 0) {
            direction[index] = false
        }
        if (direction[index]) {
            planet.x -= (index + 1)
        } else {
            planet.x += (index + 1)
        }

        val planetTextColor = when (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex) {
            Allegiance.Unoccupied -> Colors.WHITE
            Allegiance.Player -> Colors.CYAN
            Allegiance.Enemy -> Colors.RED
        }

        val turnCounter = gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform
        val planetTxt: String = if (turnCounter == -1) {
            "${gs.stars[ps.activePlayerStar]!!.planets[index]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[index]!!.type}"
        } else {
            "${gs.stars[ps.activePlayerStar]!!.planets[index]!!.name} - ${gs.stars[ps.activePlayerStar]!!.planets[index]!!.type} $turnCounter"
        }
        planetTexts[index].text = planetTxt
        planetTexts[index].color = planetTextColor
    }

    private suspend fun showNoGo(requirements: String) {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        notEnoughDialog =
            this.sceneContainer.container().roundRect(
                sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                Colors.BLACK
            )
            {
                centerOnStage()
                uiVerticalStack {
                    scaledWidth = sceneWidth / 2.00
                    text(requirements, 50.00, Colors.CYAN, font)
                    uiButton("CLOSE")
                    {
                        textFont = font
                        textColor = Colors.GOLD
                        onClick { closeMessage() }
                    }
                }
            }
    }

    private fun closeMessage() {
        notEnoughDialog.removeFromParent()
    }
}
