
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlin.random.*

class InvadeScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    private lateinit var galleonReadout: Text
    private lateinit var missileLaunchedReadout: Text
    private lateinit var galleonsRemainingReadout: Text
    private lateinit var casualtiesReadout: Text
    private lateinit var victoryReadout: Text

    private val invasionPlanet: Planet
        get() = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!

    private val playerFleet: Fleet
        get() = gs.stars[ps.activePlayerStar]!!.playerFleet

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()
        sceneContainer().changeTo<WarMusicScene>()
        ps.chosenGalleonForInvasion = 0
        val planet = invasionPlanet
        val topLine = "Invasion of ${planet.name}"

        text(topLine, 32.00, Colors.CYAN, gameFont) {
            y = 20.0
            centerXOnStage()
        }

        image(resourcesVfs[planet.getLargestImagePath()].readBitmap()) {
            size(512, 512)
            centerXOnStage()
            y = sceneHeight - 550.00
        }

        uiVerticalStack {
            position(40.0, 100.0)
            padding = 20.0

            text("${planet.type} WORLD", 25.00, Colors.CYAN, gameFont)

            uiHorizontalStack {
                padding = 5.00
                galleonReadout = text(galleonReadoutText(), 25.00, Colors.CYAN, gameFont)
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp() }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown() }
                }
                image(resourcesVfs["ships/player_galleon.png"].readBitmap()) {
                    size(96, 96)
                }
            }

            uiButton("LAND TROOPS") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick { landTroops() }
            }

            uiButton("CLOSE") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.chosenGalleonForInvasion = 0;
                    ps.musicSceneContainer?.changeTo<MusicScene>()
                    sceneContainer.changeTo<PlanetsScene>() }
            }

            missileLaunchedReadout = text("Missiles: --", 25.00, Colors.CYAN, gameFont)
            galleonsRemainingReadout = text("Approach losses: --", 25.00, Colors.CYAN, gameFont)
            casualtiesReadout = text("Ground losses: --", 25.00, Colors.CYAN, gameFont)
            victoryReadout = text("Outcome: --", 25.00, Colors.CYAN, gameFont)
        }
    }

    private fun onShipUp() {
        if (ps.chosenGalleonForInvasion < playerFleet.getGalleonTotalCount()) {
            ps.chosenGalleonForInvasion++
        }

        updateGalleonReadout()
    }

    private fun onShipDown() {
        if (ps.chosenGalleonForInvasion > 0) {
            ps.chosenGalleonForInvasion--
        }
        updateGalleonReadout()
    }

    private fun galleonReadoutText(): String {
        return "Galleons: ${ps.chosenGalleonForInvasion} Total: ${playerFleet.getGalleonTotalCount()}"
    }

    private fun updateGalleonReadout() {
        galleonReadout.text = galleonReadoutText()
    }

    private suspend fun landTroops() {
        //First make sure some have been selected to land
        if (ps.chosenGalleonForInvasion > 0) {
            val startingGalleons = ps.chosenGalleonForInvasion
            //TODO: refactor this and create code so that AI player can invade.   Or decide AI doesn't do that
            //and just blows up the colony.
            //if there are defense bases left on the world, they have a chance to blow up the galleons
            val missileTubes = invasionPlanet.defenseBases.toInt() * 5
            var hits = 0
            for (i in 1..missileTubes) {
                val roll = Random.nextInt(1, 8)
                if (roll >= 7) {
                    hits++
                }
            }
            val missileLaunchedMessage = "They launched $missileTubes missiles and did damage $hits times"
            missileLaunchedReadout.text = missileLaunchedMessage

            var galleonsLost = 0
            var galleonsLanding = ps.chosenGalleonForInvasion
            if (hits > 0) {
                var hitsToAsses = hits
                while (hitsToAsses > 0 && galleonsLanding > 0) {
                    hitsToAsses--
                    if (playerFleet.damageShip(shipType.GALLEON_HUMAN, 20)) {
                        galleonsLost++
                        galleonsLanding--
                    }
                }
            }
            val galleonsRemainingMessage = "We lost $galleonsLost of $startingGalleons galleons to defense bases"
            galleonsRemainingReadout.text = galleonsRemainingMessage
            ps.totalShipsLost += galleonsLost
            ps.regimentsLost += galleonsLost

            var troopsLost = 0
            var popsLost = 0
            while (galleonsLanding > 0
                && invasionPlanet.getTotalPopulation() > 0u
            ) {
                val attackerRoll = Random.nextInt(1, 20)
                val defenderRoll = Random.nextInt(1, 20)
                if (attackerRoll > defenderRoll) {
                    invasionPlanet.decreasePopulation()
                    popsLost++
                } else {
                    galleonsLanding--
                    troopsLost++
                    playerFleet.destroyShip(shipType.GALLEON_HUMAN)
                }
            }
            val casualtiesMessage = "We lost $troopsLost galleons fighting, they lost $popsLost population"
            casualtiesReadout.text = casualtiesMessage
            ps.totalShipsLost += troopsLost
            ps.regimentsLost += troopsLost
            ps.enemyPopulationKilled += popsLost

            //attacker won
            var victoryMessage = "This world defeated our landing force"
            if (invasionPlanet.getTotalPopulation() == 0u) {
                invasionPlanet.ownerIndex = Allegiance.Player
                invasionPlanet.farmers = galleonsLanding.toUInt()
                victoryMessage = "This world is now ours"
            }
            victoryReadout.text = victoryMessage

            //Destroy the galleon landed, it is used up
            while (galleonsLanding > 0) {
                galleonsLanding--
                playerFleet.destroyShip(shipType.GALLEON_HUMAN)
            }

            //Final adjustments, don't let people try to land more than there are left
            ps.chosenGalleonForInvasion = 0
            updateGalleonReadout()
        } else {
            showNoGo("Must have at least one galleon left to invade")
        }
    }
}
