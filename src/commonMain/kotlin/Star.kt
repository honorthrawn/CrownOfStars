import kotlinx.serialization.*
import kotlin.random.*

@Serializable
enum class StarType {
    YELLOW,
    BLUE,
    RED
}

@Serializable
data class Star(val name: String) {
    var type: StarType = StarType.YELLOW
    var planets = mutableMapOf<Int, Planet>()
    var playerFleet = Fleet()
    var enemyFleet = Fleet()
    //Location in galaxy, used to help ComputerPlayerCore and such
    var xloc = 0
    var yloc = 0

    private val numPlanets = 4

    fun roll() {
        type = StarType.values()[Random.nextInt(0, StarType.values().count())]

        for( i in 1..numPlanets) {
            val planetRolled = Planet(name)
            planetRolled.roll(i)
            planets[i-1] = planetRolled
        }
    }

    fun nextTurn() {
       for(planet in planets.values) {
           planet.nextTurn()
       }
       playerFleet.nextTurn()
       enemyFleet.nextTurn()
    }

    fun getAllegiance(): Allegiance {
        //If any world in system is enemy held, count the system as enemy
        for( i in 1..numPlanets) {
            if(planets[i-1]!!.ownerIndex == Allegiance.Enemy) {
                return Allegiance.Enemy
            }
        }
        //If one or more worlds are player held and there is no enemy world, count it as player system
        for( i in 1..numPlanets) {
            if(planets[i-1]!!.ownerIndex == Allegiance.Player) {
                return Allegiance.Player
            }
        }
        //If no player or enemy count as unoccupied
        return Allegiance.Unoccupied
    }
}
