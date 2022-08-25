import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class GalaxyState {

    var stars = mutableMapOf<Int, Star>()
    //Index of the player's chosen star or 0 if none
    var activePlayerStar = 0
    //Index of the player's chosen planet or 0 if none
    var activePlayerPlanet = 0

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
