
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ViewShipsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    //private lateinit var terraFormerReadout: Text
    private lateinit var colonyReadout: Text
    private lateinit var corvetteReadout: Text
    private lateinit var cruiserReadout: Text
    private lateinit var battleshipReadout: Text
    private lateinit var galleonReadout: Text
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()
        uiVerticalStack {
            padding = 20.0
            uiHorizontalStack {
                text("View Forces", 50.00, Colors.RED, gameFont)
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
                colonyReadout = text("Colony: ${enemyFleet.getColonyShipTotalCount()}", 25.00, Colors.RED, gameFont)
                image(resourcesVfs["ships/enemy_colony_ship.png"].readBitmap()) {
                   size(96, 96)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                corvetteReadout = text("Corvettes: ${enemyFleet.getCorvetteTotalCount()}", 25.00, Colors.RED, gameFont)
                image(resourcesVfs["ships/enemy_corvette.png"].readBitmap())  {
                    size(96, 96)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                cruiserReadout = text("Cruisers: ${enemyFleet.getCruiserTotalCount()}", 25.00, Colors.RED, gameFont)
                  image(resourcesVfs["ships/enemy_cruiser.png"].readBitmap()) {
                     size(96, 96)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                battleshipReadout = text("Battleships: ${enemyFleet.getBattleShipTotalCount()}", 25.00, Colors.RED, gameFont)
                image(resourcesVfs["ships/enemy_battleship.png"].readBitmap()) {
                     size(96, 96)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                galleonReadout = text("Galleons: ${enemyFleet.getGalleonTotalCount()}", 25.00, Colors.RED, gameFont)
                image(resourcesVfs["ships/enemy_galleon.png"].readBitmap())  {
                   size(96, 96)
                }
            }

            uiHorizontalStack {
                padding = 20.0
                uiButton("CLOSE") {
                    textColor =  Colors.GOLD
                    textFont = gameFont
                    onClick { sceneContainer.changeTo<StarsScene>() }
                }
            }
        }
    }
}
