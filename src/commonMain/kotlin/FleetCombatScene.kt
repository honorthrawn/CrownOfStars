
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlin.random.*

class FleetCombatScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState, val cp: ComputerPlayerCombat, val techTree: TechTree) : BasicScene() {

    private var terraformerSlot: Container? = null
    private var colonyShipSlot: Container? = null
    private var galleonSlot: Container? = null
    private var corvetteSlot: Container? = null
    private var cruiserSlot: Container? = null
    private var battleShipSlot: Container? = null

    private var enemyColonyShipSlot: Container? = null
    private var enemyGalleonSlot: Container? = null
    private var enemyCorvetteSlot: Container? = null
    private var enemyCruiserSlot: Container? = null
    private var enemyBattleShipSlot: Container? = null

    private var terraformerCounter: Text? = null
    private var terraformerImage: Image? = null

    private var colonyShipCounter: Text? = null
    private var colonyShipImage: Image? = null

    private var galleonCounter: Text? = null
    private var galleonImage: Image? = null

    private var corvetteCounter: Text? = null
    private var corvetteImage: Image? = null

    private var cruiserCounter: Text? = null
    private var cruiserImage: Image? = null

    private var battleShipCounter: Text? = null
    private var battleShipImage: Image? = null

    private var enemyColonyShipCounter: Text? = null
    private var enemyColonyShipImage: Image? = null

    private var enemyGalleonCounter: Text? = null
    private var enemyGalleonImage: Image? = null

    private var enemyCorvetteCounter: Text? = null
    private var enemyCorvetteImage: Image? = null

    private var enemyCruiserCounter: Text? = null
    private var enemyCruiserImage: Image? = null

    private var enemyBattleShipCounter: Text? = null
    private var enemyBattleShipImage: Image? = null

    private var playerShipHighLight: RoundRect? = null
    private var enemyShipHighLight: RoundRect? = null
    private var playerShipTypeChosen: shipType? = null
    private var enemyShipTypeChosen: shipType? = null
    private lateinit var header: Text
    private lateinit var messageLine: Text
    private lateinit var messageLine2: Text

    //Get new counts from galaxy state
    private var playerTerrafomers = 0
    private var colonyShipCount = 0
    private var corvetteCount = 0
    private var cruiserCount = 0
    private var battleShipCount = 0
    private var galleonCount = 0

    private var enemyColonyShipCount = 0
    private var enemyCorvetteCount = 0
    private var enemyCruiserCount = 0
    private var enemyBattleShipCount = 0
    private var enemyGalleonCount = 0

    private var round = 1
    private var playerInitiative = 0
    private var enemyInitiative = 0
    private var playerAccuray = 0
    private var enemyAccuracy = 0
    private var playerLowDamage = 0
    private var playerHighDamage = 0
    private var enemyLowDamage = 0
    private var enemyHighDamage = 0
    private var playerEvasion = 0
    private var playerSoak = 0
    private var enemyEvasion = 0
    private var enemySoak = 0
    private var playerWeapon = "wooden sword"
    private var enemyWeapon = "wooden sword"
    private var resolvingCombat = false

    override suspend fun SContainer.sceneMain() {
        setupBonuses()

        ps.musicSceneContainer?.changeTo<WarMusicScene>()

        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        header = text("Battle at ${gs.stars[ps.activePlayerStar]!!.name} Round: ${round}", 25.00, Colors.GOLD, font) {
            alignTopToTopOf(background)
            centerXOnStage()
        }
        messageLine = text("Click on your ship on left, then on enemy to fire on right", 25.00, Colors.GOLD, font) {
            alignTopToBottomOf(header)
            alignLeftToLeftOf(background)
        }
        messageLine2 = text("", 25.00, Colors.GOLD, font) {
            alignTopToBottomOf(messageLine)
            alignLeftToLeftOf(background)
        }

        val usefulHeight =
            sceneHeight.toDouble() - header.scaledHeight - messageLine.scaledHeight - messageLine2.scaledHeight

        getCounts()
        ps.resetBattleStats()

        println("PLAYER SHIPS: terraformers=$playerTerrafomers colony=$colonyShipCount corvette=$corvetteCount cruiser=$cruiserCount battleship=$battleShipCount galleon=$galleonCount")
        println("ENEMY SHIPS: colony=$enemyColonyShipCount corvette=$enemyCorvetteCount cruiser=$enemyCruiserCount battleship=$enemyBattleShipCount galleon=$enemyGalleonCount")

        var playerY = messageLine2.y + messageLine2.scaledHeight + 10.0
        val playerX = 60.0

        if (playerTerrafomers > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_terraformer.png",
                playerTerrafomers,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.TERRAFORMATTER_HUMAN)
            }

