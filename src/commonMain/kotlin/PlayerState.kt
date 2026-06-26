import com.soywiz.korge.scene.*

enum class operationType {
    SELECTION,
    MOVINGFLEET
}

class PlayerState {
    var playerVictory = false
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
    var chosenGalleonForInvasion = 0
    var operation = operationType.SELECTION
    var terraformIndex = 0
    var bombardIndex = 0

    var battleRounds = 0
    var damageDealtLastBattle = 0
    var damageReceivedLastBattle = 0
    var shipsLostLastBattle = 0
    var enemyShipsDestroyedLastBattle = 0
    var totalDamageDealt = 0
    var totalDamageRecieved = 0
    var totalShipsLost = 0
    var totalEnemyShipsDestroyed = 0
    var colonistsLost = 0
    var regimentsLost = 0
    var enemyPopulationKilled = 0
    var blackHolesCreatedByPlayer = 0
    var blackHolesCreatedByEnemy = 0
    var coloniesLost = 0
    var coloniesEstablished = 0
    var techRealmChosen = TechRealm.COMPUTERS

    fun reset() {
        operation = operationType.SELECTION
        chosenTerraformers = 0
        chosenColony = 0
        chosenGalleon = 0
        chosenCorvette = 0
        chosenCruiser = 0
        chosenBattleship = 0
    }

    fun resetBattleStats() {
        battleRounds = 0
        damageDealtLastBattle = 0
        damageReceivedLastBattle = 0
        shipsLostLastBattle = 0
        enemyShipsDestroyedLastBattle = 0
    }

    suspend fun determineCrown() : String {
        var retval = ""
        if(playerVictory) {
            if( blackHolesCreatedByPlayer > 0 ) {
                retval = "ui/voidCrown.png"
            } else if( coloniesLost + regimentsLost < enemyPopulationKilled ) {
                retval = "ui/stewardCrown.png"
            } else {
                retval = "ui/ashenCrown.png"
            }
        } else {
            if(false) //TODO fill this in later if the enemy managed to turn SOL system into Black Hole
            {

            } else {
                retval = "ui/militaryDefeat.png"
            }
        }
        return retval
    }

}
