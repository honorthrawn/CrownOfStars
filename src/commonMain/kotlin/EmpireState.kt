import com.soywiz.korio.file.std.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class EmpireState {
    var empires = mutableMapOf<Int, Empire>()

    fun rollEmpires(techTree: TechTree) {
        val playerEmpire = Empire(Allegiance.Player)
        val enemyEmpire = Empire(Allegiance.Enemy)

        //Assign some starting resources so player has something interesting to do on first turn
        playerEmpire.organicPoints = 50u
        playerEmpire.shipPoints = 100u
        enemyEmpire.organicPoints = 50u
        enemyEmpire.shipPoints = 100u

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

    suspend fun load() {
        val jsonIn = applicationDataVfs["empireState.json"].readString()
        val json = Json { prettyPrint = true }
        val loaded = json.decodeFromString(EmpireState.serializer(), jsonIn)
        empires = loaded.empires
    }

    suspend fun save() {
        val json = Json { prettyPrint = true }
        val jsonOut = json.encodeToString(EmpireState.serializer(), this)
        applicationDataVfs["empireState.json"].writeString(jsonOut)
    }
}
