
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
    private lateinit var corvetteReadout: Text
    private lateinit var cruiserReadout: Text
    private lateinit var battleshipReadout: Text
    private lateinit var galleonReadout: Text
    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap())
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
               padding = 5.00
               terraFormerReadout = text("Terraformers: ${ps.chosenTerraformers}", 25.00, Colors.CYAN, font)
               uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.TERRAFORMATTER_HUMAN) }
                }

               uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.TERRAFORMATTER_HUMAN) }
                }
               image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap())
               {
                   scale(0.5, 0.5)
               }
            }
            uiHorizontalStack {
                padding = 5.00
                colonyReadout = text("Colony: ${ps.chosenColony}", 25.00, Colors.CYAN, font)
                uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.COLONY_HUMAN) }
                }
                uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.COLONY_HUMAN) }
                }
                image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                corvetteReadout = text("Corvettes: ${ps.chosenCorvette}", 25.00, Colors.CYAN, font)
                uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.CORVETTE_HUMAN) }
                }
                uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.CORVETTE_HUMAN) }
                }
                image(resourcesVfs["ships/Human-Corvette.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                cruiserReadout = text("Cruisers: ${ps.chosenCruiser}", 25.00, Colors.CYAN, font)
                uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.CRUISER_HUMAN) }
                }
                uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.CRUISER_HUMAN) }
                }
                image(resourcesVfs["ships/Human-Cruiser.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                battleshipReadout = text("Battleships: ${ps.chosenBattleship}", 25.00, Colors.CYAN, font)
                uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.BATTLESHIP_HUMAN) }
                }
                uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.BATTLESHIP_HUMAN) }
                }
                image(resourcesVfs["ships/Human-Battleship.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 5.00
                galleonReadout = text("Galleons: ${ps.chosenGalleon}", 25.00, Colors.CYAN, font)
                uiButton("ADD")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipUp(shipType.GALLEON_HUMAN) }
                }
                uiButton("SUB")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { onShipDown(shipType.GALLEON_HUMAN) }
                }
                image(resourcesVfs["ships/Human-Frigate.png"].readBitmap())
                {
                    scale(0.5, 0.5)
                }
            }

            uiHorizontalStack {
                padding = 20.0

                uiButton("CLOSE")
                {
                    textColor =  Colors.GOLD
                    textFont = font
                    onClick {  ps.reset(); sceneContainer.changeTo<StarsScene>() }
                }

                uiButton("MOVE")
                {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick {
                        ps.operation = operationType.MOVINGFLEET
                        println("chosenTerraformers: ${ps.chosenTerraformers} chosenColony: ${ps.chosenColony}")
                        sceneContainer.changeTo<StarsScene>()
                    }
                }
            }
        }
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
            shipType.CORVETTE_HUMAN ->
                if(ps.chosenCorvette < gs.stars[ps.activePlayerStar]?.playerFleet?.getCorvetteCount()!!)
            {
                ps.chosenCorvette++
            }
            shipType.CRUISER_HUMAN -> {
                if(ps.chosenCruiser < gs.stars[ps.activePlayerStar]?.playerFleet?.getCruiserCount()!!)
                {
                    ps.chosenCruiser++
                }
            }
            shipType.BATTLESHIP_HUMAN -> {
                if(ps.chosenBattleship < gs.stars[ps.activePlayerStar]?.playerFleet?.getBattleShipCount()!!)
                {
                    ps.chosenBattleship++
                }
            }
            shipType.GALLEON_HUMAN -> {
                if(ps.chosenGalleon < gs.stars[ps.activePlayerStar]?.playerFleet?.getGalleonCount()!!)
                {
                    ps.chosenGalleon++
                }
            }
        }
        updateReadouts()
    }

    private suspend fun onShipDown(type: shipType) {
        when(type)
        {
            shipType.TERRAFORMATTER_HUMAN ->
            {
                if (ps.chosenTerraformers > 0)
                ps.chosenTerraformers--
            }
            shipType.COLONY_HUMAN ->
            {
                if (ps.chosenColony > 0)
                    ps.chosenColony--
            }
            shipType.CORVETTE_HUMAN -> {
                if (ps.chosenCorvette > 0)
                    ps.chosenCorvette--
            }
            shipType.CRUISER_HUMAN -> {
                if (ps.chosenCruiser > 0)
                    ps.chosenCruiser--
            }
            shipType.BATTLESHIP_HUMAN -> {
                if (ps.chosenBattleship > 0)
                    ps.chosenBattleship--}
            shipType.GALLEON_HUMAN -> {
                if (ps.chosenGalleon > 0)
                ps.chosenGalleon--
            }
        }
        updateReadouts()
    }

    private suspend fun updateReadouts() {
        terraFormerReadout.text = "Terraformers: ${ps.chosenTerraformers}"
        colonyReadout.text = "Colony: ${ps.chosenColony}"
        corvetteReadout.text = "Corvettes: ${ps.chosenCorvette}"
        cruiserReadout.text =  "Cruisers: ${ps.chosenCruiser}"
        battleshipReadout.text = "Battleships: ${ps.chosenBattleship}"
        galleonReadout.text = "Galleons: ${ps.chosenGalleon}"
    }
}
