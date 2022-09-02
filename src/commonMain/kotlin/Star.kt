import kotlinx.serialization.Serializable
import kotlin.random.*

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
    val numPlanets = 4
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

}
