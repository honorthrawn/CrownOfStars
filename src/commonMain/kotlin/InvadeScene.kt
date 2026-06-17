
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
    private lateinit var causultiesReadout: Text
    private lateinit var victoryReadout: Text
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()
        sceneContainer().changeTo<WarMusicScene>()
        ps.chosenGalleonForInvasion = 0
        val topLine = "Invasion of ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.name}"
        uiVerticalStack {
            padding = 20.0

            text(topLine, 25.00, Colors.CYAN, gameFont)

            uiHorizontalStack {
                padding = 5.00
                galleonReadout = text("Galleons: ${ps.chosenGalleonForInvasion}", 25.00, Colors.CYAN, gameFont)
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
                    scale(0.5, 0.5)
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

            //Don't ask me why but gotta put spaces or something in there or it won't get drawn
            missileLaunchedReadout = text("           ", 25.00, Colors.CYAN, gameFont)
            galleonsRemainingReadout = text("          ", 25.00, Colors.CYAN, gameFont)
            causultiesReadout = text("           ", 25.00, Colors.CYAN, gameFont)
            victoryReadout = text("          ", 25.00, Colors.CYAN, gameFont)
        }
    }

    private fun onShipUp() {
        val fleet = gs.stars[ps.activePlayerStar]?.playerFleet ?: return

        if (ps.chosenGalleonForInvasion < fleet.getGalleonTotalCount()) {
            ps.chosenGalleonForInvasion++
        }

        galleonReadout.text = "Galleons: ${ps.chosenGalleonForInvasion}"
    }

    private fun onShipDown() {
        if (ps.chosenGalleonForInvasion > 0) {
            ps.chosenGalleonForInvasion--
        }
        galleonReadout.text = "Galleons: ${ps.chosenGalleonForInvasion}"
    }

    private suspend fun landTroops() {
        //First make sure some have been selected to land
        if (ps.chosenGalleonForInvasion > 0) {
            //TODO: refactor this and create code so that AI player can invade.   Or decide AI doesn't do that
            //and just blows up the colony.
            //if there are defense bases left on the world, they have a chance to blow up the galleons
            val missileTubes = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases.toInt() * 5
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
                    if (gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.GALLEON_HUMAN, 20)) {
                        galleonsLost++
                        galleonsLanding--
                    }
                }
            }
            val galleonsRemainingMessage = "We have lost $galleonsLost galleons out of $galleonsLanding from bases"
            galleonsRemainingReadout.text = galleonsRemainingMessage

            var troopsLost = 0
            var popsLost = 0
            while (galleonsLanding > 0
                && gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getTotalPopulation() > 0u
            ) {
                val attackerRoll = Random.nextInt(1, 20)
                val defenderRoll = Random.nextInt(1, 20)
                if (attackerRoll > defenderRoll) {
                    gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.decreasePopulation()
                    popsLost++
                } else {
                    galleonsLanding--
                    troopsLost++
                    gs.stars[ps.activePlayerStar]!!.playerFleet.destroyShip(shipType.GALLEON_HUMAN)
                }
            }
            val causultiesMessage = "We lost $troopsLost galleons fighting, they lost $popsLost population"
            causultiesReadout.text = causultiesMessage

            //attacker won
            var victoryMessage = "This world defeated our landing force"
            if (gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getTotalPopulation() == 0u) {
                gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.ownerIndex = Allegiance.Player
                gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.farmers = galleonsLanding.toUInt()
                victoryMessage = "This world is now ours"
            }
            victoryReadout.text = victoryMessage

            //Destroy the galleon landed, it is used up
            while (galleonsLanding > 0) {
                galleonsLanding--
                gs.stars[ps.activePlayerStar]!!.playerFleet.destroyShip(shipType.GALLEON_HUMAN)
            }

            //Final adjustments, don't let people try to land more than there are left
            ps.chosenGalleonForInvasion = 0
            galleonReadout.text = "Galleons: ${ps.chosenGalleonForInvasion}"
        } else {
            showNoGo("Must have at least one galleon left to invade")
        }
    }
}
