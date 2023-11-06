
import com.soywiz.korio.file.std.applicationDataVfs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.UTF8
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.set

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
            stars[nI]!!.xloc = nI % 10
            stars[nI]!!.yloc = nI / 10
            nI++
        }
        //Player's starting world
        stars[0]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[0]!!.planets[2]!!.ownerIndex = Allegiance.Player
        stars[0]!!.planets[2]!!.farmers = 5u

        //Backup to last star to be enemy starting world
        nI--
        stars[nI]!!.planets[2]!!.type = PlanetType.TERRAN
        stars[nI]!!.planets[2]!!.ownerIndex = Allegiance.Enemy
        stars[nI]!!.planets[2]!!.farmers = 5u
    }

    //This function does end of turn bookkeeping for the galaxy state.  Right now, that means calling next turn
    //on each planet for terraforming
    fun nextTurn()
    {
       for( star in stars.values )
       {
           star.nextTurn()
       }
    }

    suspend fun load()
    {
        val jsonIn = applicationDataVfs["galaxyState.json"].readString()
        //println(jsonIn)
        val json = Json { prettyPrint = true; allowStructuredMapKeys= true}
        stars = json.decodeFromString(jsonIn)
    }

    suspend fun save()
    {
        val json = Json { prettyPrint = true; allowStructuredMapKeys = true }
        val jsonOut = json.encodeToString(stars)
        //println(jsonOut)
        applicationDataVfs["galaxyState.json"].writeString(jsonOut)
    }
}
