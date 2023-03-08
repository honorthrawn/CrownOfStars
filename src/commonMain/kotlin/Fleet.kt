import kotlinx.serialization.Serializable

@Serializable
class Fleet {
   private var terraformers: MutableList<Ship> = mutableListOf()
   private var colonyShips: MutableList<Ship> = mutableListOf()

    suspend fun add(shipToAdd: Ship)
    {
        when( shipToAdd.theType)
        {
            shipType.TERRAFORMATTER_HUMAN -> terraformers.add(shipToAdd)
            shipType.COLONY_HUMAN -> colonyShips.add(shipToAdd)
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
        }
        return ship
    }
}
