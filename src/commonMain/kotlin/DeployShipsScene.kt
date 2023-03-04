import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class DeployShipsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    private lateinit var terraformerSlider: UISlider
    private lateinit var colonySlider: UISlider

    override suspend fun SContainer.sceneInit() {

        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        uiVerticalStack {
            padding = 20.0
            text("Deploy Forces", 50.00, Colors.CYAN, font)

            uiHorizontalStack {
                padding = 20.0
                image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }

                terraformerSlider = uiSlider {
                    min = 0.0
                    max = gs.stars[ps.activePlayerStar]!!.playerFleet.getTerraformersCount().toDouble()
                    step = 1.00
                    value = 0.00
                }
               image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
                colonySlider = uiSlider {
                    min = 0.0
                    max = gs.stars[ps.activePlayerStar]!!.playerFleet.getColonyShipCount().toDouble()
                    step = 1.00
                    value = 0.00
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
                        ps.chosenTerraformers = terraformerSlider.value.toInt()
                        ps.chosenColony = colonySlider.value.toInt()
                        ps.operation = operationType.MOVINGFLEET
                        println("chosenTerraformers: ${ps.chosenTerraformers} chosenColony: ${ps.chosenColony}")
                        sceneContainer.changeTo<StarsScene>() }
                }
            }
        }
    }
}
