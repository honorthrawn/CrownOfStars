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

    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val star = resourcesVfs["ui/star1.png"].readBitmap()
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
                    onClick { clickedSector(i,j) } //for some weird reason trying to use nI always results in 40
                }

                uiVerticalStack(cellSize, UI_DEFAULT_PADDING, false) {
                    centerXOn(rect)
                    centerYOn(rect)
                    uiHorizontalStack {
                        val starImage = image(star)
                        when (gs.stars[nI]!!.type) {
                            StarType.YELLOW -> starImage.colorMul = Colors.YELLOW
                            StarType.BLUE -> starImage.colorMul = Colors.BLUE
                            StarType.RED -> starImage.colorMul = Colors.RED
                        }

                        if (gs.stars[nI]!!.playerFleet.isPresent()) {
                            val fleetImage = image(fleet)
                            {
                                colorMul = Colors.CYAN
                                onClick { clickedFleet(i, j) }
                            }
                        }
                       if (gs.stars[nI]!!.enemyFleet.isPresent()) {
                            val fleetImage = image(fleet)
                            {
                                colorMul = Colors.RED
                                onClick{ clickedEnemyFleet(i,j) }
                            }
                        }
                    }
                    val textColor = when(gs.stars[nI]!!.getAllegiance())
                    {
                        Allegiance.Unoccupied -> Colors.WHITE
                        Allegiance.Player -> Colors.CYAN
                        Allegiance.Enemy -> Colors.RED
                    }
                    text(gs.stars[nI]!!.name, 10.00, textColor, font)

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
           // anchor = this.koruiComponent.factory.
        }

        text("NEXT TURN", 50.00,Colors.GOLD, font)
        {
            position(0.00, y +  2 * cellHeight)
            onClick { nextTurn() }
        }
    }

    private suspend fun nextTurn()
    {
        es.addProduction(gs)
        updateScreen()
        es.save()
        gs.save()
    }

    private fun updateScreen()
    {
        val Ship = "SHIP: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}"
        val Research = "SCIENCE: ${es.empires[Allegiance.Player.ordinal]!!.researchPoints}"
        val Organic = "ORGANIC: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}"
        val defense = "DEFENSE: ${es.empires[Allegiance.Player.ordinal]!!.defensePoints}  "
        defenseReadout.text = defense
        shipsReadout.text = Ship
        farmerReadout.text = Organic
        scienceReadout.text = Research
    }

    private suspend fun clickedSector(x: Int, y: Int)
    {
        ps.activePlayerStar = x * 10 + y
        sceneContainer.changeTo<PlanetsScene>()
    }

    private suspend fun clickedFleet(x: Int, y: Int)
    {
        println("OBAMANATION, we clicked a fleet")
        ps.activePlayerStar = x * 10 + y
        sceneContainer.changeTo<DeployShipsScene>()
    }

    private suspend fun clickedEnemyFleet(x: Int, y: Int)
    {
        println("OBAMANATION, we clicked an enemy fleet")
        ps.activePlayerStar = x * 10 + y
        sceneContainer.changeTo<DeployShipsScene>()
    }
}
