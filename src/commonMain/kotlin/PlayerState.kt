enum class operationType {
    SELECTION,
    MOVINGFLEET
}

class PlayerState {
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

    fun reset() {
        operation = operationType.SELECTION
        chosenTerraformers = 0
        chosenColony = 0
    }
}
