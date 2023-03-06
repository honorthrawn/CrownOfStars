import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class DeployShipsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    private lateinit var terraFormerReadout: Text
    private lateinit var colonyReadout: Text
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        uiVerticalStack {
            padding = 20.0
            uiHorizontalStack {
                 text("Deploy Forces", 50.00, Colors.CYAN, font)
            }
           uiHorizontalStack {
                text(" ADD ", 50.00, Colors.GOLD, font)
                {
                    onClick { onShipUp(shipType.TERRAFORMATTER_HUMAN) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onShipDown(shipType.TERRAFORMATTER_HUMAN) }
                }
                terraFormerReadout = text("Terraformers: 00", 50.00, Colors.CYAN, font)
                image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap())
               {
                   scale(0.5, 0.5)
               }
            }
            uiHorizontalStack {
                text(" ADD ", 50.00, Colors.GOLD, font)
                {
                    onClick { onShipUp(shipType.COLONY_HUMAN) }
                }
                text(" SUB ", 50.00, Colors.GOLD, font)
                {
                    onClick { onShipDown(shipType.COLONY_HUMAN) }
                }
                colonyReadout = text("Colony Ships: 00", 50.00, Colors.CYAN, font)
                image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }

            }
            uiHorizontalStack {
                padding = 20.0
                text("CLOSE", 50.00, Colors.GOLD, font)
                {
                    ps.operation = operationType.SELECTION
                    onClick { sceneContainer.changeTo<StarsScene>() }
                }

                text("MOVE", 50.00, Colors.GOLD, font)
                {
                    onClick {
                        ps.operation = operationType.MOVINGFLEET
                        println("chosenTerraformers: ${ps.chosenTerraformers} chosenColony: ${ps.chosenColony}")
                        sceneContainer.changeTo<StarsScene>()
                    }
                }
            }
        }
        updateReadouts()
    }

    private suspend fun onShipUp(type: shipType) {
        when(type)
        {
            shipType.TERRAFORMATTER_HUMAN ->
                if(ps.chosenTerraformers < gs.stars[ps.activePlayerStar]?.playerFleet?.getTerraformersCount()!!)
                {
                    ps.chosenTerraformers++
                }
            shipType.COLONY_HUMAN ->
                if(ps.chosenColony < gs.stars[ps.activePlayerStar]?.playerFleet?.getColonyShipCount()!!)
                {
                    ps.chosenColony++
                }
        }
        updateReadouts()
    }

    private fun onShipDown(type: shipType) {
        when(type)
        {
            shipType.TERRAFORMATTER_HUMAN -> if (ps.chosenTerraformers > 0) {
                ps.chosenTerraformers--
            }
            shipType.COLONY_HUMAN -> if(ps.chosenColony > 0 ) {
                ps.chosenColony--
            }
        }
        updateReadouts()
    }

    private fun updateReadouts() {
        terraFormerReadout.text = "Terraformers: ${ps.chosenTerraformers}"
        colonyReadout.text = "Colony Ships: ${ps.chosenColony}"
    }
}
