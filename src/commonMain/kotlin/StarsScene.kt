
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*

class StarsScene(
    val gs: GalaxyState,
    val es: EmpireState,
    val ps: PlayerState,
    val ai: ComputerPlayerCore,
    val cps: ComputerPlayerState,
    val bc: BonusCalculator
) : BasicScene() {
    private data class RagnarokAdvanceResult(
        val collapsedStarIndex: Int?,
        val changed: Boolean,
        val showAftermath: Boolean
    )

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
        addDefaultBackground()

        val starImages = StarType.values().associateWith {
            resourcesVfs[StarType.getImagePath(it)].readBitmap()
        }

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
                val star = gs.stars[nI]!!
                val rect = roundRect(cellSize, cellHeight, 5.0, 5.0, Colors.BLACK, Colors.WHITE, 5.00) {
                    position(x, y)
                    onClick { showSystemActions(i, j) } //for some weird reason trying to use nI always results in 40
                }

                val fleetImage = image(ourFlag) {
                    visible = star.playerFleet.isPresent()
                    scaledHeight = cellHeight / 2.0
                    scaledWidth = cellSize / 2.0
                    alignTopToTopOf(rect)
                    alignLeftToLeftOf(rect)
                }
                friendlyFleets.add(fleetImage)

                val enemyFleetImage = image(enemyFlag) {
                    visible = star.enemyFleet.isPresent()
                    scaledHeight = cellHeight / 2.0
                    scaledWidth = cellSize / 2.0
                    alignTopToTopOf(rect)
                    alignRightToRightOf(rect)
                }
                enemyFleets.add(enemyFleetImage)

                val textColor = when (star.getAllegiance()) {
                    Allegiance.Unoccupied -> Colors.WHITE
                    Allegiance.Player -> Colors.CYAN
                    Allegiance.Enemy -> Colors.RED
                }
                text(star.name, 11.00, textColor, gameFont) {
                    centerXOn(rect)
                    alignBottomToBottomOf(rect, 2.00)
                }

                if (star.isRagnarokProtocolActive()) {
                    text("R${star.turnsLeftRagnarok}", 16.00, Colors["#FFB000"], gameFont) {
                        centerXOn(rect)
                        alignTopToTopOf(rect, 5.0)
                    }
                }

                if (star.type == StarType.BLACK_HOLE) {
                    roundRect(40.0, 40.0, 20.0, 20.0, Colors["#16050A"], Colors["#FFB000"], 1.5) {
                        centerOn(rect)
                    }
                }

                image(
                    starImages[star.type]!!
                ) {
                    scaledWidth = if (star.type == StarType.BLACK_HOLE) 34.0 else 30.0
                    scaledHeight = if (star.type == StarType.BLACK_HOLE) 34.0 else 30.0
                    centerOn(rect)
                }
                x += cellSize
                nI++
            }
            y += cellHeight
        }

        uiVerticalStack {
            position(5.00, cellHeight * 4.0 + 18.0)
            padding = 8.00

            val turn = "STARDATE: ${gs.starDate}"
            val Ship = "METAL: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}"
            val Research = "RESEARCH: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints}"
            val Organic = "ORGANICS: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}"
            val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints} "

            turnReadout = text(turn, 44.00, Colors.CYAN, gameFont)
            shipsReadout = text(Ship, 44.00, Colors.CYAN, gameFont)
            farmerReadout = text(Organic, 44.00, Colors.CYAN, gameFont)
            scienceReadout = text(Research, 44.00, Colors.CYAN, gameFont)
            defenseReadout = text(defense, 44.00, Colors.CYAN, gameFont)

            uiHorizontalStack {
                padding = 10.00
                uiButton("NEXT TURN") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { nextTurn() }
                }

                uiButton("TECH") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { showChooseResearchRealmDialog(es, ps) }
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
        val combatStars = gs.stars.values.filter { it.hasCombat() }
        for( star in combatStars) {
            //I think I actually got my x and y flipped around building the grid -- but easier to change here
            //than possibly break other things
            ps.activePlayerStar = star.yloc * 10 + star.xloc
            cps.activeBattleStar = star.yloc * 10 + star.xloc
            sceneContainer.changeTo<FleetCombatScene>()
        }
        es.addProduction(gs)
        gs.nextTurn()
        val ragnarokResult = advanceRagnarokProtocols()
        updateScreen()
        if (ragnarokResult.collapsedStarIndex != null) {
            ps.activePlayerStar = ragnarokResult.collapsedStarIndex
            if (ragnarokResult.showAftermath) {
                sceneContainer.changeTo<RagnarokAftermathScene>()
            } else {
                sceneContainer.changeTo<StarsScene>()
            }
            return
        }
        if (ragnarokResult.changed) {
            sceneContainer.changeTo<StarsScene>()
            return
        }

        val winner = ai.checkForVictory()
        when(winner) {
            Allegiance.Unoccupied -> return;
            Allegiance.Player -> playerVictory()
            Allegiance.Enemy -> enemyVictory()
            null -> return;
        }
    }

    suspend fun playerVictory() {
        ps.playerVictory = true
        sceneContainer.changeTo<YouWinScene>()
    }


    suspend fun enemyVictory() {
        ps.catastrophicDefeat = !playerHasWorlds()
        sceneContainer.changeTo<YouLostScene>()
    }

    private fun playerHasWorlds(): Boolean {
        return gs.stars.values.any { star ->
            star.planets.values.any { planet -> planet.ownerIndex == Allegiance.Player }
        }
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

        if (::systemActionsPanel.isInitialized) {
            systemActionsPanel.removeFromParent()
        }

        val panel = showGameDialog(
            title = "SYSTEM OPERATIONS",
            width = sceneWidth / 2.0,
            height = sceneHeight / 3.0
        ) { dialog ->

            uiVerticalStack {
                scaledWidth = sceneWidth / 2.0 - 40.0

                uiButton("PLANETS") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        dialog.removeFromParent()
                        ps.reset()
                        ps.activePlayerStar = x * 10 + y
                        sceneContainer.changeTo<PlanetsScene>()
                    }
                }

                uiButton("RAGNAROK PROTOCOL") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        dialog.removeFromParent()
                        ps.reset()
                        ps.activePlayerStar = x * 10 + y
                        startRagnarokProtocol()
                    }
                }

                uiButton("MOVE OUR SHIPS") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        dialog.removeFromParent()
                        ps.reset()
                        ps.operation = operationType.SELECTION
                        clickedFleet(x, y)
                    }
                }

                uiButton("VIEW ENEMY SHIPS") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        ps.reset()
                        dialog.removeFromParent()
                        clickedEnemyFleet(x, y)
                    }
                }

                uiButton("CLOSE") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        ps.reset()
                        dialog.removeFromParent()
                    }
                }
            }
        }

        systemActionsPanel = panel
    }

    private suspend fun startRagnarokProtocol() {
        val star = gs.stars[ps.activePlayerStar]!!

        if(star.type == StarType.BLACK_HOLE) {
            showNoGo("System is already a BLACK HOLE")
            return
        }

        if (star.isRagnarokProtocolActive()) {
            showNoGo("Ragnarok Protocol is already charging. ${star.turnsLeftRagnarok} turns remain.")
            return
        }

        if (!hasSingularityLance() || !star.playerFleet.isBatteshipsPresent()) {
            showNoGo("You must have Singularity Lance weapon and a battleship in system to start")
            return
        }

        if (showRagnarokConfirmationDialog(star.name)) {
            star.startRagnarokProtocol()
            ps.reset()
            sceneContainer.changeTo<StarsScene>()
        }
    }

    private fun hasSingularityLance(): Boolean {
        val playerEmpire = es.empires[Allegiance.Player.ordinal] ?: return false
        return playerEmpire.techTags.any { techId ->
            val tech = ai.techs.findTech(techId, TechRealm.WEAPONS)
            tech?.key == "SINGULARITY_LANCE" || tech?.name == "Singularity Lance"
        }
    }

    private suspend fun showRagnarokConfirmationDialog(starName: String): Boolean {
        val result = CompletableDeferred<Boolean>()
        val overlay = sceneContainer.container()

        overlay.solidRect(sceneWidth, sceneHeight, Colors["#000000CC"])

        val width = sceneWidth * 0.88
        val height = sceneHeight * 0.68
        val dialogX = (sceneWidth - width) / 2.0
        val dialogY = (sceneHeight - height) / 2.0

        val dialog = overlay.container {
            position(dialogX, dialogY)
        }

        dialog.roundRect(width, height, 16.0, 16.0, Colors["#130509"], Colors["#B00020"], 4.0)
        dialog.roundRect(width - 18.0, height - 18.0, 12.0, 12.0, Colors["#26090F"], Colors["#FFB000"], 2.0) {
            position(9.0, 9.0)
        }

        var yPos = 28.0
        dialog.text("RAGNAROK PROTOCOL", 30.0, Colors["#FFB000"], gameFont) {
            y = yPos
            centerXOn(dialog)
        }

        yPos += 58.0
        val warningParagraphs = listOf(
            "You are about to begin stellar collapse in the $starName system.",
            "The Singularity Lance must charge for 3 turns.",
            "At least one battleship must remain in this system until the countdown completes.",
            "When collapse occurs, all colonies, planetary bodies and enemy fleet in this system will be destroyed.",
            "This system will become a permanent black hole and can never be colonized again.",
            "History will remember this act."
        )

        warningParagraphs.forEach { paragraph ->
            wrapText(paragraph, 44).forEach { line ->
                dialog.text(line, 18.0, Colors["#FFD8D8"], gameFont) {
                    y = yPos
                    centerXOn(dialog)
                }
                yPos += 34.0
            }

            yPos += 12.0
        }

        val buttonWidth = 220.0
        val standDownWidth = 180.0
        val buttonHeight = 48.0
        val buttonGap = 20.0
        val buttonRowWidth = buttonWidth + buttonGap + standDownWidth
        val buttonY = height - 66.0
        val buttonX = (width - buttonRowWidth) / 2.0

        dialog.uiButton("INITIATE RAGNAROK", width = buttonWidth, height = buttonHeight) {
            position(buttonX, buttonY)
            textFont = gameFont
            textColor = Colors["#FFB000"]
            onClick {
                overlay.removeFromParent()
                if (!result.isCompleted) {
                    result.complete(true)
                }
            }
        }

        dialog.uiButton("STAND DOWN", width = standDownWidth, height = buttonHeight) {
            position(buttonX + buttonWidth + buttonGap, buttonY)
            textFont = gameFont
            textColor = Colors.CYAN
            onClick {
                overlay.removeFromParent()
                if (!result.isCompleted) {
                    result.complete(false)
                }
            }
        }

        return result.await()
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""

        for (word in words) {
            val candidate = if (current.isBlank()) word else "$current $word"

            if (candidate.length > maxChars) {
                if (current.isNotBlank()) {
                    lines.add(current)
                }
                current = word
            } else {
                current = candidate
            }
        }

        if (current.isNotBlank()) {
            lines.add(current)
        }

        return lines
    }

    private fun advanceRagnarokProtocols(): RagnarokAdvanceResult {
        var changed = false
        var collapsedStarIndex: Int? = null
        var showAftermath = false

        for ((starIndex, star) in gs.stars) {
            when (star.advanceRagnarokProtocol()) {
                RagnarokProtocolAdvanceResult.INACTIVE -> {}
                RagnarokProtocolAdvanceResult.CANCELED,
                RagnarokProtocolAdvanceResult.CHARGING -> changed = true
                RagnarokProtocolAdvanceResult.READY_TO_COLLAPSE -> {
                    changed = true
                    if (ps.blackHolesCreatedByPlayer == 0 && collapsedStarIndex == null) {
                        showAftermath = true
                    }
                    collapseRagnarokProtocol(starIndex)
                    if (collapsedStarIndex == null) {
                        collapsedStarIndex = starIndex
                    }
                }
            }
        }

        return RagnarokAdvanceResult(collapsedStarIndex, changed, showAftermath)
    }

    private fun collapseRagnarokProtocol(starIndex: Int) {
        val star = gs.stars[starIndex]!!

        ps.blackHolesCreatedByPlayer++
        ps.totalEnemyShipsDestroyed += star.enemyFleet.getTotalShipCount()

        for (planet in star.planets.values) {
            when (planet.ownerIndex) {
                Allegiance.Player -> {
                    ps.coloniesLost++
                    ps.colonistsLost += planet.getTotalPopulation().toInt()
                }

                Allegiance.Enemy -> {
                    ps.enemyPopulationKilled += planet.getTotalPopulation().toInt()
                }

                Allegiance.Unoccupied -> {}
            }
        }

        star.type = StarType.BLACK_HOLE
        star.cancelRagnarokProtocol()
        star.planets.clear()
        star.enemyFleet.clear()
    }

    private suspend fun movechosenShips(x: Int, y: Int) {
        val destination = x * 10 + y
        val origin = ps.activePlayerStar

        // todo: range/supply validation goes here
        val destStar = gs.stars[destination]!!
        val originStar = gs.stars[origin]!!
        val distance = gs.gridDistance(originStar, destStar)
        val speed = bc.getSpeed(Allegiance.Player)
        if(distance > speed) {
            showNoGo("You can only move $speed sectors at a time")
            ps.reset()
            return
        }

        moveShips(origin, destination, shipType.TERRAFORMATTER_HUMAN, ps.chosenTerraformers)
        moveShips(origin, destination, shipType.COLONY_HUMAN, ps.chosenColony)
        moveShips(origin, destination, shipType.GALLEON_HUMAN, ps.chosenGalleon)
        moveShips(origin, destination, shipType.CORVETTE_HUMAN, ps.chosenCorvette)
        moveShips(origin, destination, shipType.CRUISER_HUMAN, ps.chosenCruiser)
        moveShips(origin, destination, shipType.BATTLESHIP_HUMAN, ps.chosenBattleship)

       ps.reset()

        if (
         destStar.hasCombat()
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

        if (!(gs.stars[ps.activePlayerStar]!!.playerFleet.isTerraformerAvailableToMove() ||
            gs.stars[ps.activePlayerStar]!!.playerFleet.isColonyAvailableToMove() ||
            gs.stars[ps.activePlayerStar]!!.playerFleet.isCorvetteAvailableToMove() ||
            gs.stars[ps.activePlayerStar]!!.playerFleet.isCruiserAvailableToMove() ||
            gs.stars[ps.activePlayerStar]!!.playerFleet.isBattleshipAvailableToMove() ||
            gs.stars[ps.activePlayerStar]!!.playerFleet.isGalleonAvailableToMove()) ) {
            showNoGo("The fleet has already moved this turn")
            return
        }

        //Assume want to move whole fleet
        ps.chosenTerraformers = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableTerraformersCount()
        ps.chosenColony = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableColonyShipCount()
        ps.chosenGalleon = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableGalleonCount()
        ps.chosenCorvette = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableCorvetteCount()
        ps.chosenCruiser = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableCruiserCount()
        ps.chosenBattleship = gs.stars[ps.activePlayerStar]!!.playerFleet.getMovableBattleShipCount()
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
