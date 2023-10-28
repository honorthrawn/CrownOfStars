
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*

@Serializable
enum class shipType {
    TERRAFORMATTER_HUMAN,
    COLONY_HUMAN,
    CORVETTE_HUMAN,
    CRUISER_HUMAN,
    BATTLESHIP_HUMAN,
    GALLEON,
}

data class shipCosts(val metal: UInt, val organics: UInt) {
}

suspend fun getCosts(shipType: shipType) : shipCosts {
    val shipList = resourcesVfs["ships/shipcosts.txt"].readLines(UTF8)
    val key = shipType.name
    for(record in shipList)
    {
        //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
        val sep = "|"
        var fields = record.split(sep)
        if(fields[0].trim() == key) {
            val metal = fields[1].toUInt()
            val organics = fields[2].toUInt()
            return shipCosts(metal, organics)
        }
    }
    return(shipCosts(0u,0u))
}

suspend fun shipFactory(shipType: shipType): Ship {
    val shipList = resourcesVfs["ships/ships.txt"].readLines(UTF8)
    val key = shipType.name
    for(record in shipList)
    {
        //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
        val sep = "|"
        var fields = record.split(sep)
        if(fields[0].trim() == key) {
            val hits = fields[1].toUInt()
            val moves = fields[2].toUInt()
            return Ship(shipType, hits, moves)
        }
    }
    println("Ship factory returning default ship")
    return Ship(shipType, 0u,0u)
}

@Serializable
data class Ship(val theType: shipType, val maxHP: UInt, val maxMoves: UInt) {
    var movesLeft = maxMoves
    var currentHP = maxHP
}
