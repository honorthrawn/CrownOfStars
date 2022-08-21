import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*

class GalaxyState {

    var stars = mutableMapOf<Int, Star>()
    //Index of the player's chosen star or 0 if none
    var activePlayerStar = 0
    //Index of the player's chosen planet or 0 if none
    var activePlayerPlanet = 0

    suspend fun rollGalaxy()
    {
        //val sol = Star("Sol")
        //sol.roll()
        //stars[0] = sol
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

    fun load()
    {

    }
}
