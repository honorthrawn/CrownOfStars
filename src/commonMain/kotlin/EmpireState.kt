import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class EmpireState {
   var empires = mutableMapOf<Int, Empire>()

    fun rollEmpires()
    {
        var playerEmpire = Empire(Allegiance.Player)
        var enemyEmpire = Empire(Allegiance.Enemy)
        empires[0] = playerEmpire
        empires[1] = enemyEmpire
    }

    fun addProduction(gs: GalaxyState)
    {
        for(empire in empires.values)
        {
            empire.addProduction(gs);
        }
    }

    suspend fun load()
    {
        val jsonIn = applicationDataVfs["empireState.json"].readString()
        println(jsonIn)
        val json = Json { prettyPrint = true }
        empires = json.decodeFromString(jsonIn)
    }

    suspend fun save()
    {
        val json = Json { prettyPrint = true }
        val jsonOut = json.encodeToString(empires)
        println(jsonOut)
        applicationDataVfs["empireState.json"].writeString(jsonOut)
    }
}