            terraformerSlot = view.slot
            terraformerImage = view.image
            terraformerCounter = view.counter


            playerY += 120.0
        }

        if (colonyShipCount > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_colony_ship.png",
                colonyShipCount,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.COLONY_HUMAN)
            }

            colonyShipSlot = view.slot
            colonyShipImage = view.image
            colonyShipCounter = view.counter
            playerY += 120.0
        }

        if (corvetteCount > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_corvette.png",
                corvetteCount,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.CORVETTE_HUMAN)
            }

            corvetteSlot = view.slot
            corvetteImage = view.image
            corvetteCounter = view.counter
            playerY += 120.0
        }

        if (cruiserCount > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_cruiser.png",
                cruiserCount,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.CRUISER_HUMAN)
            }

            cruiserSlot = view.slot
            cruiserImage = view.image
            cruiserCounter = view.counter
            playerY += 120.0
        }

        if (battleShipCount > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_battleship.png",
                battleShipCount,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.BATTLESHIP_HUMAN)
            }

            battleShipSlot = view.slot
            battleShipImage = view.image
            battleShipCounter = view.counter
            playerY += 120.0
        }

        if (galleonCount > 0) {
            val view = combatShipSlot(
                playerX,
                playerY,
                "ships/player_galleon.png",
                galleonCount,
                Colors.CYAN,
                font,
                faceLeft = true
            ) {
                clickOnPlayerShip(shipType.GALLEON_HUMAN)
            }

            galleonSlot = view.slot
            galleonImage = view.image
            galleonCounter = view.counter
        }

        var enemyY = messageLine2.y + messageLine2.scaledHeight + 10.0
        val enemyX = sceneWidth - 170.0

        if (enemyColonyShipCount > 0) {
            val view = combatShipSlot(
                enemyX,
                enemyY,
                "ships/enemy_colony_ship.png",
                enemyColonyShipCount,
                Colors.RED,
                font,
                faceLeft = false
            ) {
                clickonEnemyShip(shipType.COLONY_ENEMY)
            }

            enemyColonyShipSlot = view.slot
            enemyColonyShipImage = view.image
            enemyColonyShipCounter = view.counter
            enemyY += 120.0
        }

        if (enemyCorvetteCount > 0) {
            val view = combatShipSlot(
                enemyX,
                enemyY,
                "ships/enemy_corvette.png",
                enemyCorvetteCount,
                Colors.RED,
                font,
                faceLeft = false
            ) {
                clickonEnemyShip(shipType.CORVETTE_ENEMY)
            }

            enemyCorvetteSlot = view.slot
            enemyCorvetteImage = view.image
            enemyCorvetteCounter = view.counter
            enemyY += 120.0
        }

        if (enemyCruiserCount > 0) {
            val view = combatShipSlot(
                enemyX,
                enemyY,
                "ships/enemy_cruiser.png",
                enemyCruiserCount,
                Colors.RED,
                font,
                faceLeft = false
            ) {
                clickonEnemyShip(shipType.CRUISER_ENEMY)
            }

            enemyCruiserSlot = view.slot
            enemyCruiserImage = view.image
            enemyCruiserCounter = view.counter
            enemyY += 120.0
        }

        if (enemyBattleShipCount > 0) {
            val view = combatShipSlot(
                enemyX,
                enemyY,
                "ships/enemy_battleship.png",
                enemyBattleShipCount,
                Colors.RED,
                font,
                faceLeft = false
            ) {
                clickonEnemyShip(shipType.BATTLESHIP_ENEMY)
            }

            enemyBattleShipSlot = view.slot
            enemyBattleShipImage = view.image
            enemyBattleShipCounter = view.counter
            enemyY += 120.0
        }

        if (enemyGalleonCount > 0) {
            val view = combatShipSlot(
                enemyX,
                enemyY,
                "ships/enemy_galleon.png",
                enemyGalleonCount,
                Colors.RED,
                font,
                faceLeft = false
            ) {
                clickonEnemyShip(shipType.GALLEON_ENEMY)
            }

            enemyGalleonSlot = view.slot
            enemyGalleonImage = view.image
            enemyGalleonCounter = view.counter
        }

        val playerInitiativeRoll = Random.nextInt(1, 6) + playerInitiative
        val enemyRoll = Random.nextInt(1, 6) + enemyInitiative
        if (enemyRoll > playerInitiativeRoll) {
            resolveOneEnemyFireAndRefresh()

            if (!isBattleOver()) {
                checkForNewRound()
            }
        }
    }

    private suspend fun getCounts() {
        //Get new counts from galaxy state
        playerTerrafomers = gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformerCombatCount()
        colonyShipCount = gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyCombatCount()
        corvetteCount = gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount()
        cruiserCount = gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount()
        battleShipCount = gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount()
        galleonCount = gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCombatCount()

        //val playerTerrafomers = gs.stars[ps.activePlayerStar]!!.enemyFleet.getTerraformerCombatCount()
        enemyColonyShipCount = gs.stars[ps.activePlayerStar]!!.enemyFleet.getColonyCombatCount()
        enemyCorvetteCount = gs.stars[ps.activePlayerStar]!!.enemyFleet.getCorvetteCombatCount()
        enemyCruiserCount = gs.stars[ps.activePlayerStar]!!.enemyFleet.getCruiserCombatCount()
        enemyBattleShipCount = gs.stars[ps.activePlayerStar]!!.enemyFleet.getBattleShipCombatCount()
        enemyGalleonCount = gs.stars[ps.activePlayerStar]!!.enemyFleet.getGalleonCombatCount()
    }

    private suspend fun clickOnPlayerShip(shipTypeClicked: shipType) {
        //Probably need to clear the messages somewhere, doing it here at least for now
        messageLine.text = ""
        messageLine2.text = ""

        if (gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipTypeClicked)) {
            playerShipHighLight?.removeFromParent()

            playerShipTypeChosen = shipTypeClicked

            playerShipHighLight = when (shipTypeClicked) {
                shipType.TERRAFORMATTER_HUMAN -> showHighlight(terraformerSlot!!, Colors.GREEN)
                shipType.COLONY_HUMAN -> showHighlight(colonyShipSlot!!, Colors.GREEN)
                shipType.CORVETTE_HUMAN -> showHighlight(corvetteSlot!!, Colors.GREEN)
                shipType.CRUISER_HUMAN -> showHighlight(cruiserSlot!!, Colors.GREEN)
                shipType.BATTLESHIP_HUMAN -> showHighlight(battleShipSlot!!, Colors.GREEN)
                shipType.GALLEON_HUMAN -> showHighlight(galleonSlot!!, Colors.GREEN)
                else -> null
            }
        } else {
            messageLine.text = "Already FIRED this round"
        }
    }


    private suspend fun clickonEnemyShip(shipTypeClicked: shipType) {
        if (resolvingCombat) return

        if (playerShipTypeChosen == null) {
            messageLine.text = "Choose one of your ships first"
            return
        }

        resolvingCombat = true
        try {
            enemyShipHighLight?.removeFromParent()

            enemyShipTypeChosen = shipTypeClicked

            enemyShipHighLight = when (shipTypeClicked) {
                shipType.COLONY_ENEMY -> showHighlight(enemyColonyShipSlot!!, Colors.RED)
                shipType.CORVETTE_ENEMY -> showHighlight(enemyCorvetteSlot!!, Colors.RED)
                shipType.CRUISER_ENEMY -> showHighlight(enemyCruiserSlot!!, Colors.RED)
                shipType.BATTLESHIP_ENEMY -> showHighlight(enemyBattleShipSlot!!, Colors.RED)
                shipType.GALLEON_ENEMY -> showHighlight(enemyGalleonSlot!!, Colors.RED)
                else -> null
            }

            fire()
            getCounts()
            updateScreen()
            checkForBattleOver()

            // Normal alternating response: enemy fires one ship type after the player fires.
            if (!isBattleOver() && enemyHasShipsThatCanFire()) {
                resolveOneEnemyFireAndRefresh()
            }

            // If the player is now out of ship types for this round,
            // let the enemy finish any remaining ship types.
            if (!isBattleOver() && !playerHasShipsThatCanFire() && enemyHasShipsThatCanFire()) {
                resolveAllEnemyFire()
            }

            if (!isBattleOver()) {
                checkForNewRound()
            }
        } finally {
            resolvingCombat = false
        }
    }

    private suspend fun fire() {
        if (playerShipTypeChosen != null) {
            val gunsFiring = gs.stars[ps.activePlayerStar]!!.playerFleet.getGunMountCount(playerShipTypeChosen!!)
            println("Number of guns: ${gunsFiring}")
            var hits = 0
            var totaldamage = 0
            if (enemyShipTypeChosen != null) {
                for (i in 1..gunsFiring) {
                    val roll = Random.nextInt(1, 10)
                    println("Rolled: ${roll}")
                    val adjustedRoll = roll - enemyEvasion + playerAccuray
                    if (adjustedRoll > 5) {
                        hits++
                        var damageRolled = Random.nextInt(playerLowDamage, playerHighDamage + 1) - enemySoak
                        if (damageRolled <= 0) {
                            damageRolled = 1
                        }
                        totaldamage += damageRolled
                        println("Rolled damage: ${damageRolled}")
                        val destroyed =
                            gs.stars[ps.activePlayerStar]!!.enemyFleet.damageShip(enemyShipTypeChosen!!, damageRolled)
                        if (destroyed) {
                            ps.enemyShipsDestroyed++
                            val countRemaining = when (enemyShipTypeChosen) {
                                shipType.COLONY_ENEMY -> gs.stars[ps.activePlayerStar]!!.enemyFleet.getColonyCombatCount()
                                shipType.CORVETTE_ENEMY -> gs.stars[ps.activePlayerStar]!!.enemyFleet.getCorvetteCombatCount()
                                shipType.CRUISER_ENEMY -> gs.stars[ps.activePlayerStar]!!.enemyFleet.getCruiserCombatCount()
                                shipType.BATTLESHIP_ENEMY -> gs.stars[ps.activePlayerStar]!!.enemyFleet.getBattleShipCombatCount()
                                shipType.GALLEON_ENEMY -> gs.stars[ps.activePlayerStar]!!.enemyFleet.getGalleonCombatCount()
                                //Player shouldn't fire on own ships
                                shipType.TERRAFORMATTER_HUMAN -> 0
                                shipType.COLONY_HUMAN -> 0
                                shipType.CORVETTE_HUMAN -> 0
                                shipType.CRUISER_HUMAN -> 0
                                shipType.BATTLESHIP_HUMAN -> 0
                                shipType.GALLEON_HUMAN -> 0
                                null -> 0
                            }
                            //If we destroyed the last ship, then don't evaluate more damage
                            if (countRemaining == 0) {
                                break
                            }
                        }
                    }
                }
                ps.totalDamageDealt += totaldamage
                //need to mark that the ship type has fired
                gs.stars[ps.activePlayerStar]!!.playerFleet.setFiredGuns(playerShipTypeChosen!!)
                playerShipHighLight?.removeFromParent()
                playerShipHighLight = null
                enemyShipHighLight?.removeFromParent()
                enemyShipHighLight = null
                playerShipTypeChosen = null
                enemyShipTypeChosen = null
                messageLine.text = "We fired ${gunsFiring} times ${hits} hits ${totaldamage} damage"
                println("We fired ${playerWeapon} ${gunsFiring} times ${hits} hits ${totaldamage} damage")
            }
        }
    }

    private suspend fun resolveOneEnemyFireAndRefresh() {
        val fired = resolveEnemyFire()
        if (fired) {
            getCounts()
            updateScreen()
            checkForBattleOver()
        }
    }

    private suspend fun resolveEnemyFire(): Boolean {
        println("Resolving enemy FIRE")

        val firingShips = cp.getShipsToFire()
        val targetShips = cp.getShipsToFireOn()

        if (firingShips == null || targetShips == null) {
            return false
        }

        if (!gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(firingShips)) {
            println("Enemy AI picked $firingShips but it cannot fire")
            return false
        }

        val gunsFiring = gs.stars[ps.activePlayerStar]!!.enemyFleet.getGunMountCount(firingShips)

        var hits = 0
        var totaldamage = 0

        for (i in 1..gunsFiring) {
            val roll = Random.nextInt(1, 10)
            val adjustedRoll = roll + enemyAccuracy - playerEvasion

            if (adjustedRoll > 5) {
                hits++

                var damageApplied = Random.nextInt(enemyLowDamage, enemyHighDamage + 1) - playerSoak
                if (damageApplied <= 0) damageApplied = 1

                totaldamage += damageApplied

                val destroyed =
                    gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(targetShips, damageApplied)

                if (destroyed) {
                    ps.shipsLost++

                    val countRemaining = when (targetShips) {
                        shipType.TERRAFORMATTER_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformerCombatCount()
                        shipType.COLONY_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyCombatCount()
                        shipType.CORVETTE_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount()
                        shipType.CRUISER_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount()
                        shipType.BATTLESHIP_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount()
                        shipType.GALLEON_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCombatCount()
                        else -> 0
                    }

                    if (countRemaining == 0) {
                        break
                    }
                }
            }
        }

        ps.totalDamgeReceived += totaldamage

        gs.stars[ps.activePlayerStar]!!.enemyFleet.setFiredGuns(firingShips)

        messageLine2.text =
            "Enemy fired $enemyWeapon $gunsFiring times $hits hits doing $totaldamage damage"

        println("enemy fired $enemyWeapon $gunsFiring times $hits hits doing $totaldamage damage")

        return true
    }

    private suspend fun updateScreen() {
        //Update all counters
        terraformerCounter?.text = playerTerrafomers.toString()
        colonyShipCounter?.text = colonyShipCount.toString()
        corvetteCounter?.text = corvetteCount.toString()
        cruiserCounter?.text = cruiserCount.toString()
        battleShipCounter?.text = battleShipCount.toString()
        galleonCounter?.text = galleonCount.toString()

        enemyColonyShipCounter?.text = enemyColonyShipCount.toString()
        enemyCorvetteCounter?.text = enemyCorvetteCount.toString()
        enemyCruiserCounter?.text = enemyCruiserCount.toString()
        enemyBattleShipCounter?.text = enemyBattleShipCount.toString()
        enemyGalleonCounter?.text = enemyGalleonCount.toString()

        if (corvetteCount == 0 && corvetteSlot != null) {
            corvetteSlot!!.removeFromParent()
            corvetteSlot = null
            corvetteImage = null
            corvetteCounter = null
        }
        if (playerTerrafomers == 0 && terraformerSlot != null) {
            terraformerSlot!!.removeFromParent()
            terraformerSlot = null
            terraformerImage = null
            terraformerCounter = null
        }

        if (colonyShipCount == 0 && colonyShipSlot != null) {
            colonyShipSlot!!.removeFromParent()
            colonyShipSlot = null
            colonyShipImage = null
            colonyShipCounter = null
        }

        if (cruiserCount == 0 && cruiserSlot != null) {
            cruiserSlot!!.removeFromParent()
            cruiserSlot = null
            cruiserImage = null
            cruiserCounter = null
        }

        if (battleShipCount == 0 && battleShipSlot != null) {
            battleShipSlot!!.removeFromParent()
            battleShipSlot = null
            battleShipImage = null
            battleShipCounter = null
        }

        if (galleonCount == 0 && galleonSlot != null) {
            galleonSlot!!.removeFromParent()
            galleonSlot = null
            galleonImage = null
            galleonCounter = null
        }

        if (enemyColonyShipCount == 0 && enemyColonyShipSlot != null) {
            enemyColonyShipSlot!!.removeFromParent()
            enemyColonyShipSlot = null
            enemyColonyShipImage = null
            enemyColonyShipCounter = null
        }

        if (enemyCorvetteCount == 0 && enemyCorvetteSlot != null) {
            enemyCorvetteSlot!!.removeFromParent()
            enemyCorvetteSlot = null
            enemyCorvetteImage = null
            enemyCorvetteCounter = null
        }

        if (enemyCruiserCount == 0 && enemyCruiserSlot != null) {
            enemyCruiserSlot!!.removeFromParent()
            enemyCruiserSlot = null
            enemyCruiserImage = null
            enemyCruiserCounter = null
        }

        if (enemyBattleShipCount == 0 && enemyBattleShipSlot != null) {
            enemyBattleShipSlot!!.removeFromParent()
            enemyBattleShipSlot = null
            enemyBattleShipImage = null
            enemyBattleShipCounter = null
        }

        if (enemyGalleonCount == 0 && enemyGalleonSlot != null) {
            enemyGalleonSlot!!.removeFromParent()
            enemyGalleonSlot = null
            enemyGalleonImage = null
            enemyGalleonCounter = null
        }
    }

    private fun isBattleOver(): Boolean {
        val star = gs.stars[ps.activePlayerStar]!!
        return !star.playerFleet.isPresent() || !star.enemyFleet.isPresent()
    }

    private fun enemyHasShipsThatCanFire(): Boolean {
        val star = gs.stars[ps.activePlayerStar]!!

        return (enemyColonyShipCount > 0 && star.enemyFleet.canFire(shipType.COLONY_ENEMY)) ||
            (enemyCorvetteCount > 0 && star.enemyFleet.canFire(shipType.CORVETTE_ENEMY)) ||
            (enemyCruiserCount > 0 && star.enemyFleet.canFire(shipType.CRUISER_ENEMY)) ||
            (enemyBattleShipCount > 0 && star.enemyFleet.canFire(shipType.BATTLESHIP_ENEMY)) ||
            (enemyGalleonCount > 0 && star.enemyFleet.canFire(shipType.GALLEON_ENEMY))
    }

    private fun playerHasShipsThatCanFire(): Boolean {
        val star = gs.stars[ps.activePlayerStar]!!

        return (colonyShipCount > 0 && star.playerFleet.canFire(shipType.COLONY_HUMAN)) ||
            (corvetteCount > 0 && star.playerFleet.canFire(shipType.CORVETTE_HUMAN)) ||
            (cruiserCount > 0 && star.playerFleet.canFire(shipType.CRUISER_HUMAN)) ||
            (battleShipCount > 0 && star.playerFleet.canFire(shipType.BATTLESHIP_HUMAN)) ||
            (galleonCount > 0 && star.playerFleet.canFire(shipType.GALLEON_HUMAN))
    }


    private suspend fun checkForNewRound() {
        if (isRoundOver()) {
            round++
            gs.stars[ps.activePlayerStar]!!.playerFleet.nextCombatTurn()
            gs.stars[ps.activePlayerStar]!!.enemyFleet.nextCombatTurn()
            header.text = "Battle at ${gs.stars[ps.activePlayerStar]!!.name} Round: ${round}"
        }
    }

    private suspend fun checkForBattleOver() {
        ps.totalRounds = round
        if (gs.stars[ps.activePlayerStar]!!.enemyFleet.isPresent() && !gs.stars[ps.activePlayerStar]!!.playerFleet.isPresent()) {
            sceneContainer.changeTo<LoseFleetCombatScene>()
        } else if (gs.stars[ps.activePlayerStar]!!.playerFleet.isPresent() && !gs.stars[ps.activePlayerStar]!!.enemyFleet.isPresent()) {
            sceneContainer.changeTo<WinFleetCombatScene>()
        }
        //Otherwise continue
    }

    private suspend fun resolveAllEnemyFire() {
        while (!isBattleOver() && enemyHasShipsThatCanFire()) {
            resolveEnemyFire()
            getCounts()
            updateScreen()
            checkForBattleOver()
        }
    }

    private suspend fun isRoundOver(): Boolean {
        //Start with the assumption that the round is over
        var roundOver = true

        //There's a new round if all ships still present have fired -- EXCEPT for terraformers which have no Gun Mounts
        if (colonyShipCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.COLONY_HUMAN)) {
            roundOver = false
        } else if (corvetteCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.CORVETTE_HUMAN)) {
            roundOver = false
        } else if (cruiserCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.CRUISER_HUMAN)) {
            roundOver = false
        } else if (battleShipCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.BATTLESHIP_HUMAN)) {
            roundOver = false
        } else if (galleonCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.GALLEON_HUMAN)) {
            roundOver = false
        } else if (enemyColonyShipCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.COLONY_ENEMY)) {
            roundOver = false
        } else if (enemyCorvetteCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.CORVETTE_ENEMY)) {
            roundOver = false
        } else if (enemyCruiserCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.CRUISER_ENEMY)) {
            roundOver = false
        } else if (enemyBattleShipCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.BATTLESHIP_ENEMY)) {
            roundOver = false
        } else if (enemyGalleonCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.GALLEON_ENEMY)) {
            roundOver = false
        }

        return (roundOver)
    }

    suspend fun setupBonuses() {
        val bonusCalculator = BonusCalculator(es, techTree)
        playerInitiative = bonusCalculator.getInitiativeBonus(Allegiance.Player)
        enemyInitiative = bonusCalculator.getInitiativeBonus(Allegiance.Enemy)
        playerAccuray = bonusCalculator.getAccuracyBonus(Allegiance.Player)
        enemyAccuracy = bonusCalculator.getAccuracyBonus(Allegiance.Enemy)
        val playerDamageCode = bonusCalculator.getDamageCode(Allegiance.Player)
        playerLowDamage = playerDamageCode.first
        playerHighDamage = playerDamageCode.second
        playerWeapon = playerDamageCode.third
        val enemyDamageCode = bonusCalculator.getDamageCode(Allegiance.Enemy)
        enemyLowDamage = enemyDamageCode.first
        enemyHighDamage = enemyDamageCode.second
        enemyWeapon = enemyDamageCode.third
        playerEvasion = bonusCalculator.getEvasion(Allegiance.Player)
        enemyEvasion = bonusCalculator.getEvasion(Allegiance.Enemy)
        playerSoak = bonusCalculator.getDamageSoaked(Allegiance.Player)
        enemySoak = bonusCalculator.getDamageSoaked(Allegiance.Enemy)
    }
    private data class CombatShipView(
        val slot: Container,
        val image: Image,
        val counter: Text
    )

    private suspend fun Container.combatShipSlot(
        x: Double,
        y: Double,
        path: String,
        count: Int,
        counterColor: RGBA,
        font: Font,
        faceLeft: Boolean = false,
        onClickHandler: suspend () -> Unit
    ): CombatShipView {
        val slotSize = 110.0
        val maxShipWidth = 96.0
        val maxShipHeight = 88.0

        val slot = container {
            position(x, y)

            // Keep this visible while testing.
            // Change to Colors.TRANSPARENT after it works.
            solidRect(slotSize, slotSize, Colors.TRANSPARENT_BLACK) {
                onClick { onClickHandler() }
            }
        }

        val bitmap = resourcesVfs[path].readBitmap()

        val scaleAmount = minOf(
            maxShipWidth / bitmap.width.toDouble(),
            maxShipHeight / bitmap.height.toDouble()
        )

        val ship = slot.image(bitmap) {
            anchor(0.5, 0.5)
            position(slotSize / 2.0, slotSize / 2.0)

            scaleX = if (faceLeft) -scaleAmount else scaleAmount
            scaleY = scaleAmount
        }

        val counter = slot.text(count.toString(), 25.0, counterColor, font) {
            centerOn(slot)
        }

        return CombatShipView(slot, ship, counter)
    }
}
