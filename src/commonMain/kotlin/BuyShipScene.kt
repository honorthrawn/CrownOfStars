
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class BuyShipScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {
    private lateinit var notEnoughDialog: RoundRect

    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val terraformerCosts = getCosts(shipType.TERRAFORMATTER_HUMAN)
        val terraFormer = image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToTopOf(background, 12.00)
            scale(0.5)
        }

            val line1 = text("Terraformer",50.00, Colors.CYAN, font)
            {
                alignLeftToRightOf(terraFormer)
                alignTopToTopOf(terraFormer)
            }

            val line2 = text("Organics ${terraformerCosts.organics} Ship ${terraformerCosts.metal}",50.00,
                Colors.CYAN, font)
            {
                alignLeftToRightOf(terraFormer)
                alignTopToBottomOf(line1)
            }

            uiButton("BUY")
            {
               textColor = Colors.GOLD
               textFont = font
               alignLeftToRightOf(terraFormer, 5.00)
               alignTopToBottomOf(line2)
               onClick { buy(shipType.TERRAFORMATTER_HUMAN) }
            }

        val colonyCosts = getCosts(shipType.COLONY_HUMAN)
        val colonyShip = image(resourcesVfs[ "ships/Human-Battlecruiser.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(terraFormer,12.00)
            scale(0.5)
        }
        val line3 = text("Colony", 50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(colonyShip)
            alignTopToTopOf(colonyShip)
        }
        val line4 = text("Organics ${colonyCosts.organics} Ship ${colonyCosts.metal}", 50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(colonyShip)
            alignTopToBottomOf(line3)
        }

        val buyColony = uiButton("BUY")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(colonyShip, 5.00)
            alignTopToBottomOf(line4)
            onClick { buy(shipType.COLONY_HUMAN) }
        }

        val corvetteCosts = getCosts(shipType.CORVETTE_HUMAN)
        val corvetteShip = image(resourcesVfs[ "ships/Human-Corvette.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(buyColony,12.00)
            scale(1, 1)
        }
        val line5 = text("Corvette",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(corvetteShip)
            alignTopToTopOf(corvetteShip)
        }
        val line6 = text("Organics ${corvetteCosts.organics} Ship ${corvetteCosts.metal}",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(corvetteShip)
            alignTopToBottomOf(line5)
        }
        val buyCorvette = uiButton("BUY")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(corvetteShip, 12.00)
            alignTopToBottomOf(line6)
            onClick { buy(shipType.CORVETTE_HUMAN)}
        }

        val cruiserCost = getCosts(shipType.CRUISER_HUMAN)
        val cruiserShip =  image(resourcesVfs[ "ships/Human-Cruiser.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(buyCorvette,12.00)
            scale(0.5)
        }
        val line7 = text("Cruiser",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(cruiserShip)
            alignTopToTopOf(cruiserShip)
        }
        val line8 = text("Organics ${cruiserCost.organics} Ship ${cruiserCost.metal}",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(cruiserShip)
            alignTopToBottomOf(line7)
        }
        val buyCruiser = uiButton("BUY")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(cruiserShip, 12.00)
            alignTopToBottomOf(line8)
            onClick { buy(shipType.CRUISER_HUMAN) }
        }

        val battleShipCost = getCosts(shipType.BATTLESHIP_HUMAN)
        val battleShip =  image(resourcesVfs[ "ships/Human-Battleship.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(buyCruiser,12.00)
            scale(0.5)
        }
        val line9 = text("Battleship",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(battleShip)
            alignTopToTopOf(battleShip)
        }
        val line10 = text("Organics ${battleShipCost.organics} Ship ${battleShipCost.metal}",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(battleShip)
            alignTopToBottomOf(line9)
        }
        val buyBattleShip = uiButton("BUY")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(battleShip, 12.00)
            alignTopToBottomOf(line10)
            onClick { buy(shipType.BATTLESHIP_HUMAN) }
        }

        val galleonCost = getCosts(shipType.BATTLESHIP_HUMAN)
        val galleon =  image(resourcesVfs[ "ships/Human-Frigate.png"].readBitmap())
        {
            alignLeftToLeftOf(background, 12.00)
            alignTopToBottomOf(buyBattleShip,12.00)
            scale(0.5)
        }
        val line11 = text("Galleon",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(galleon)
            alignTopToTopOf(galleon)
        }
        val line12 = text("Organics ${galleonCost.organics} Ship ${galleonCost.metal}",50.00, Colors.CYAN, font)
        {
            alignLeftToRightOf(galleon)
            alignTopToBottomOf(line11)
        }
        val buyhGalleon = uiButton("BUY")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(galleon, 12.00)
            alignTopToBottomOf(line12)
            onClick { buy(shipType.GALLEON_HUMAN) }
        }

        uiButton("BACK")
        {
            textColor = Colors.GOLD
            textFont = font
            alignLeftToRightOf(buyhGalleon, 5.00)
            alignBottomToBottomOf(buyhGalleon)
            onClick { sceneContainer.changeTo<PlanetScene>() }
        }
   }

    private suspend fun buy(shipType: shipType) {
        val costs = getCosts(shipType)
        if(es.empires[Allegiance.Player.ordinal]!!.buyShip(costs)) {
            var newShip = shipFactory(shipType)
            gs.stars[ps.activePlayerStar]!!.playerFleet.add(newShip)
        } else {
            showNotEnough("Organics ${costs.organics} Ship ${costs.metal}")
        }
    }


    private suspend fun showNotEnough(requirements: String) {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        notEnoughDialog =
            this.sceneContainer.container().roundRect(
                sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                Colors.BLACK
            )
            {
                centerOnStage()
                uiVerticalStack {
                    scaledWidth = sceneWidth / 2.00
                    text("Not enough resources", 50.00, Colors.CYAN, font)
                    {
                        autoScaling = true
                    }
                    text(requirements, 50.00, Colors.CYAN, font)
                    uiButton("CLOSE")
                    {
                        textColor = Colors.GOLD
                        textFont = font
                        onClick { closeMessage() }
                    }
                }
            }
    }

    private fun closeMessage()
    {
        notEnoughDialog.removeFromParent()
    }

    }
