import kotlinx.serialization.*

@Serializable
class Fleet {
   private var terraformers: MutableList<Ship> = mutableListOf()
   private var colonyShips: MutableList<Ship> = mutableListOf()
   private var corvettes: MutableList<Ship> = mutableListOf()
   private var cruisers: MutableList<Ship> = mutableListOf()
   private var battleships: MutableList<Ship> = mutableListOf()
   private var galleons: MutableList<Ship> = mutableListOf()

    fun add(shipToAdd: Ship) {
        when( shipToAdd.theType) {
            shipType.TERRAFORMATTER_HUMAN -> terraformers.add(shipToAdd)
            shipType.COLONY_HUMAN -> colonyShips.add(shipToAdd)
            shipType.CORVETTE_HUMAN -> corvettes.add(shipToAdd)
            shipType.CRUISER_HUMAN -> cruisers.add(shipToAdd)
            shipType.BATTLESHIP_HUMAN -> battleships.add(shipToAdd)
            shipType.GALLEON_HUMAN -> galleons.add(shipToAdd)

            shipType.COLONY_ENEMY -> colonyShips.add(shipToAdd)
            shipType.CORVETTE_ENEMY -> corvettes.add(shipToAdd)
            shipType.CRUISER_ENEMY -> cruisers.add(shipToAdd)
            shipType.BATTLESHIP_ENEMY -> battleships.add(shipToAdd)
            shipType.GALLEON_ENEMY -> galleons.add(shipToAdd)
        }
    }

    fun isPresent(): Boolean {
      return terraformers.isNotEmpty()  || colonyShips.isNotEmpty() || corvettes.isNotEmpty() ||
          cruisers.isNotEmpty() || battleships.isNotEmpty() || galleons.isNotEmpty()
    }

    fun isColonyPresent(): Boolean {
        return colonyShips.isNotEmpty()
    }

    fun isTerraformersPresent(): Boolean {
        return terraformers.isNotEmpty()
    }

    //Changed these functions to return count that hasn't moved already, that way neither player
    //nor AI will get to move the ships more than once per turn
    fun getColonyShipCount(): Int {
        return colonyShips.filterNot { it.hasMoved }.count()
    }

    fun getTerraformersCount(): Int {
        return terraformers.filterNot { it.hasMoved }.count()
    }

    fun getCorvetteCount() : Int {
        return corvettes.filterNot { it.hasMoved }.count()
    }

    fun getCruiserCount() : Int {
        return cruisers.filterNot { it.hasMoved }.count()
    }

    fun getBattleShipCount() : Int {
        return battleships.filterNot { it.hasMoved }.count()
    }

    fun getGalleonCount() : Int {
        return galleons.filterNot { it.hasMoved }.count()
    }

    fun removeShipFromFleetForMove(shipTypeToRemove: shipType) : Ship  {
        val ship: Ship
        when (shipTypeToRemove) {

            shipType.TERRAFORMATTER_HUMAN -> {
                println("Trying to remove terraformer")
                val indx = terraformers.indexOfFirst { !it.hasMoved }
                ship = terraformers[indx]
                terraformers.removeAt(indx)
            }

            shipType.COLONY_HUMAN -> {
                println("Trying to remove colony ship")
                val indx = colonyShips.indexOfFirst { !it.hasMoved }
                ship = colonyShips[indx]
                colonyShips.removeAt(indx)
            }

            shipType.CORVETTE_HUMAN -> {
                val indx = corvettes.indexOfFirst { !it.hasMoved }
                ship = corvettes[indx]
                corvettes.removeAt(indx)
            }

            shipType.CRUISER_HUMAN -> {
                val indx = cruisers.indexOfFirst { !it.hasMoved }
                ship = cruisers[indx]
                cruisers.removeAt(indx)
            }

            shipType.BATTLESHIP_HUMAN -> {
                val indx = battleships.indexOfFirst { !it.hasMoved }
                ship = battleships[indx]
                battleships.removeAt(indx)
            }

            shipType.GALLEON_HUMAN -> {
                val indx = galleons.indexOfFirst { !it.hasMoved }
                ship = galleons[indx]
                galleons.removeAt(indx)
            }

            shipType.COLONY_ENEMY -> {
                println("Trying to remove colony ship")
                val indx = colonyShips.indexOfFirst { !it.hasMoved }
                ship = colonyShips[indx]
                colonyShips.removeAt(indx)
            }

            shipType.CORVETTE_ENEMY -> {
                val indx = corvettes.indexOfFirst { !it.hasMoved }
                ship = corvettes[indx]
                corvettes.removeAt(indx)
            }

            shipType.CRUISER_ENEMY -> {
                val indx = cruisers.indexOfFirst { !it.hasMoved }
                ship = cruisers[indx]
                cruisers.removeAt(indx)
            }

            shipType.BATTLESHIP_ENEMY -> {
                val indx = battleships.indexOfFirst { !it.hasMoved }
                ship = battleships[indx]
                battleships.removeAt(indx)
            }

            shipType.GALLEON_ENEMY -> {
                val indx = galleons.indexOfFirst { !it.hasMoved }
                ship = galleons[indx]
                galleons.removeAt(indx)
            }
        }
        return ship
    }

    fun destroyShip(shipTypeToRemove: shipType) {
        when (shipTypeToRemove) {
            shipType.TERRAFORMATTER_HUMAN -> {
                println("Trying to remove terraformer")
                terraformers.removeAt(0)
            }

            shipType.COLONY_HUMAN -> {
                println("Trying to remove colony ship")
                colonyShips.removeAt(0)
            }

            shipType.CORVETTE_HUMAN -> {
                corvettes.removeAt(0)
            }

            shipType.CRUISER_HUMAN -> {
                cruisers.removeAt(0)
            }

            shipType.BATTLESHIP_HUMAN -> {
                battleships.removeAt(0)
            }

            shipType.GALLEON_HUMAN -> {
                galleons.removeAt(0)
            }

            shipType.COLONY_ENEMY -> {
                println("Trying to remove colony ship")
                colonyShips.removeAt(0)

            }
            shipType.CORVETTE_ENEMY -> {
                corvettes.removeAt(0)
            }

            shipType.CRUISER_ENEMY -> {
                cruisers.removeAt(0)
            }

            shipType.BATTLESHIP_ENEMY -> {
                battleships.removeAt(0)
            }

            shipType.GALLEON_ENEMY -> {
                galleons.removeAt(0)
            }
        }
    }

    fun nextTurn() {
       /* for (former in terraformers) {
            former.hasMoved = false
        }
        for (colony in colonyShips) {
            colony.hasMoved = false
        }*/
        terraformers.map { it.hasMoved = false }
        colonyShips.map { it.hasMoved = false }
        corvettes.map { it.hasMoved = false }
        cruisers.map { it.hasMoved = false }
        battleships.map { it.hasMoved = false }
        galleons.map { it.hasMoved = false }
    }
}
