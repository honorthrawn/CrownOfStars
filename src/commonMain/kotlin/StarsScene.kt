
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class StarsScene(
    val gs: GalaxyState,
    val es: EmpireState,
    val ps: PlayerState,
    val ai: ComputerPlayerCore,
    val cps: ComputerPlayerState
) : BasicScene() {
    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var defenseReadout: Text
    private lateinit var turnReadout: Text
    private lateinit var systemActionsPanel: Container
    private var friendlyFleets = arrayListOf<Image>()
    private var enemyFleets = arrayListOf<Image>()

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val yellowStar = resourcesVfs[StarType.getImagePath(StarType.YELLOW)].readBitmap()
        val blueStar = resourcesVfs[StarType.getImagePath(StarType.BLUE)].readBitmap()
        val redStar = resourcesVfs[StarType.getImagePath(StarType.RED)].readBitmap()

        val ourFlag = resourcesVfs["ui/player_fleet_banner.png"].readBitmap()
        val enemyFlag = resourcesVfs["ui/enemy_fleet_banner.png"].readBitmap()

        val cellSize = views.virtualWidth / 10.0
        val cellHeight = views.virtualHeight / 10.0
        var x: Double
        var y = 0.00
        var nI = 0
        for (i in 0..3) {
            x = 0.00
            for (j in 0..9) {
                val rect = roundRect(cellSize, cellHeight, 5.0, 5.0, Colors.BLACK, Colors.WHITE, 5.00) {
                    position(x, y)
                    onClick { showSystemActions(i, j) } //for some weird reason trying to use nI always results in 40
                }

                val fleetImage = image(ourFlag) {
                    visible = gs.stars[nI]!!.playerFleet.isPresent()
                    scaledHeight = cellHeight / 2.0
                    scaledWidth = cellSize / 2.0
                    alignTopToTopOf(rect)
                    alignLeftToLeftOf(rect)
                }
                friendlyFleets.add(fleetImage)

                val enemyFleetImage = image(enemyFlag) {
                    visible = gs.stars[nI]!!.enemyFleet.isPresent()
                    scaledHeight = cellHeight / 2.0
                    scaledWidth = cellSize / 2.0
                    alignTopToTopOf(rect)
                    alignRightToRightOf(rect)
                }
                enemyFleets.add(enemyFleetImage)

                val textColor = when (gs.stars[nI]!!.getAllegiance()) {
                    Allegiance.Unoccupied -> Colors.WHITE
                    Allegiance.Player -> Colors.CYAN
                    Allegiance.Enemy -> Colors.RED
                }
                text(gs.stars[nI]!!.name, 11.00, textColor, gameFont) {
                    centerXOn(rect)
                    alignBottomToBottomOf(rect, 2.00)
                }

                var starImage = image(
                    when (gs.stars[nI]!!.type) {
                        StarType.YELLOW -> yellowStar
                        StarType.BLUE -> blueStar
                        StarType.RED -> redStar
                    }
                ) {
                    scaledWidth = 30.0
                    scaledHeight = 30.0
                    centerOn(rect)
                }
                x += cellSize
                nI++
            }
            y += cellHeight
        }

        uiVerticalStack {
            position(5.00, y + cellHeight)
            padding = 10.00

            val turn = "STARDATE: ${gs.starDate}"
            val Ship = "METAL: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}"
            val Research = "RESEARCH: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints}"
            val Organic = "ORGANICS: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}"
            val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints} "

            turnReadout = text(turn, 50.00, Colors.CYAN, gameFont)
            shipsReadout = text(Ship, 50.00, Colors.CYAN, gameFont)
            farmerReadout = text(Organic, 50.00, Colors.CYAN, gameFont)
            scienceReadout = text(Research, 50.00, Colors.CYAN, gameFont)
            defenseReadout = text(defense, 50.00, Colors.CYAN, gameFont)

            uiHorizontalStack {
                uiButton("NEXT TURN") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { nextTurn() }
                }

                uiButton("TECH") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { sceneContainer.changeTo<ChooseResearchRealm>() }
                }

                uiButton("SAVE") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick {
                        es.save()
                        gs.save()
                    }
                }

                uiButton("QUIT") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { kotlin.system.exitProcess(0) }
                }
            }
        }
    }

    private suspend fun nextTurn() {
        ai.setShipCosts()
        ai.takeTurn()
        es.addProduction(gs)
        gs.nextTurn()
        updateScreen()
    }

    private fun updateScreen() {
        val turn = "STARDATE: ${gs.starDate}"
        val Ship = "METAL: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints} "
        val Research = "RESEARCH: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints} "
        val Organic = "ORGANICS: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints} "
        val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints}  "
        turnReadout.text = turn
        defenseReadout.text = defense
        shipsReadout.text = Ship
        farmerReadout.text = Organic
        scienceReadout.text = Research
        for (i in 0..gs.stars.count() - 1) {
            enemyFleets[i].visible = gs.stars[i]!!.enemyFleet.isPresent()
            friendlyFleets[i].visible = gs.stars[i]!!.playerFleet.isPresent()
        }
    }

    private suspend fun showSystemActions(x: Int, y: Int) {
        if (ps.operation == operationType.MOVINGFLEET) {
            movechosenShips(x, y)
            return
        }

        // Remove any existing actions panel before creating a new one
        if (::systemActionsPanel.isInitialized) {
            systemActionsPanel.removeFromParent()
        }

        val panel = sceneContainer.container()
        systemActionsPanel = panel

        panel.roundRect(
            sceneWidth / 2.00,
            sceneHeight / 4.00,
            5.0,
            5.0,
            Colors.BLACK
        ) {
            uiVerticalStack {
                scaledWidth = sceneWidth / 2.00

                text("SYSTEM OPERATIONS", 50.00, Colors.CYAN, gameFont)

                uiButton("PLANETS") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        panel.removeFromParent()
                        ps.operation = operationType.SELECTION
                        ps.activePlayerStar = x * 10 + y
                        sceneContainer.changeTo<PlanetsScene>()
                    }
                }

                uiButton("MOVE OUR SHIPS") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        panel.removeFromParent()
                        ps.operation = operationType.SELECTION
                        clickedFleet(x, y)
                    }
                }

                uiButton("VIEW ENEMY SHIPS") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        panel.removeFromParent()
                        clickedEnemyFleet(x, y)
                    }
                }

                uiButton("CLOSE") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        panel.removeFromParent()
                    }
                }
            }
        }
    }

    private suspend fun movechosenShips(x: Int, y: Int) {
        val destination = x * 10 + y
        val origin = ps.activePlayerStar

        // todo: range/supply validation goes here

        moveShips(origin, destination, shipType.TERRAFORMATTER_HUMAN, ps.chosenTerraformers)
        moveShips(origin, destination, shipType.COLONY_HUMAN, ps.chosenColony)
        moveShips(origin, destination, shipType.GALLEON_HUMAN, ps.chosenGalleon)
        moveShips(origin, destination, shipType.CORVETTE_HUMAN, ps.chosenCorvette)
        moveShips(origin, destination, shipType.CRUISER_HUMAN, ps.chosenCruiser)
        moveShips(origin, destination, shipType.BATTLESHIP_HUMAN, ps.chosenBattleship)

       ps.reset()

        val destStar = gs.stars[destination]!!

        if (
            destStar.playerFleet?.isPresent() == true &&
            destStar.enemyFleet?.isPresent() == true
        ) {
            ps.activePlayerStar = destination
            cps.activeBattleStar = destination
            sceneContainer.changeTo<FleetCombatScene>()
            return
        }

        updateScreen()
    }


    private fun moveShips(origin: Int, destination: Int, type: shipType, count: Int) {
        repeat(count) {
            val shipMoving =
                gs.stars[origin]?.playerFleet?.removeShipFromFleetForMove(type)

            if (shipMoving != null) {
                shipMoving.hasMoved = true

                // Best if destination playerFleet is guaranteed non-null.
                gs.stars[destination]!!.playerFleet!!.add(shipMoving)
            }
        }
    }

    private suspend fun clickedFleet(x: Int, y: Int) {
        println("we clicked a fleet")
        ps.activePlayerStar = x * 10 + y

        if (!gs.stars[ps.activePlayerStar]!!.playerFleet.isPresent()) {
            showNoGo("Our Fleet is not here")
            return
        }

        //Assume want to move whole fleet
        if (gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformersCount() == 0 &&
            gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyShipCount() == 0 &&
            gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCount() == 0 &&
            gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCount() == 0 &&
            gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCount() == 0 &&
            gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCount() == 0
        ) {
            showNoGo("The fleet has already moved this turn")
            return
        }

        ps.chosenTerraformers = gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformersCount()
        ps.chosenColony = gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyShipCount()
        ps.chosenGalleon = gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCount()
        ps.chosenCorvette = gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCount()
        ps.chosenCruiser = gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCount()
        ps.chosenBattleship = gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCount()
        sceneContainer.changeTo<DeployShipsScene>()
    }

    private suspend fun clickedEnemyFleet(x: Int, y: Int) {
        println("we clicked an enemy fleet")
        ps.activePlayerStar = x * 10 + y
        if (!gs.stars[ps.activePlayerStar]!!.enemyFleet.isPresent()) {
            showNoGo("No Enemy Fleet Here")
            return
        }
        sceneContainer.changeTo<ViewShipsScene>()
    }
}

//TODO: power graphs?
//TODO: settings like turn music on or off or color preference?
//TODO: Random events?
//TODO: Rename ships to metal/mining
//TODO: what if computer player moves its fleet to attack?
