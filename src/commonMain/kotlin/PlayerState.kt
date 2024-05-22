import com.soywiz.korge.scene.*

enum class operationType {
    SELECTION,
    MOVINGFLEET
}

class PlayerState {
    var musicSceneContainer: SceneContainer? = null
    //Index of the player's chosen star or 0 if none
    var activePlayerStar = 0
    //Index of the player's chosen planet or 0 if none
    var activePlayerPlanet = 0

    //chosen number of ships
    var chosenTerraformers = 0
    var chosenColony = 0
    var chosenCorvette = 0
    var chosenCruiser = 0
    var chosenBattleship = 0
    var chosenGalleon = 0
    var operation = operationType.SELECTION
    var terraformIndex = 0
    var bombardIndex = 0

    var totalRounds = 0
    var totalDamageDealt = 0
    var totalDamgeReceived = 0
    var shipsLost = 0
    var enemyShipsDestroyed = 0

    var techRealmChosen = TechRealm.COMPUTERS
    var techQueryId = 0

    fun reset() {
        operation = operationType.SELECTION
        chosenTerraformers = 0
        chosenColony = 0
    }

    fun resetBattleStats() {
        totalRounds = 0
        totalDamageDealt = 0
        totalDamgeReceived = 0
        shipsLost = 0
        enemyShipsDestroyed = 0
    }
}
