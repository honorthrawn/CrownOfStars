
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class GalaxyState {

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
    }

    fun nextTurn() {
        for (star in stars.values) {
            star.nextTurn()
        }
    }

    suspend fun load() {
        val jsonIn = applicationDataVfs["galaxyState.json"].readString()
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
        applicationDataVfs["galaxyState.json"].writeString(jsonOut)
    }
}
