import com.soywiz.korio.file.std.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class EmpireState {
    var empires = mutableMapOf<Int, Empire>()

    fun rollEmpires(techTree: TechTree) {
        val playerEmpire = Empire(Allegiance.Player)
        val enemyEmpire = Empire(Allegiance.Enemy)

        playerEmpire.popTagsStartingTechs(techTree)
        enemyEmpire.popTagsStartingTechs(techTree)

        empires[Allegiance.Player.ordinal] = playerEmpire
        empires[Allegiance.Enemy.ordinal] = enemyEmpire
    }

    fun addProduction(gs: GalaxyState) {
        for (empire in empires.values) {
            empire.addProduction(gs)
        }
    }

    suspend fun load(): EmpireState {
        val jsonIn = applicationDataVfs["empireState.json"].readString()
        val json = Json { prettyPrint = true }
        return json.decodeFromString(EmpireState.serializer(), jsonIn)
    }

    suspend fun save() {
        val json = Json { prettyPrint = true }
        val jsonOut = json.encodeToString(EmpireState.serializer(), this)
        applicationDataVfs["empireState.json"].writeString(jsonOut)
    }
}
