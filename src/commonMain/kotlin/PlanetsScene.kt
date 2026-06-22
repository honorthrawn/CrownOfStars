
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class PlanetsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    private val direction = mutableListOf<Boolean>()
    private var selectOperationDialog: Container? = null
    private var showingSelectOperationDialog = false
    private var planetTexts = mutableListOf<Text>()

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        val background = addDefaultBackground()
        println("ACTIVE STAR: ${ps.activePlayerStar}")

        val startx = 200
        var starty = 600
        val planetButtonStack = uiVerticalStack {
            position(20.0, 120.0)
            padding = 8.0
        }

        for ((i, planet) in gs.stars[ps.activePlayerStar]!!.planets.values.withIndex()) {
            val fileName = planet.getImagePath()
            val planetImage = image(resourcesVfs[fileName].readBitmap()) {
                scaledWidth = 80.0
                scaledHeight = 80.0
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
            planetTexts.add(i, text(planetTxt, 50.00, planetTextColor, gameFont) {
                centerXOnStage()
                alignTopToTopOf(planetImage, 12.0) })

            planetButtonStack.uiButton(planet.name, width = 150.0, height = 42.0) {
                textColor = planetTextColor
                textFont = gameFont
                onClick { planetClicked(i) }
            }

            planetImage.addUpdater { updatePlanet(planetImage, i) }
            planetImage.onClick { planetClicked(i) }
            starty -= 200
        }

        val fileName = StarType.getImagePath(gs.stars[ps.activePlayerStar]!!.type)
        val starImage = image(resourcesVfs[fileName].readBitmap()) {
            centerXOnStage()
            alignBottomToBottomOf(background)
        }

        val starTextColor = when (gs.stars[ps.activePlayerStar]!!.getAllegiance()) {
            Allegiance.Unoccupied -> Colors.WHITE
            Allegiance.Player -> Colors.CYAN
            Allegiance.Enemy -> Colors.RED
        }
        text(gs.stars[ps.activePlayerStar]!!.name, 50.00, starTextColor, gameFont) {
            centerXOn(starImage)
            alignTopToTopOf(starImage, 12.0)
        }
        planetButtonStack.uiButton("BACK") {
            textColor = Colors.GOLD
            textFont = gameFont
            onClick { sceneContainer.changeTo<StarsScene>() }
        }

}

    private suspend fun planetClicked(index: Int) {
         if(!showingSelectOperationDialog) {
            showingSelectOperationDialog = true
            when (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex) {
                Allegiance.Player -> {
                    ps.activePlayerPlanet = index; sceneContainer.changeTo<PlanetScene>()
                }

                Allegiance.Unoccupied -> {
                    selectOperationDialog = showGameDialog(
                        title = "UNCLAIMED WORLD",
                        width = sceneWidth / 2.0,
                        height = sceneHeight / 3.0
                    ) { dialog ->
                        uiVerticalStack {
                            scaledWidth = sceneWidth / 2.0 - 40.0
                            uiButton("COLONIZE") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick { colonizePlanet(index) }
                            }
                            uiButton("TERRAFORM") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick { terraformPlanet(index) }
                            }
                            uiButton("BACK") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick {
                                    showingSelectOperationDialog = false
                                    dialog.removeFromParent()
                                }
                            }
                        }
                    }

                }

                Allegiance.Enemy -> {
                    selectOperationDialog = showGameDialog(
                        title = "ENEMY WORLD",
                        width = sceneWidth / 2.0,
                        height = sceneHeight / 3.0
                    ) { dialog ->
                        uiVerticalStack {
                            scaledWidth = sceneWidth / 2.0 - 40.0
                            uiButton("VIEW") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick { showEnemyPlanet(index) }
                            }
                            uiButton("BOMBARD") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick { bombardPlanet(index) }
                            }
                            uiButton("INVADE") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick { invadePlanet(index) }
                            }
                            uiButton("BACK") {
                                textColor = Colors.GOLD
                                textFont = gameFont
                                onClick {
                                    showingSelectOperationDialog = false
                                    dialog.removeFromParent()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun terraformPlanet(index: Int) {
        selectOperationDialog?.removeFromParent()
        showingSelectOperationDialog = false
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.isTerraformersPresent()) {
                if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.type != PlanetType.SUPERTERRAN) {
                    if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.turnsLeftTerraform == -1) {
                        ps.terraformIndex = index
                        gs.stars[ps.activePlayerStar]!!.planets[ps.terraformIndex]!!.startTerraforming()
                        gs.stars[ps.activePlayerStar]!!.playerFleet.destroyShip(shipType.TERRAFORMATTER_HUMAN)
                        sceneContainer.changeTo<TerraformingScene>()
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
        selectOperationDialog?.removeFromParent()
        showingSelectOperationDialog = false
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Unoccupied) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.isColonyPresent()) {
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

    private suspend fun showEnemyPlanet(index: Int) {
        selectOperationDialog?.removeFromParent()
        showingSelectOperationDialog = false
        ps.bombardIndex = index
        sceneContainer.changeTo<EnemyPlanetScene>()
    }

    private suspend fun bombardPlanet(index: Int) {
        selectOperationDialog?.removeFromParent()
        showingSelectOperationDialog = false
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Enemy) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.isWarshipsPresent()) {
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.isWarshipsCanBombard()) {
                    ps.bombardIndex = index
                    sceneContainer.changeTo<BombardScene>()
                } else {
                    showNoGo("Your warships may only bombard once per turn")
                }
            } else {
                showNoGo("You must have warships present to bombard an enemy held world")
            }
        } else {
            showNoGo("Your admirals will only bombard enemy held worlds")
        }
    }

    private suspend fun invadePlanet(index: Int) {
        selectOperationDialog?.removeFromParent()
        showingSelectOperationDialog = false
        if (gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Enemy) {
            if (gs.stars[ps.activePlayerStar]!!.playerFleet.isGalleonsPresent()) {
            ps.bombardIndex = index
            ps.chosenGalleon = gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonTotalCount()
            sceneContainer.changeTo<InvadeScene>()
            } else {
                showNoGo("You must have galleons present to invade an enemy held world")
            }
        } else {
            showNoGo("Your generals will only invade enemy held worlds")
        }
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

    override suspend fun sceneBeforeLeaving() {
        super.sceneBeforeLeaving()
        showingSelectOperationDialog = false
        selectOperationDialog?.removeFromParent()
    }
}
