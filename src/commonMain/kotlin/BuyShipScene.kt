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
        loadBasicAssets()
        addDefaultBackground()
        shipFactory.init()
        val playerEmpire = es.empires[Allegiance.Player.ordinal]!!

        shipsReadout = text(
            "SHIP: ${playerEmpire.shipPoints}",
            36.0,
            Colors.CYAN,
            gameFont
        ) {
            position(20, 10)
        }

        farmerReadout = text(
            "ORGANICS: ${playerEmpire.organicPoints}",
            36.0,
            Colors.CYAN,
            gameFont
        ) {
            position(300, 10)
        }

        uiButton("BACK") {
            textColor = Colors.GOLD
            textFont = gameFont
            position(sceneWidth - 220.0, 10.0)
            onClick { sceneContainer.changeTo<PlanetScene>() }
        }

        uiButton("MAP") {
            textColor = Colors.GOLD
            textFont = gameFont
            position(sceneWidth - 110.0, 10.0)
            onClick { sceneContainer.changeTo<StarsScene>() }
        }

        var y = 60.0
        val x = 30.0
        val gap = 10.0
        val slotHeight = 130.0

        val terraformerCosts = getCosts(shipType.TERRAFORMATTER_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_terraformer.png",
            gameFont,
            "Terraformer",
            terraformerCosts.organics,
            terraformerCosts.metal,
            true
        ) {
            buy(shipType.TERRAFORMATTER_HUMAN)
        }

        y += slotHeight + gap

        val colonyCosts = getCosts(shipType.COLONY_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_colony_ship.png",
            gameFont,
            "Colony Ship",
            colonyCosts.organics,
            colonyCosts.metal,
            true
        ) {
            buy(shipType.COLONY_HUMAN)
        }

        y += slotHeight + gap

        val corvetteCosts = getCosts(shipType.CORVETTE_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_corvette.png",
            gameFont,
            "Corvette",
            corvetteCosts.organics,
            corvetteCosts.metal,
            true
        ) {
            buy(shipType.CORVETTE_HUMAN)
        }

        y += slotHeight + gap

        val cruiserCosts = getCosts(shipType.CRUISER_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_cruiser.png",
            gameFont,
            "Cruiser",
            cruiserCosts.organics,
            cruiserCosts.metal,
            true
        ) {
            buy(shipType.CRUISER_HUMAN)
        }

        y += slotHeight + gap

        val battleshipCosts = getCosts(shipType.BATTLESHIP_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_battleship.png",
            gameFont,
            "Battleship",
            battleshipCosts.organics,
            battleshipCosts.metal,
            true
        ) {
            buy(shipType.BATTLESHIP_HUMAN)
        }

        y += slotHeight + gap

        val galleonCosts = getCosts(shipType.GALLEON_HUMAN)
        ShipSlot(
            x, y,
            "ships/player_galleon.png",
            gameFont,
            "Galleon",
            galleonCosts.organics,
            galleonCosts.metal,
            true
        ) {
            buy(shipType.GALLEON_HUMAN)
        }
    }

    private suspend fun Container.ShipSlot(
        x: Double,
        y: Double,
        path: String,
        font: Font,
        name: String,
        organics: UInt,
        metals: UInt,
        faceLeft: Boolean = false,
        onClickHandler: suspend () -> Unit
    ): Container {
        val slotWidth = sceneWidth - 60.0
        val slotHeight = 130.0

        val maxShipWidth = 110.0
        val maxShipHeight = 95.0

        val slot = container {
            position(x, y)

            // Keep visible while testing. Later maybe use a darker translucent panel.
            solidRect(slotWidth, slotHeight, Colors["#00102099"])
        }

        val bitmap = resourcesVfs[path].readBitmap()

        val scaleAmount = minOf(
            maxShipWidth / bitmap.width.toDouble(),
            maxShipHeight / bitmap.height.toDouble()
        )

        slot.image(bitmap) {
            anchor(0.5, 0.5)
            position(75.0, slotHeight / 2.0)

            scaleX = if (faceLeft) -scaleAmount else scaleAmount
            scaleY = scaleAmount
        }

        slot.text(name, 34.0, Colors.CYAN, font) {
            position(160.0, 22.0)
        }

        slot.text("Organics: $organics    Ship: $metals", 28.0, Colors.CYAN, font) {
            position(160.0, 70.0)
        }

        slot.uiButton("BUY") {
            textColor = Colors.GOLD
            textFont = font
            position(slotWidth - 130.0, 43.0)
            onClick { onClickHandler() }
        }

        return slot
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

