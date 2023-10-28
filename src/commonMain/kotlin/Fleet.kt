import kotlinx.serialization.*

@Serializable
class Fleet {
   private var terraformers: MutableList<Ship> = mutableListOf()
   private var colonyShips: MutableList<Ship> = mutableListOf()
   private var corvettes: MutableList<Ship> = mutableListOf()
   private var cruisers: MutableList<Ship> = mutableListOf()
   private var battleships: MutableList<Ship> = mutableListOf()
   private var galleons: MutableList<Ship> = mutableListOf()
    suspend fun add(shipToAdd: Ship)
    {
        when( shipToAdd.theType)
        {
            shipType.TERRAFORMATTER_HUMAN -> terraformers.add(shipToAdd)
            shipType.COLONY_HUMAN -> colonyShips.add(shipToAdd)
            shipType.CORVETTE_HUMAN -> corvettes.add(shipToAdd)
            shipType.CRUISER_HUMAN -> cruisers.add(shipToAdd)
            shipType.BATTLESHIP_HUMAN -> battleships.add(shipToAdd)
            shipType.GALLEON -> galleons.add(shipToAdd)
        }
    }

    suspend fun isPresent(): Boolean
    {
      return terraformers.isNotEmpty()  || colonyShips.isNotEmpty()
    }

    suspend fun getColonyShipCount(): Int
    {
        return colonyShips.count()
    }

    suspend fun getTerraformersCount(): Int
    {
        return terraformers.count()
    }

    suspend fun removeShipFromFleet(shipTypeToRemove: shipType) : Ship
    {
        val ship: Ship
        when (shipTypeToRemove) {
            shipType.TERRAFORMATTER_HUMAN -> {
                println("Trying to remove terraformer")
                ship = terraformers.get(0)
                terraformers.removeAt(0)
            }

            shipType.COLONY_HUMAN -> {
                println("Trying to remove colony ship")
                ship = colonyShips.get(0)
                colonyShips.removeAt(0)

            }
            shipType.CORVETTE_HUMAN -> {
                ship = corvettes.get(0)
                corvettes.removeAt(0)
            }

            shipType.CRUISER_HUMAN -> {
                ship = cruisers.get(0)
                cruisers.removeAt(0)
            }

            shipType.BATTLESHIP_HUMAN -> {
                ship = battleships.get(0)
                battleships.removeAt(0)
            }

            shipType.GALLEON -> {
                ship = galleons.get(0)
                galleons.removeAt(0)
            }
        }
        return ship
    }
}
