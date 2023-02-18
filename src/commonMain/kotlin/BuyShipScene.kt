import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class BuyShipScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val terraFormer = image(resourcesVfs[ "ships/Human-Spacestation.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToTopOf(background, 12.00)
            scale(0.5)
        }

            text("Terraformer\nOrganics 100 Ship 100", 50.00, Colors.CYAN, font)
            {
                alignLeftToRightOf(terraFormer)
                alignTopToTopOf(terraFormer)
            }

            text("BUY", 50.00, Colors.GOLD, font)
            {
               alignLeftToRightOf(terraFormer, 5.00)
               alignBottomToBottomOf(terraFormer)
               onClick { buyTerraformer() }
            }

        val colonyShip = image(resourcesVfs[ "ships/Human-Battlecruiser.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(terraFormer,12.00)
            scale(0.5)
        }
        text("Colony Ship\nOrganics 50 Ship 100", 50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(colonyShip)
            alignTopToTopOf(colonyShip)
        }

        text("BUY", 50.00, Colors.GOLD, font)
        {
            alignLeftToRightOf(colonyShip, 5.00)
            alignBottomToBottomOf(colonyShip)
            onClick { buyColony() }
        }
   }

    private suspend fun buyTerraformer()
    {

    }

    private suspend fun buyColony()
    {

    }
    }
