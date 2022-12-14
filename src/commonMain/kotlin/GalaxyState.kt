import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class GalaxyState {

    var stars = mutableMapOf<Int, Star>()
    suspend fun rollGalaxy()
    {
        val starList = resourcesVfs["stars/starlist.txt"].readLines(UTF8)
        var nI = 0
        for(name in starList)
        {
            val newStar = Star(name)
            newStar.roll()
            stars[nI] = newStar
            nI++
        }
        //Player's starting world
        stars[0]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[0]!!.planets[2]!!.ownerIndx = Allegiance.Player
        stars[0]!!.planets[2]!!.farmers = 5u

        //Backup to last star to be enemy starting world
        nI-=2
        stars[nI]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[nI]!!.planets[2]!!.ownerIndx = Allegiance.Enemy
        stars[nI]!!.planets[2]!!.farmers = 5u
    }


    suspend fun load()
    {
        val jsonIn = applicationDataVfs["galaxyState.json"].readString()
        println(jsonIn)
        val json = Json { prettyPrint = true }
        stars = json.decodeFromString(jsonIn)
    }

    suspend fun save()
    {
        val json = Json { prettyPrint = true }
        val jsonOut = json.encodeToString(stars)
        println(jsonOut)
        applicationDataVfs["galaxyState.json"].writeString(jsonOut)
    }
}
