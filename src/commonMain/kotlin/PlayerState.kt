import com.soywiz.korge.scene.*

enum class operationType {
    SELECTION,
    MOVINGFLEET
}

enum class CrownOutcome {
    VOID,
    STEWARD,
    ASHEN,
    MILITARY_DEFEAT,
    CATASTROPHIC_DEFEAT
}

class PlayerState {
    var playerVictory = false
    var catastrophicDefeat = false
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

    fun determineCrownOutcome(): CrownOutcome {
        return if (playerVictory) {
            if (blackHolesCreatedByPlayer > 0) {
                CrownOutcome.VOID
            } else if (coloniesLost + regimentsLost < enemyPopulationKilled) {
                CrownOutcome.STEWARD
            } else {
                CrownOutcome.ASHEN
            }
        } else {
            if (catastrophicDefeat) {
                CrownOutcome.CATASTROPHIC_DEFEAT
            } else {
                CrownOutcome.MILITARY_DEFEAT
            }
        }
    }

    suspend fun determineCrown() : String {
        return when (determineCrownOutcome()) {
            CrownOutcome.VOID -> "ui/voidCrown.png"
            CrownOutcome.STEWARD -> "ui/stewardCrown.png"
            CrownOutcome.ASHEN -> "ui/ashenCrown.png"
            CrownOutcome.MILITARY_DEFEAT -> "ui/militaryDefeat.png"
            CrownOutcome.CATASTROPHIC_DEFEAT -> "ui/catastrophicDefeat.png"
        }
    }

    fun determineVictoryMessage(): String {
        return when (determineCrownOutcome()) {
            CrownOutcome.VOID -> "Through terrible power you won the Void Crown"
            CrownOutcome.STEWARD -> "Through wisdom you won the Steward Crown"
            CrownOutcome.ASHEN -> "Through ruin and sacrifice you won the Ashen Crown"
            CrownOutcome.MILITARY_DEFEAT -> ""
            CrownOutcome.CATASTROPHIC_DEFEAT -> ""
        }
    }

    fun determineDefeatMessage(): String {
        return when (determineCrownOutcome()) {
            CrownOutcome.MILITARY_DEFEAT -> "The crown slips from your grasp beneath the weight of enemy steel"
            CrownOutcome.CATASTROPHIC_DEFEAT -> "Your last world has fallen. The Crown of Stars is lost to history"
            CrownOutcome.VOID,
            CrownOutcome.STEWARD,
            CrownOutcome.ASHEN -> ""
        }
    }

}
