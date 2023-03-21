import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class StarsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text
    private lateinit var scienceReadout: Text
    private lateinit var defenseReadout: Text
    private var friendlyFleets = arrayListOf<Image>()
    private var enemyFleets = arrayListOf<Image>()

    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val yellowStar = resourcesVfs["stars/Star cK gK eg9.bmp"].readBitmap()
        val blueStar = resourcesVfs["stars/Star B supeg5.bmp"].readBitmap()
        val redStar = resourcesVfs["stars/Star M supeg5.bmp"].readBitmap()
        val fleet = resourcesVfs["ui/med-head-scout.png"].readBitmap()

        val cellSize = views.virtualWidth / 10.0
        val cellHeight = views.virtualHeight / 10.0
        var x: Double
        var y = 0.00
        var nI = 0
        for (i in 0..3) {
            x = 0.00
            for (j in 0..9) {
                val rect = roundRect(cellSize, cellHeight, 5.0, 5.0, Colors.BLACK, Colors.WHITE, 5.00)
                {
                    position(x, y)
                    onClick { clickedSector(i, j) } //for some weird reason trying to use nI always results in 40
                }

                uiVerticalStack(cellSize, UI_DEFAULT_PADDING, false) {
                    centerXOn(rect)
                    alignTopToTopOf(rect)
                    scaledHeight = cellHeight
                    scaledWidth = cellSize
                    uiHorizontalStack {
                        alignTopToTopOf(rect)
                        val fleetImage = image(fleet)
                        {
                            colorMul = Colors.CYAN
                            onClick { clickedFleet(i, j) }
                            visible = gs.stars[nI]!!.playerFleet.isPresent()
                        }
                        friendlyFleets.add(fleetImage)
                        val enemyfleetImage = image(fleet)
                        {
                            colorMul = Colors.RED
                            onClick { clickedEnemyFleet(i, j) }
                            visible = gs.stars[nI]!!.enemyFleet.isPresent()
                        }
                        enemyFleets.add(enemyfleetImage)
                    }

                    val textColor = when (gs.stars[nI]!!.getAllegiance()) {
                        Allegiance.Unoccupied -> Colors.WHITE
                        Allegiance.Player -> Colors.CYAN
                        Allegiance.Enemy -> Colors.RED
                    }
                    text(gs.stars[nI]!!.name, 11.00, textColor, font)
                    //Trying to put star first pushed the text and the fleet down to bottom of screen
                    // outside of rect don't understand
                    var starImage = image(
                        when (gs.stars[nI]!!.type) {
                            StarType.YELLOW -> yellowStar
                            StarType.BLUE -> blueStar
                            StarType.RED -> redStar
                        }
                    )
                    {
                        scaledWidth = 30.0
                        scaledHeight = 30.0
                    }
                }
                x += cellSize
                nI++
            }
            y += cellHeight
        }

        val Ship = "SHIP: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}  "
        val Research = "SCIENCE: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints}  "
        val Organic = "ORGANIC: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}  "
        val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints}  "

        uiVerticalStack {
            position(0.00, y + cellHeight)
            uiHorizontalStack {
                padding = 10.00
                shipsReadout = text(Ship, 50.00, Colors.CYAN, font)
                farmerReadout = text(Organic, 50.00, Colors.CYAN, font)
            }
            uiHorizontalStack {
                padding = 10.00
                scienceReadout = text(Research, 50.00, Colors.CYAN, font)
                defenseReadout = text(defense, 50.00, Colors.CYAN, font)
            }
        }

        uiButton("NEXT TURN")
        {
            position(0.00, y + 2 * cellHeight)
            textColor = Colors.GOLD
            textFont = font
            onClick { nextTurn() }
        }
    }

    private suspend fun nextTurn() {
        es.addProduction(gs)
        gs.nextTurn()
        updateScreen()
        es.save()
        gs.save()
    }

    private suspend fun updateScreen() {
        val Ship = "SHIP: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}"
        val Research = "SCIENCE: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints}"
        val Organic = "ORGANIC: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}"
        val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints}  "
        defenseReadout.text = defense
        shipsReadout.text = Ship
        farmerReadout.text = Organic
        scienceReadout.text = Research
        for (i in 0..gs.stars.count() - 1) {
            enemyFleets[i].visible = gs.stars[i]!!.enemyFleet.isPresent()
            friendlyFleets[i].visible = gs.stars[i]!!.playerFleet.isPresent()
            // println("FLEET HUMAN ${gs.stars[i]!!.playerFleet.isPresent()} ENEMY: ${gs.stars[i]!!.enemyFleet.isPresent()}")
        }
    }

    private suspend fun clickedSector(x: Int, y: Int) {
        when (ps.operation) {
            operationType.SELECTION -> {
                ps.activePlayerStar = x * 10 + y
                sceneContainer.changeTo<PlanetsScene>()
            }

            operationType.MOVINGFLEET -> {
                movechosenShips(x, y)
                ps.reset()
            }
        }
    }


    private suspend fun movechosenShips(x: Int, y: Int) {
        //Figure out destination star
        val destination = x * 10 + y
        //Check if can move there, todo

        //Remove the ships from the current star's fleet
        while (ps.chosenTerraformers > 0) {
            var shipMoving =
                gs.stars[ps.activePlayerStar]?.playerFleet?.removeShipFromFleet(shipType.TERRAFORMATTER_HUMAN)
            if (shipMoving != null) {
                gs.stars[destination]?.playerFleet?.add(shipMoving)
            }
            ps.chosenTerraformers--
        }
        while (ps.chosenColony > 0) {
            var shipMoving = gs.stars[ps.activePlayerStar]?.playerFleet?.removeShipFromFleet(shipType.COLONY_HUMAN)
            if (shipMoving != null) {
                gs.stars[destination]?.playerFleet?.add(shipMoving)
            }
            ps.chosenColony--
        }
        updateScreen()
    }

    private suspend fun clickedFleet(x: Int, y: Int) {
        println("we clicked a fleet")
        ps.activePlayerStar = x * 10 + y
        //Assume want to move whole fleet
        ps.chosenTerraformers = gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformersCount()
        ps.chosenColony = gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyShipCount()
        sceneContainer.changeTo<DeployShipsScene>()
    }

    private suspend fun clickedEnemyFleet(x: Int, y: Int) {
        println("we clicked an enemy fleet")
        ps.activePlayerStar = x * 10 + y
        sceneContainer.changeTo<DeployShipsScene>()
    }
}
