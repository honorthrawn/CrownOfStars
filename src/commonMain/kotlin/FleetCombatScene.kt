
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlin.random.*

class FleetCombatScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState, val cp: ComputerPlayerCombat, val techTree: TechTree) : BasicScene() {

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
    private var enemyShipTypeChosen:  shipType? = null
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

    override suspend fun SContainer.sceneMain() {
        setupBonuses()

        ps.musicSceneContainer?.changeTo<WarMusicScene>()

        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/nebula.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        header = text("Battle at ${gs.stars[ps.activePlayerStar]!!.name} Round: ${round}", 25.00, Colors.GOLD, font) {
            alignTopToTopOf(background)
            centerXOnStage()
        }
        messageLine = text("Click on your ship on left, then on enemy to fire on right", 25.00, Colors.GOLD, font ) {
            alignTopToBottomOf(header)
            alignLeftToLeftOf(background)
        }
        messageLine2= text("", 25.00, Colors.GOLD, font ) {
            alignTopToBottomOf(messageLine)
            alignLeftToLeftOf(background)
        }

        val usefulHeight = sceneHeight.toDouble() - header.scaledHeight
        - messageLine.scaledHeight - messageLine2.scaledHeight

        getCounts()
        ps.resetBattleStats()

        uiVerticalStack {
            alignTopToBottomOf(messageLine2)
            positionX(150)
            padding = 25.00
            scaledHeight = usefulHeight

            if(playerTerrafomers > 0) {
                terraformerImage = image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.TERRAFORMATTER_HUMAN) }
                }
            }

            if(colonyShipCount > 0) {
                colonyShipImage = image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.COLONY_HUMAN) }
                }
            }

            if(corvetteCount > 0) {
                corvetteImage = image(resourcesVfs["ships/Human-Corvette.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.CORVETTE_HUMAN) }
                }
            }

            if(cruiserCount > 0) {
                cruiserImage = image(resourcesVfs["ships/Human-Cruiser.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.CRUISER_HUMAN) }
                }
            }

            if(battleShipCount > 0) {
                battleShipImage = image(resourcesVfs["ships/Human-Battleship.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.BATTLESHIP_HUMAN) }
                }
            }

            if( galleonCount > 0) {
                galleonImage = image(resourcesVfs["ships/Human-Frigate.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickOnPlayerShip(shipType.GALLEON_HUMAN) }
                }
            }
        }

        uiVerticalStack {
            alignRightToRightOf(background)
            alignTopToBottomOf(messageLine2)
            padding = 25.00
            scaledHeight = usefulHeight

            if(enemyColonyShipCount > 0) {
                enemyColonyShipImage = image(resourcesVfs["ships/Frigate.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickonEnemyShip(shipType.COLONY_ENEMY) }
                }
            }

            if(enemyCorvetteCount > 0) {
                enemyCorvetteImage = image(resourcesVfs["ships/Corvette.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickonEnemyShip(shipType.CORVETTE_ENEMY) }
                }
            }

            if(enemyCruiserCount > 0) {
                enemyCruiserImage = image(resourcesVfs["ships/Cruiser.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickonEnemyShip(shipType.CRUISER_ENEMY) }
                }
            }

            if(enemyBattleShipCount > 0) {
                enemyBattleShipImage = image(resourcesVfs["ships/Battleship.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickonEnemyShip(shipType.BATTLESHIP_ENEMY) }
                }
            }

            if(enemyGalleonCount > 0) {
                enemyGalleonImage = image(resourcesVfs["ships/Destroyer.png"].readBitmap()) {
                    scale(0.5, 0.5)
                    onClick { clickonEnemyShip(shipType.GALLEON_ENEMY) }
                }
            }
        }

        //This is also weird.   The counters won't be where I want them to be if I put them in the stack, even with
        //the centerOn call
        if(playerTerrafomers > 0) {
            terraformerCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformerCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(terraformerImage!!)
            }
        }

        if(colonyShipCount > 0) {
            colonyShipCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(colonyShipImage!!)
            }
        }

        if(galleonCount > 0) {
            galleonCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(galleonImage!!)
            }
        }

        if(corvetteCount > 0) {
            corvetteCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(corvetteImage!!)
            }
        }

        if(cruiserCount > 0) {
            cruiserCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(cruiserImage!!)
            }
        }

        if(battleShipCount > 0) {
            battleShipCounter = text(
                gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount().toString(),
                25.00, Colors.CYAN, font
            ) {
                centerOn(battleShipImage!!)
            }
        }


        if(enemyColonyShipCount > 0) {
            enemyColonyShipCounter = text(
                gs.stars[ps.activePlayerStar]!!.enemyFleet.getColonyCombatCount().toString(),
                25.00, Colors.RED, font
            ) {
                centerOn(enemyColonyShipImage!!)
            }
        }

        if(enemyGalleonCount > 0) {
            enemyGalleonCounter = text(
                gs.stars[ps.activePlayerStar]!!.enemyFleet.getGalleonCombatCount().toString(),
                25.00, Colors.RED, font
            ) {
                centerOn(enemyGalleonImage!!)
            }
        }

        if(enemyCorvetteCount > 0) {
            enemyCorvetteCounter = text(
                gs.stars[ps.activePlayerStar]!!.enemyFleet.getCorvetteCombatCount().toString(),
                25.00, Colors.RED, font
            ) {
                centerOn(enemyCorvetteImage!!)
            }
        }

        if(enemyCruiserCount > 0) {
            enemyCruiserCounter = text(
                gs.stars[ps.activePlayerStar]!!.enemyFleet.getCruiserCombatCount().toString(),
                25.00, Colors.RED, font
            ) {
                centerOn(enemyCruiserImage!!)
            }
        }

        if(enemyBattleShipCount > 0) {
            enemyBattleShipCounter = text(
                gs.stars[ps.activePlayerStar]!!.enemyFleet.getBattleShipCombatCount().toString(),
                25.00, Colors.RED, font
            ) {
                centerOn(enemyBattleShipImage!!)
            }
        }

        //Lazy way of removing ship images of ships that aren't at the battle
        //Just doing it lazy way is bad because you can other ships for tiny time slice and then they just disappear
        //it's grating.
        //updateScreen()
        val playerInitiativeRoll = Random.nextInt(1, 6) + playerInitiative
        val enemyRoll = Random.nextInt(1, 6) + enemyInitiative
        if(enemyRoll > playerInitiativeRoll) {
            resolveEnemyFire()
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

        if(gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipTypeClicked) ) {
            playerShipHighLight?.removeFromParent()

            playerShipTypeChosen = shipTypeClicked

            playerShipHighLight = when (shipTypeClicked) {
                shipType.TERRAFORMATTER_HUMAN -> showHighlight(terraformerImage!!, Colors.GREEN)
                shipType.COLONY_HUMAN -> showHighlight(colonyShipImage!!, Colors.GREEN)
                shipType.CORVETTE_HUMAN -> showHighlight(corvetteImage!!, Colors.GREEN)
                shipType.CRUISER_HUMAN -> showHighlight(cruiserImage!!, Colors.GREEN)
                shipType.BATTLESHIP_HUMAN -> showHighlight(battleShipImage!!, Colors.GREEN)
                shipType.GALLEON_HUMAN -> showHighlight(galleonImage!!, Colors.GREEN)
                else -> null
            }
        } else {
            messageLine.text = "Already FIRED this round"
        }
    }


    private suspend fun clickonEnemyShip(shipTypeClicked: shipType) {
        enemyShipHighLight?.removeFromParent()

        enemyShipTypeChosen = shipTypeClicked

        enemyShipHighLight = when (shipTypeClicked) {
            shipType.COLONY_ENEMY -> showHighlight(enemyColonyShipImage!!, Colors.RED)
            shipType.CORVETTE_ENEMY -> showHighlight(enemyCorvetteImage!!, Colors.RED)
            shipType.CRUISER_ENEMY -> showHighlight(enemyCruiserImage!!, Colors.RED)
            shipType.BATTLESHIP_ENEMY -> showHighlight(enemyBattleShipImage!!, Colors.RED)
            shipType.GALLEON_ENEMY -> showHighlight(enemyGalleonImage!!, Colors.RED)
            else -> null
        }

        fire()
        getCounts()
        updateScreen()
        resolveEnemyFire()
        getCounts()
        updateScreen()
        checkForBattleOver()
        checkForNewRound()
    }

    private suspend fun fire() {
        if(playerShipTypeChosen != null) {
            val gunsFiring = gs.stars[ps.activePlayerStar]!!.playerFleet.getGunMountCount(playerShipTypeChosen!!)
            println("Number of guns: ${gunsFiring}")
            var hits = 0
            var totaldamage = 0
            if(enemyShipTypeChosen != null) {
                for (i in 1..gunsFiring) {
                    val roll = Random.nextInt(1, 10)
                    println("Rolled: ${roll}")
                    val adjustedRoll = roll - enemyEvasion + playerAccuray
                    if(adjustedRoll > 5) {
                        hits++
                        var damageRolled = Random.nextInt(playerLowDamage, playerHighDamage) - enemySoak
                        if(damageRolled <= 0 ) {
                            damageRolled = 1
                        }
                        totaldamage += damageRolled
                        println("Rolled damage: ${damageRolled}")
                        val destroyed = gs.stars[ps.activePlayerStar]!!.enemyFleet.damageShip(enemyShipTypeChosen!!, damageRolled)
                        if(destroyed) {
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

    private suspend fun resolveEnemyFire() {
        println("Resolving enemy FIRE")
        val firingShips = cp.getShipsToFire()
        val targetShips = cp.getShipsToFireOn()
        if (firingShips != null && targetShips != null) {
            val gunsFiring = gs.stars[ps.activePlayerStar]!!.enemyFleet.getGunMountCount(firingShips!!)
            println("Number of guns: ${gunsFiring}")
            var hits = 0
            var totaldamage = 0
            for (i in 1..gunsFiring) {
                val roll = Random.nextInt(1, 10)
                println("Rolled: ${roll}")
                val adjustedRoll = roll + enemyAccuracy - playerEvasion
                if (adjustedRoll > 5) {
                    hits++
                    val damageRolled = Random.nextInt(enemyLowDamage, enemyHighDamage)
                    totaldamage += (damageRolled - playerSoak)
                    if(totaldamage <= 0) {
                        totaldamage = 1
                    }
                    println("Rolled damage: ${damageRolled}")
                    val destroyed = gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(targetShips!!, damageRolled)
                    if(destroyed) {
                        ps.shipsLost++
                        val countRemaining = when(targetShips) {
                            shipType.TERRAFORMATTER_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformerCombatCount()
                            shipType.COLONY_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyCombatCount()
                            shipType.CORVETTE_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount()
                            shipType.CRUISER_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount()
                            shipType.BATTLESHIP_HUMAN -> gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount()
                            shipType.GALLEON_HUMAN ->gs.stars[ps.activePlayerStar]!!.playerFleet.getGalleonCombatCount()
                            //Enemy shouldn't fire on itself so this kinda silly
                            shipType.COLONY_ENEMY -> 0
                            shipType.CORVETTE_ENEMY ->0
                            shipType.CRUISER_ENEMY -> 0
                            shipType.BATTLESHIP_ENEMY -> 0
                            shipType.GALLEON_ENEMY -> 0
                        }
                        //If we destroyed the last ship, then don't evaluate more damage
                        if(countRemaining == 0) {
                            break
                        }
                    }
                }
            }
            ps.totalDamgeReceived = totaldamage
           // need to mark that the ship type has fired
            gs.stars[ps.activePlayerStar]!!.enemyFleet.setFiredGuns(firingShips!!)
            messageLine2.text =
                "Enemy fired ${enemyWeapon} ${gunsFiring} times ${hits} hits doing ${totaldamage} damage"
            println("enemy fired  ${enemyWeapon} ${gunsFiring} times ${hits} hits doing ${totaldamage} damage")
        }
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

        //Remove images and counter if ships have blown up (or retreated, if we add that option)
        if(playerTerrafomers == 0 && terraformerImage != null) {
            terraformerImage!!.removeFromParent()
            terraformerCounter?.removeFromParent()
            terraformerImage = null
        }
        if(colonyShipCount == 0 && colonyShipImage != null) {
            colonyShipImage!!.removeFromParent()
            colonyShipCounter?.removeFromParent()
            colonyShipImage = null
        }
        if(corvetteCount == 0 && corvetteImage != null) {
            corvetteImage!!.removeFromParent()
            corvetteCounter?.removeFromParent()
            corvetteImage = null
        }
        if(cruiserCount == 0 && cruiserImage != null) {
            cruiserImage!!.removeFromParent()
            cruiserCounter?.removeFromParent()
            cruiserImage = null
        }
        if(battleShipCount == 0 && battleShipImage != null) {
            battleShipImage!!.removeFromParent()
            battleShipCounter?.removeFromParent()
            battleShipImage = null
        }
        if(galleonCount == 0 && galleonImage != null) {
            galleonImage!!.removeFromParent()
            galleonCounter?.removeFromParent()
            galleonImage = null
        }

        //Renove enemy images and counters if those have been destroyed
        if(enemyColonyShipCount == 0 && enemyColonyShipImage != null) {
            enemyColonyShipImage!!.removeFromParent()
            enemyColonyShipCounter?.removeFromParent()
            enemyColonyShipImage = null
        }
        if(enemyCorvetteCount == 0 && enemyCorvetteImage != null) {
            enemyCorvetteImage!!.removeFromParent()
            enemyCorvetteCounter?.removeFromParent()
            enemyCorvetteImage = null
        }
        if(enemyCruiserCount == 0 && enemyCruiserImage != null) {
            enemyCruiserImage!!.removeFromParent()
            enemyCruiserCounter?.removeFromParent()
            enemyCruiserImage = null
        }
        if(enemyBattleShipCount == 0 && enemyBattleShipImage != null) {
            enemyBattleShipImage!!.removeFromParent()
            enemyBattleShipCounter?.removeFromParent()
            enemyBattleShipImage = null
        }
        if(enemyGalleonCount == 0 && enemyGalleonImage != null) {
            enemyGalleonImage!!.removeFromParent()
            enemyGalleonCounter?.removeFromParent()
            enemyGalleonImage = null
        }
    }

    private suspend fun checkForNewRound() {
        if(isRoundOver()) {
            round++
            gs.stars[ps.activePlayerStar]!!.playerFleet.nextCombatTurn()
            gs.stars[ps.activePlayerStar]!!.enemyFleet.nextCombatTurn()
            header.text = "Battle at ${gs.stars[ps.activePlayerStar]!!.name} Round: ${round}"
        }
    }

    private suspend fun checkForBattleOver() {
        ps.totalRounds = round
        if( gs.stars[ps.activePlayerStar]!!.enemyFleet.isPresent() && !gs.stars[ps.activePlayerStar]!!.playerFleet.isPresent()) {
            sceneContainer.changeTo<LoseFleetCombatScene>()
        } else if ( gs.stars[ps.activePlayerStar]!!.playerFleet.isPresent() && !gs.stars[ps.activePlayerStar]!!.enemyFleet.isPresent() ) {
            sceneContainer.changeTo<WinFleetCombatScene>()
        }
        //Otherwise continue
    }

    private suspend fun isRoundOver() : Boolean {
        //Start with the assumption that the round is over
        var roundOver = true

        //There's a new round if all ships still present have fired -- EXCEPT for terraformers which have no Gun Mounts
        if (colonyShipCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.COLONY_HUMAN) ) {
            roundOver = false
        } else if (corvetteCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.CORVETTE_HUMAN) ) {
            roundOver = false
        } else if (cruiserCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.CRUISER_HUMAN) ) {
            roundOver = false
        } else if (battleShipCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.BATTLESHIP_HUMAN) ) {
            roundOver = false
        } else if (galleonCount > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.canFire(shipType.GALLEON_HUMAN) ) {
            roundOver = false
        } else if (enemyColonyShipCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.COLONY_ENEMY) ) {
            roundOver = false
        } else if (enemyCorvetteCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.CORVETTE_ENEMY) ) {
            roundOver = false
        } else if (enemyCruiserCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.CRUISER_ENEMY) ) {
            roundOver = false
        } else if (enemyBattleShipCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.BATTLESHIP_ENEMY) ) {
            roundOver = false
        } else if (enemyGalleonCount > 0 && gs.stars[ps.activePlayerStar]!!.enemyFleet.canFire(shipType.GALLEON_ENEMY) ) {
            roundOver = false
        }

        return(roundOver)
    }

    suspend fun setupBonuses() {
        val bonusCalculator = BonusCalculator(es,techTree)
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
}

