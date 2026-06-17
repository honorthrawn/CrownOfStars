
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class GalaxyState {
    var starDate = 2260
    var stars = mutableMapOf<Int, Star>()

    suspend fun rollGalaxy() {
        val starList = resourcesVfs["stars/starlist.txt"].readLines(UTF8)
        var nI = 0

        for (name in starList) {
            val newStar = Star(name)
            newStar.roll()

            stars[nI] = newStar
            stars[nI]!!.xloc = nI % 10
            stars[nI]!!.yloc = nI / 10

            nI++
        }

        // Player's starting world
        stars[0]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[0]!!.planets[2]!!.ownerIndex = Allegiance.Player
        stars[0]!!.planets[2]!!.farmers = 5u

        // Backup to last star to be enemy starting world
        nI--
        stars[nI]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[nI]!!.planets[2]!!.ownerIndex = Allegiance.Enemy
        stars[nI]!!.planets[2]!!.farmers = 5u

        //Starting ships for the player and the enemy!
        val factory = shipFactory()
        factory.init()
        val colonyShipPlayer = factory.getShip(shipType.COLONY_HUMAN)
        stars[0]!!.playerFleet.add(colonyShipPlayer)
        val playerCorvette = factory.getShip(shipType.CORVETTE_HUMAN)
        stars[0]!!.playerFleet.add(playerCorvette)
        stars[0]!!.playerFleet.add(playerCorvette)
        stars[0]!!.playerFleet.add(playerCorvette)
        val colonyShipEnemy = factory.getShip(shipType.COLONY_ENEMY)
        stars[nI]!!.enemyFleet.add(colonyShipEnemy)
        val enemyCorvette = factory.getShip(shipType.CORVETTE_ENEMY)
        stars[nI]!!.enemyFleet.add(enemyCorvette)
        stars[nI]!!.enemyFleet.add(enemyCorvette)
        stars[nI]!!.enemyFleet.add(enemyCorvette)
    }

    fun nextTurn() {
        for (star in stars.values) {
            star.nextTurn()
        }
        starDate++
    }

    private val galaxySaveFile get() = applicationDataVfs["galaxyState.json"]

    suspend fun load() {
        val jsonIn = galaxySaveFile.readString()
        val json = Json {
            prettyPrint = true
            allowStructuredMapKeys = true
        }

        val loaded = json.decodeFromString(GalaxyState.serializer(), jsonIn)
        stars = loaded.stars
    }

    suspend fun save() {
        val json = Json {
            prettyPrint = true
            allowStructuredMapKeys = true
        }

        val jsonOut = json.encodeToString(GalaxyState.serializer(), this)
        galaxySaveFile.writeString(jsonOut)
    }

    suspend fun hasSaveGame(): Boolean {
        return galaxySaveFile.exists()
    }

}
