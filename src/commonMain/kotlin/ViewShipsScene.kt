import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ViewShipsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    //private lateinit var terraFormerReadout: Text
    private lateinit var colonyReadout: Text
    private lateinit var corvetteReadout: Text
    private lateinit var cruiserReadout: Text
    private lateinit var battleshipReadout: Text
    private lateinit var galleonReadout: Text
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
        uiVerticalStack {
            padding = 20.0
            uiHorizontalStack {
                text("View Forces", 50.00, Colors.RED, font)
            }
            //TODO Enemy doesn't use terraformers, if they start, need to change this
            //uiHorizontalStack {
            //    padding = 5.00
            //    terraFormerReadout = text("Terraformers: ${ps.chosenTerraformers}", 25.00, Colors.CYAN, font)
            //    image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap()) {
            //        scale(0.5, 0.5)
            //    }
            // }
            val enemyFleet = gs.stars[ps.activePlayerStar]!!.enemyFleet
            uiHorizontalStack {
                padding = 5.00
                colonyReadout = text("Colony: ${enemyFleet.getColonyCombatCount()}", 25.00, Colors.RED, font)
                image(resourcesVfs["ships/Frigate.png"].readBitmap()) {
                   // scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                corvetteReadout = text("Corvettes: ${enemyFleet.getCorvetteCombatCount()}", 25.00, Colors.RED, font)
                image(resourcesVfs["ships/Corvette.png"].readBitmap())  {
                   // scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                cruiserReadout = text("Cruisers: ${enemyFleet.getCruiserCombatCount()}", 25.00, Colors.RED, font)
                  image(resourcesVfs["ships/Cruiser.png"].readBitmap()) {
                  //  scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                battleshipReadout = text("Battleships: ${enemyFleet.getBattleShipCombatCount()}", 25.00, Colors.RED, font)
                image(resourcesVfs["ships/Battleship.png"].readBitmap()) {
                 //   scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                galleonReadout = text("Galleons: ${enemyFleet.getGalleonCombatCount()}", 25.00, Colors.RED, font)
                image(resourcesVfs["ships/Destroyer.png"].readBitmap())  {
                //    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 20.0
                uiButton("CLOSE") {
                    textColor =  Colors.GOLD
                    textFont = font
                    onClick { sceneContainer.changeTo<StarsScene>() }
                }
            }
        }
    }

}
