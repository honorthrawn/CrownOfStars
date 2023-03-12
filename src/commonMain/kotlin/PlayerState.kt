enum class operationType
{
    SELECTION,
    MOVINGFLEET,
    COLONIZE,
    TERRAFORM
}

class PlayerState {
    //Index of the player's chosen star or 0 if none
    var activePlayerStar = 0
    //Index of the player's chosen planet or 0 if none
    var activePlayerPlanet = 0
    var chosenTerraformers = 0
    var chosenColony = 0
    var operation = operationType.SELECTION
    var terraformingIndex = 0

    fun reset() {
        operation = operationType.SELECTION
        chosenTerraformers = 0
        chosenColony = 0
    }
}
