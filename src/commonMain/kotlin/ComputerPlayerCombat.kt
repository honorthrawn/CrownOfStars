class ComputerPlayerCombat(val gs: GalaxyState, val es: EmpireState, val cs: ComputerPlayerState) {

    public fun getShipsToFire() : shipType? {
        var firingShips: shipType? = null
        if(gs.stars[cs.activeBattleStar]!!.enemyFleet.canFire(shipType.CORVETTE_ENEMY) ) {
            firingShips = shipType.CORVETTE_ENEMY
        } else if (gs.stars[cs.activeBattleStar]!!.enemyFleet.canFire(shipType.CRUISER_ENEMY)) {
            firingShips = shipType.CRUISER_ENEMY
        } else if (gs.stars[cs.activeBattleStar]!!.enemyFleet.canFire(shipType.BATTLESHIP_ENEMY)) {
            firingShips = shipType.BATTLESHIP_ENEMY
        } else if (gs.stars[cs.activeBattleStar]!!.enemyFleet.canFire(shipType.GALLEON_ENEMY)) {
            firingShips = shipType.GALLEON_ENEMY
        } else if (gs.stars[cs.activeBattleStar]!!.enemyFleet.canFire(shipType.COLONY_ENEMY)) {
            firingShips = shipType.COLONY_ENEMY
        }
        return( firingShips)
    }

    public fun getShipsToFireOn() : shipType? {
        var target: shipType? = null
        if(gs.stars[cs.activeBattleStar]!!.playerFleet.isColonyPresent()) {
            target = shipType.COLONY_HUMAN
        } else if(gs.stars[cs.activeBattleStar]!!.playerFleet.isGalleonsPresent()) {
            target = shipType.GALLEON_HUMAN
        } else if(gs.stars[cs.activeBattleStar]!!.playerFleet.isCorvettesPresent()) {
            target = shipType.CORVETTE_HUMAN
        } else if(gs.stars[cs.activeBattleStar]!!.playerFleet.isCruisersPresent()) {
            target = shipType.CRUISER_HUMAN
        } else if(gs.stars[cs.activeBattleStar]!!.playerFleet.isBatteshipsPresent()) {
            target = shipType.BATTLESHIP_HUMAN
        } else if(gs.stars[cs.activeBattleStar]!!.playerFleet.isTerraformersPresent()) {
            target = shipType.TERRAFORMATTER_HUMAN
        }
        return(target)
    }
}
