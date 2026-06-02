import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class BuyShipScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    @Transient
    private var shipFactory = shipFactory()
    private lateinit var farmerReadout: Text
    private lateinit var shipsReadout: Text

    override suspend fun SContainer.sceneInit() {
        shipFactory.init()
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val terraformerCosts = getCosts(shipType.TERRAFORMATTER_HUMAN)
        val colonyCosts = getCosts(shipType.COLONY_HUMAN)
        val corvetteCosts = getCosts(shipType.CORVETTE_HUMAN)
        val cruiserCost = getCosts(shipType.CRUISER_HUMAN)
        val battleShipCost = getCosts(shipType.BATTLESHIP_HUMAN)
        val galleonCost = getCosts(shipType.GALLEON_HUMAN)

        uiVerticalStack {
            uiHorizontalStack {
                val terraFormer = image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }

                val line1 = text("Terraformer", 50.00, Colors.CYAN, font) {
                }
            }
            uiHorizontalStack {
                val line2 = text(
                    "Organics ${terraformerCosts.organics} Ship ${terraformerCosts.metal}", 50.00,
                    Colors.CYAN, font
                ) {
                }

                uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.TERRAFORMATTER_HUMAN) }
                }
            }

            uiHorizontalStack {
                val colonyShip = image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }
                val line3 = text("Colony", 50.00, Colors.CYAN, font) {
                }
            }

            uiHorizontalStack {
                val line4 =
                    text(
                        "Organics ${colonyCosts.organics} Ship ${colonyCosts.metal}",
                        50.00,
                        Colors.CYAN,
                        font
                    ) {
                    }

                val buyColony = uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.COLONY_HUMAN) }
                }
            }


            uiHorizontalStack {
                val corvetteShip = image(resourcesVfs["ships/Human-Corvette.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }
                val line5 = text("Corvette", 50.00, Colors.CYAN, font) {
                }
            }

            uiHorizontalStack {
                val line6 =
                    text(
                        "Organics ${corvetteCosts.organics} Ship ${corvetteCosts.metal}",
                        50.00,
                        Colors.CYAN,
                        font
                    ) {
                    }
                val buyCorvette = uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.CORVETTE_HUMAN) }
                }
            }

            uiHorizontalStack {
                val cruiserShip = image(resourcesVfs["ships/Human-Cruiser.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }
                val line7 = text("Cruiser", 50.00, Colors.CYAN, font) {
                }
            }

            uiHorizontalStack {
                val line8 =
                    text("Organics ${cruiserCost.organics} Ship ${cruiserCost.metal}", 50.00, Colors.CYAN, font) {
                    }
                val buyCruiser = uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.CRUISER_HUMAN) }
                }
            }

            uiHorizontalStack {
                val battleShip = image(resourcesVfs["ships/Human-Battleship.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }
                val line9 = text("Battleship", 50.00, Colors.CYAN, font) {
                }
            }

            uiHorizontalStack {
                val line10 =
                    text("Organics ${battleShipCost.organics} Ship ${battleShipCost.metal}", 50.00, Colors.CYAN, font) {
                    }
                val buyBattleShip = uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.BATTLESHIP_HUMAN) }
                }
            }

            uiHorizontalStack {
                val galleon = image(resourcesVfs["ships/Human-Frigate.png"].readBitmap()) {
                    setSizeScaled(50.0, 50.0)
                }
                val line11 = text("Galleon", 50.00, Colors.CYAN, font) {
                }
            }

            uiHorizontalStack {
                val line12 =
                    text("Organics ${galleonCost.organics} Ship ${galleonCost.metal}", 50.00, Colors.CYAN, font) {
                    }
                val buyGalleon = uiButton("BUY") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buy(shipType.GALLEON_HUMAN) }
                }
            }

            val Ship = "STORES SHIP: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}  "
            val Organic = "ORGANICS: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}  "

            uiHorizontalStack {
                padding = 10.00
                shipsReadout = text(Ship, 50.00, Colors.CYAN, font)
                farmerReadout = text(Organic, 50.00, Colors.CYAN, font)
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<PlanetScene>() }
            }

            uiButton("MAP") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>() }
            }
        }
    }

    private suspend fun buy(shipType: shipType) {
        val costs = getCosts(shipType)
        if (es.empires[Allegiance.Player.ordinal]!!.buyShip(costs)) {
            var newShip = shipFactory.getShip(shipType)
            gs.stars[ps.activePlayerStar]!!.playerFleet.add(newShip)
            updateScreen()
        } else {
            showNoGo("Requires at least Organics ${costs.organics} Ship ${costs.metal}")
        }
    }

    private fun updateScreen() {
        val Ship = "SHIP: ${es.empires[Allegiance.Player.ordinal]!!.shipPoints}"
        val Organic = "ORGANIC: ${es.empires[Allegiance.Player.ordinal]!!.organicPoints}"
        shipsReadout.text = Ship
        farmerReadout.text = Organic
    }
}

