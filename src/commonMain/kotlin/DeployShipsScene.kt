
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class DeployShipsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    private lateinit var terraFormerReadout: Text
    private lateinit var colonyReadout: Text
    private lateinit var corvetteReadout: Text
    private lateinit var cruiserReadout: Text
    private lateinit var battleshipReadout: Text
    private lateinit var galleonReadout: Text
    private lateinit var currentFleet: Fleet

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()
        currentFleet = gs.stars[ps.activePlayerStar]?.playerFleet!!

        uiVerticalStack {
            padding = 20.0
            uiHorizontalStack {
                 text("Deploy Forces", 50.00, Colors.CYAN, gameFont)
            }

            uiHorizontalStack {
                terraFormerReadout = text(
                    "Selected: ${ps.chosenTerraformers} Ready: ${currentFleet.getMovableTerraformersCount()} Total: ${currentFleet.getTerraformerTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

           uiHorizontalStack {
               padding = 5.00

               uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.TERRAFORMATTER_HUMAN) }
                }

               uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.TERRAFORMATTER_HUMAN) }
                }
                image(resourcesVfs["ships/player_terraformer.png"].readBitmap()) {
                     size(96,96)
               }
            }

            uiHorizontalStack {
                colonyReadout = text(
                    "Selected: ${ps.chosenColony} Ready: ${currentFleet.getMovableColonyShipCount()} Total: ${currentFleet.getColonyShipTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

            uiHorizontalStack {
                padding = 5.00

                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.COLONY_HUMAN) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.COLONY_HUMAN) }
                }
                image(resourcesVfs["ships/player_colony_ship.png"].readBitmap()) {
                    size(96,96)
                }
            }

            uiHorizontalStack {
                corvetteReadout = text(
                    "Selected: ${ps.chosenCorvette} Ready: ${currentFleet.getMovableCorvetteCount()} Total: ${currentFleet.getCorvetteTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

            uiHorizontalStack {
                padding = 5.00

                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.CORVETTE_HUMAN) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.CORVETTE_HUMAN) }
                }
                image(resourcesVfs["ships/player_corvette.png"].readBitmap())  {
                    size(96,96)
                }
            }

            uiHorizontalStack {
                cruiserReadout = text(
                    "Selected: ${ps.chosenCruiser} Ready: ${currentFleet.getMovableCruiserCount()} Total: ${currentFleet.getCruiserTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

            uiHorizontalStack {
                padding = 5.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.CRUISER_HUMAN) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.CRUISER_HUMAN) }
                }
                image(resourcesVfs["ships/player_cruiser.png"].readBitmap()) {
                    size(96,96)
                }
            }

            uiHorizontalStack {
                battleshipReadout = text(
                    "Selected: ${ps.chosenBattleship} Ready: ${currentFleet.getMovableBattleShipCount()} Total: ${currentFleet.getBattleShipTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

            uiHorizontalStack {
                padding = 5.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.BATTLESHIP_HUMAN) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.BATTLESHIP_HUMAN) }
                }
                image(resourcesVfs["ships/player_battleship.png"].readBitmap()) {
                    size(96,96)
                }
            }

            uiHorizontalStack {
                galleonReadout = text(
                    "Selected: ${ps.chosenGalleon} Ready: ${currentFleet.getMovableGalleonCount()} Total: ${currentFleet.getGalleonTotalCount()}",
                    50.00, Colors.CYAN, gameFont
                )
            }

            uiHorizontalStack {
                padding = 5.00
                uiButton("ADD") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipUp(shipType.GALLEON_HUMAN) }
                }
                uiButton("SUB") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { onShipDown(shipType.GALLEON_HUMAN) }
                }
                image(resourcesVfs["ships/player_galleon.png"].readBitmap())  {
                   size(96, 96)
                }
            }


            uiHorizontalStack {
                padding = 20.0

                uiButton("CLOSE") {
                    textColor =  Colors.GOLD
                    textFont = gameFont
                    onClick {  ps.reset(); sceneContainer.changeTo<StarsScene>() }
                }

                uiButton("MOVE") {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick {
                        if (totalSelectedShips() <= 0) {
                            showNoGo("Select at least one ship to move")
                            return@onClick
                        }
                        ps.operation = operationType.MOVINGFLEET
                        sceneContainer.changeTo<StarsScene>()
                    }
                }
            }
        }
    }

    private fun onShipUp(type: shipType) {
        when(type) {
            shipType.TERRAFORMATTER_HUMAN ->
                if(ps.chosenTerraformers < currentFleet.getMovableTerraformersCount()) {
                    ps.chosenTerraformers++
                }
            shipType.COLONY_HUMAN ->
                if(ps.chosenColony < currentFleet.getMovableColonyShipCount()) {
                    ps.chosenColony++
                }
            shipType.CORVETTE_HUMAN ->
                if(ps.chosenCorvette < currentFleet.getMovableCorvetteCount()) {
                ps.chosenCorvette++
            }
            shipType.CRUISER_HUMAN -> {
                if(ps.chosenCruiser < currentFleet.getMovableCruiserCount()) {
                    ps.chosenCruiser++
                }
            }
            shipType.BATTLESHIP_HUMAN -> {
                if(ps.chosenBattleship < currentFleet.getMovableBattleShipCount()) {
                    ps.chosenBattleship++
                }
            }
            shipType.GALLEON_HUMAN -> {
                if(ps.chosenGalleon < currentFleet.getMovableGalleonCount()) {
                    ps.chosenGalleon++
                }
            } else -> {
                //not going to be moving enemy ships
            }
        }
        updateReadouts()
    }

    private fun onShipDown(type: shipType) {
        when(type) {
            shipType.TERRAFORMATTER_HUMAN -> {
                if (ps.chosenTerraformers > 0)
                  ps.chosenTerraformers--
            }
            shipType.COLONY_HUMAN -> {
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
                    ps.chosenBattleship--
            }
            shipType.GALLEON_HUMAN -> {
                if (ps.chosenGalleon > 0)
                ps.chosenGalleon--
            } else -> {} //not going to be moving enemy ships
        }
        updateReadouts()
    }

    private fun updateReadouts() {
        terraFormerReadout.text =
            "Selected: ${ps.chosenTerraformers} Ready: ${currentFleet.getMovableTerraformersCount()} Total: ${currentFleet.getTerraformerTotalCount()}"

        colonyReadout.text =
            "Selected: ${ps.chosenColony} Ready: ${currentFleet.getMovableColonyShipCount()} Total: ${currentFleet.getColonyShipTotalCount()}"

        corvetteReadout.text =
            "Selected: ${ps.chosenCorvette} Ready: ${currentFleet.getMovableCorvetteCount()} Total: ${currentFleet.getCorvetteTotalCount()}"

        cruiserReadout.text =
            "Selected: ${ps.chosenCruiser} Ready: ${currentFleet.getMovableCruiserCount()} Total: ${currentFleet.getCruiserTotalCount()}"

        battleshipReadout.text =
            "Selected: ${ps.chosenBattleship} Ready: ${currentFleet.getMovableBattleShipCount()} Total: ${currentFleet.getBattleShipTotalCount()}"

        galleonReadout.text =
            "Selected: ${ps.chosenGalleon} Ready: ${currentFleet.getMovableGalleonCount()} Total: ${currentFleet.getGalleonTotalCount()}"
    }

    private fun totalSelectedShips(): Int {
        return ps.chosenTerraformers +
            ps.chosenColony +
            ps.chosenCorvette +
            ps.chosenCruiser +
            ps.chosenBattleship +
            ps.chosenGalleon
    }
}
