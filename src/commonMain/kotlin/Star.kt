import kotlinx.serialization.Serializable
import kotlin.random.*

@Serializable
enum class StarType
{
    YELLOW,
    BLUE,
    RED
}

@Serializable
data class Star(val name: String)
{
    var type: StarType = StarType.YELLOW
    var planets = mutableMapOf<Int, Planet>()
    var terraformers: MutableList<Ship> = mutableListOf()
    var colonyShips:  MutableList<Ship> = mutableListOf()
    private val numPlanets = 4

    fun roll()
    {
        type = StarType.values()[Random.nextInt(0, StarType.values().count())]
        //val numPlanets = Random.nextInt(1,4)

        for( i in 1..numPlanets)
        {
            val planetRolled = Planet(name)
            planetRolled.roll(i)
            planets[i-1] = planetRolled
        }
    }

    fun getAllegiance(): Allegiance
    {
        //If any world in system is enemy held, count the system as enemy
        for( i in 1..numPlanets)
        {
            if(planets[i-1]!!.ownerIndx == Allegiance.Enemy) {
                return Allegiance.Enemy
            }
        }
        //If one or more worlds are player held and there is no enemy world, count it as player system
        for( i in 1..numPlanets)
        {
            if(planets[i-1]!!.ownerIndx == Allegiance.Player) {
                return Allegiance.Player
            }
        }
        //If no player or enemy count as unoccupied
        return Allegiance.Unoccupied
    }

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

    fun enemyIsPresent(): Boolean
    {
        return false; //TODO
    }
}
