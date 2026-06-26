
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import kotlin.random.*

class BombardScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {

    private lateinit var bombMessage: String
    private lateinit var basesMessage: String
    private lateinit var popsMessage: String
    private lateinit var missileLaunchedMessage: String
    private lateinit var corrvettesLostMessage: String
    private lateinit var cruisersLostMessage: String
    private lateinit var battleShipsLostMessage: String
    private lateinit var colonyStatus: String

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addBackground("ui/planetBombed.jpg")
        ps.musicSceneContainer?.changeTo<WarMusicScene>()
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

           var yPos = 0.00
           val padding = 4.00
            text(topLine, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(intiativeMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(bombMessage, 20.00, Colors.CYAN, gameFont){
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(basesMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(popsMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(missileLaunchedMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(corrvettesLostMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(cruisersLostMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                centerXOnStage()
            }
            text(battleShipsLostMessage, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }
            text(colonyStatus, 20.00, Colors.CYAN, gameFont) {
                y = yPos;
                yPos += height
                yPos += padding
                centerXOnStage()
            }

            uiButton("CLOSE") {
                y = yPos;
                centerXOnStage()
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.musicSceneContainer?.changeTo<MusicScene>()
                    sceneContainer.changeTo<PlanetsScene>()
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
        ps.enemyPopulationKilled += lostPops
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
        var totalCorvettes = gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteTotalCount()
        var cruisersLost = 0
        var totalCruisers = gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserTotalCount()
        var battleShipsLost = 0
        var totalBattleShips = gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipTotalCount()

        if (hits > 0) {
            var hitsToAsses = hits
            while (hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getCorvetteTotalCount() > 0) {
                hitsToAsses--
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.CORVETTE_HUMAN, 20)) {
                    corvettesLost++
                }
            }
            while(hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getCruiserTotalCount() > 0 ) {
                hitsToAsses--
                //TODO: Fix this -- it appears like one missle from ground can take whole cruiser
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.CRUISER_HUMAN, 20)) {
                    cruisersLost++
                }
            }
            while(hitsToAsses > 0 && gs.stars[ps.activePlayerStar]!!.playerFleet.getBattleShipTotalCount() > 0 ) {
                hitsToAsses--
                //TODO: Fix this -- it appears like one missle from ground can take whole battleship
                if(gs.stars[ps.activePlayerStar]!!.playerFleet.damageShip(shipType.BATTLESHIP_HUMAN, 20)) {
                    battleShipsLost++
                }
            }
        }
        ps.totalShipsLost += corvettesLost + cruisersLost + battleShipsLost
        corrvettesLostMessage = "We lost $corvettesLost of $totalCorvettes corvettes"
        cruisersLostMessage = "We lost $cruisersLost of $totalCruisers cruisers"
        battleShipsLostMessage = "We lost $battleShipsLost of $totalBattleShips battleships"
    }
}
