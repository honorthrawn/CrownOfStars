import kotlinx.serialization.*
import kotlin.random.*

@Serializable
enum class StarType {
    YELLOW,
    BLUE,
    RED,
    BLACK_HOLE;

    companion object {
        fun getImagePath(type: StarType) : String {
            val retval = when(type) {
                RED -> "stars/red_star.png"
                BLUE ->  "stars/blue_star.png"
                YELLOW ->  "stars/yellow_star.png"
                BLACK_HOLE -> "stars/black_hole.png"
            }
            return retval
        }

        fun rollableTypes(): List<StarType> {
            return listOf(YELLOW, BLUE, RED)
        }
    }
}

enum class RagnarokProtocolAdvanceResult {
    INACTIVE,
    CANCELED,
    CHARGING,
    READY_TO_COLLAPSE
}

@Serializable
data class Star(val name: String) {
    var type: StarType = StarType.YELLOW
    var planets = mutableMapOf<Int, Planet>()
    var playerFleet = Fleet()
    var enemyFleet = Fleet()
    var turnsLeftRagnarok = -1
    //Location in galaxy, used to help ComputerPlayerCore and such
    var xloc = 0
    var yloc = 0
    val numPlanets = 4
    var exploredByPlayer = false


    fun roll() {
        val rollableTypes = StarType.rollableTypes()
        type = rollableTypes[Random.nextInt(0, rollableTypes.count())]

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

    fun startRagnarokProtocol() {
        turnsLeftRagnarok = 3
    }

    fun cancelRagnarokProtocol() {
        turnsLeftRagnarok = -1
    }

    fun isRagnarokProtocolActive(): Boolean {
        return turnsLeftRagnarok > 0
    }

    fun advanceRagnarokProtocol(): RagnarokProtocolAdvanceResult {
        if (!isRagnarokProtocolActive()) {
            return RagnarokProtocolAdvanceResult.INACTIVE
        }

        if (!playerFleet.isBatteshipsPresent()) {
            cancelRagnarokProtocol()
            return RagnarokProtocolAdvanceResult.CANCELED
        }

        turnsLeftRagnarok--
        return if (turnsLeftRagnarok <= 0) {
            RagnarokProtocolAdvanceResult.READY_TO_COLLAPSE
        } else {
            RagnarokProtocolAdvanceResult.CHARGING
        }
    }

    fun getAllegiance(): Allegiance {
        //Black hole systems have no planets and cannot be occupied.   Fixing bug here
        if(type == StarType.BLACK_HOLE) {
            return Allegiance.Unoccupied
        }
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

    fun hasEnemyColony() : Boolean {
        if(type == StarType.BLACK_HOLE) {
            return false
        }
        //If any world in system is enemy held, count the system as enemy
        for( i in 1..numPlanets) {
            if(planets[i-1]!!.ownerIndex == Allegiance.Enemy) {
                return true
            }
        }
        return false
    }

    fun hasPlayerColony() : Boolean {
        if(type == StarType.BLACK_HOLE) {
            return false
        }
        //If any world in system is enemy held, count the system as enemy
        for( i in 1..numPlanets) {
            if(planets[i-1]!!.ownerIndex == Allegiance.Player) {
                return true
            }
        }
        return false
    }

    fun hasCombat(): Boolean {
        return playerFleet.isPresent() && enemyFleet.isPresent()
    }

}
