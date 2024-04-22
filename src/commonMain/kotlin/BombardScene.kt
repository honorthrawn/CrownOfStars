
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlin.random.*

class BombardScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {

    private lateinit var bombMessage: String
    private lateinit var basesMessage: String
    private lateinit var popsMessage: String
    private lateinit var missileLaunchedMessage: String
    private lateinit var corrvettesLostMessage: String
    private lateinit var cruisersLostMessage: String
    private lateinit var battleShipsLostMessage: String
    private lateinit var colonyStatus: String

    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["ui/planetBombed.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val topLine = "Bombardment of ${gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.name}"
        val playerInitiative = Random.nextInt(1, 6)
        val aiInitiative = Random.nextInt(1, 6)

        var intiativeMessage: String
        if (playerInitiative > aiInitiative) {
            intiativeMessage = "Our glorious fleet surprised the enemy scum"
            resolveBombs()
            resolveMissileLaunch()
        } else {
            intiativeMessage = "The enemy fiends saw our fleet coming"
            resolveMissileLaunch()
            resolveBombs()
        }

        uiVerticalStack {
            scaledWidth = sceneWidth.toDouble()
            scaledHeight = sceneHeight.toDouble()

            text(topLine, 20.00, Colors.CYAN, font)
            text(intiativeMessage, 20.00, Colors.CYAN, font)
            text(bombMessage, 20.00, Colors.CYAN, font)
            text(basesMessage, 20.00, Colors.CYAN, font)
            text(popsMessage, 20.00, Colors.CYAN, font)
            text(missileLaunchedMessage, 20.00, Colors.CYAN, font)
            text(corrvettesLostMessage, 20.00, Colors.CYAN, font)
            text(cruisersLostMessage, 20.00, Colors.CYAN, font)
            text(battleShipsLostMessage, 20.00, Colors.CYAN, font)
            text(colonyStatus, 20.00, Colors.CYAN, font)

            uiButton("CLOSE") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<PlanetsScene>() }
            }
        }
    }

    //TODO: refactor this and allow the AI player to bombard human colonies.
    private fun resolveBombs() {
        val bombRacks = gs.stars[ps.activePlayerStar]!!.playerFleet.getBombRackCount()
        var hits = 0
        for (i in 1..bombRacks) {
            val roll = Random.nextInt(1, 8)
            if (roll >= 7) {
                hits++
            }
        }
        gs.stars[ps.activePlayerStar]!!.playerFleet.setBombarded()
        bombMessage = "We dropped $bombRacks bombs and did damage $hits times"
        var lostBases = 0
        var lostPops = 0
        val startingBases = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases
        val startingPops = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getTotalPopulation()
        if (hits > 0) {
            var hitsToAsses = hits
            while (gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases > 0u && hitsToAsses > 0) {
                gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases--
                hitsToAsses--
                lostBases++
            }
            while (hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.decreasePopulation()) {
                hitsToAsses--
                lostPops++
            }
        }
        basesMessage = "We destroyed $lostBases of $startingBases bases"
        popsMessage = "We killed $lostPops of $startingPops population"
        if(gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.getTotalPopulation() == 0u) {
            colonyStatus = "The enemy colony has been eliminated"
        } else {
            colonyStatus = "This colony survived this onslaught"
        }
    }

    private fun resolveMissileLaunch() {
        val missileTubes = gs.stars[ps.activePlayerStar]!!.planets[ps.bombardIndex]!!.defenseBases.toInt() * 5
        var hits = 0
        for (i in 1..missileTubes) {
            val roll = Random.nextInt(1, 8)
            if (roll >= 7) {
                hits++
            }
        }
        missileLaunchedMessage = "They launched $missileTubes missiles and did damage $hits times"
        var corvettesLost = 0
        var totalCorvettes = gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount()
        var cruisersLost = 0
        var totalCruisers = gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount()
        var battleShipsLost = 0
        var totalBattleShips = gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount()

        if (hits > 0) {
            var hitsToAsses = hits
            while (hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteCombatCount() > 0) {
                hitsToAsses--
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.CORVETTE_HUMAN, 20)) {
                    corvettesLost++
                }
            }
            while(hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserCombatCount() > 0 ) {
                hitsToAsses--
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.CRUISER_HUMAN, 20)) {
                    cruisersLost++
                }
            }
            while(hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipCombatCount() > 0 ) {
                hitsToAsses--
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.BATTLESHIP_HUMAN, 20)) {
                    battleShipsLost++
                }
            }
        }
        corrvettesLostMessage = "We lost $corvettesLost of $totalCorvettes corvettes"
        cruisersLostMessage = "We lost $cruisersLost of $totalCruisers cruisers"
        battleShipsLostMessage = "We lost $battleShipsLost of $totalBattleShips battleships"
    }
}
