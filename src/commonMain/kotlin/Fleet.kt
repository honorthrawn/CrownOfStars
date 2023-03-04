import kotlinx.serialization.Serializable

@Serializable
class Fleet {
    private var terraformers = arrayListOf<Ship>()
    private val colonyShips = arrayListOf<Ship>()

    fun add(shipToAdd: Ship)
    {
        when( shipToAdd.theType)
        {
            shipType.TERRAFORMATTER_HUMAN -> terraformers.add(shipToAdd)
            shipType.COLONY_HUMAN -> colonyShips.add(shipToAdd)
        }
    }

    fun isPresent(): Boolean
    {
        return terraformers.isNotEmpty() || colonyShips.isNotEmpty()
    }
}
