import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.Serializable

@Serializable
enum class shipType
{
    TERRAFORMATTER_HUMAN,
    COLONY_HUMAN

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
            val image = fields[1].trim()
            val hits = fields[2].toUInt()
            val moves = fields[3].toUInt()
            return Ship(shipType, image, hits, moves)
        }
    }
    println("Ship factory returning default ship")
    return Ship(shipType, "NOIMAGE", 0u,0u)
}

@Serializable
data class Ship(val theType: shipType, val image: String, val maxHP: UInt, val maxMoves: UInt)
{
    var movesLeft = maxMoves
    var currentHP = maxHP
}
