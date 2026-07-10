enum class FleetMission {
    DEFEND_COLONY,
    ATTACK_PLAYER_COLONY,
    //HUNT_PLAYER_FLEET,
    //ESCORT_COLONY_SHIP,
    HOLD_POSITION
}


class ComputerAdmiral(val galaxy: GalaxyState, val calculator: BonusCalculator) {

    var computerFleetSpeed = 0

    suspend fun issueShipOrders() {
        computerFleetSpeed = calculator.getSpeed(Allegiance.Enemy)
        moveColonyShips()
        assignDestinationsForFleets()
    }

    fun assignDestinationsForFleets() {
        val allStars = galaxy.stars.values.toList()

        val starsWithCombatFleets = allStars.filter { star ->
            star.enemyFleet.isCorvetteAvailableToMove() ||
                star.enemyFleet.isCruiserAvailableToMove() ||
                star.enemyFleet.isBattleshipAvailableToMove()
        }

        for (star in starsWithCombatFleets) {
            val mission = chooseMissionForFleet(star.enemyFleet, star)

            val target = when (mission) {
                FleetMission.DEFEND_COLONY ->
                    chooseDefenseTarget(star.enemyFleet, star) ?: star

                FleetMission.ATTACK_PLAYER_COLONY ->
                    chooseAttackTarget(star.enemyFleet, star) ?: star

                FleetMission.HOLD_POSITION ->
                    nearestEnemyColony(star) ?: star
            }

            val nextStar = chooseStepTowardTarget(star, target, star.enemyFleet, galaxy)
            if (nextStar !== star) {
                moveComputerFleet(star, nextStar, star.enemyFleet)
            }
        }
    }

    fun chooseAttackTarget(fleet: Fleet, startStar: Star) : Star {
        if(startStar.hasPlayerColony() || startStar.playerFleet.isPresent()) {
            return startStar
        }
        val possibleTargets = galaxy.stars.values.filter { it.hasPlayerColony() }
        val target = possibleTargets.maxByOrNull { scoreComputerAttackDestination( fleet, startStar, it ) }
        //Special Case -- may have one colony but forces are too weak to attack it
        if( target != null) {
            val score = scoreComputerAttackDestination(fleet, startStar, target)
            if(score == Int.MIN_VALUE) {
                return startStar
            }
        }
        return target ?: startStar
    }


    fun chooseDefenseTarget(fleet: Fleet, startStar: Star) : Star? {
        // If already at a colony under attack, stay and fight.
        if (startStar.hasEnemyColony() && startStar.playerFleet.isPresent()) {
            return startStar
        }

        // If Ragnarok is active here, stay.
        if (startStar.isRagnarokProtocolActive()) {
            return startStar
        }

        return galaxy.stars.values
            .filter { it.hasEnemyColony() }
            .filter { enemyColonyThreatScore(it) > 0 }
            .maxByOrNull { enemyColonyThreatScore(it) }
    }

    fun nearestEnemyColony(startStar: Star): Star? {
        return galaxy.stars.values
            .filter { it.hasEnemyColony() }
            .minByOrNull { galaxy.gridDistance(startStar, it) }
    }

    fun chooseMissionForFleet(fleet: Fleet, currentStar: Star): FleetMission {
        if (currentStar.playerFleet.isPresent()) {
            val enemyStrength = fleetStrength(fleet)
            val playerStrength = fleetStrength(currentStar.playerFleet)

            return if (enemyStrength >= playerStrength * 8 / 10) {
                FleetMission.ATTACK_PLAYER_COLONY // or later HUNT_PLAYER_FLEET
            } else {
                FleetMission.DEFEND_COLONY
            }
        }

        val threatenedColony = galaxy.stars.values
            .filter { it.hasEnemyColony() }
            .maxByOrNull { enemyColonyThreatScore(it) }

        val threatScore = threatenedColony?.let { enemyColonyThreatScore(it) } ?: 0

        if (threatScore >= 50) {
            return FleetMission.DEFEND_COLONY
        }

        val hasPlayerColonies = galaxy.stars.values.any { it.hasPlayerColony() }

        return if (hasPlayerColonies) {
            FleetMission.ATTACK_PLAYER_COLONY
        } else {
            FleetMission.HOLD_POSITION
        }
   }

    fun scoreComputerAttackDestination(
        fleet: Fleet,
        from: Star,
        candidate: Star,
    ): Int {
        if (candidate.playerFleet.isPresent() && !shouldAttackFleet(fleet, candidate.playerFleet)) {
            return Int.MIN_VALUE
        }

        var score = 0
        val distance = galaxy.gridDistance(from, candidate)

        score -= distance * 5

        val targetPlanets = candidate.planets.values
            .filter { it.ownerIndex == Allegiance.Player }

        for (planet in targetPlanets) {
            score += planet.getColonyValue()
        }

        return score
    }

  fun scoreStepTowardTarget(
    from: Star,
    target: Star,
    candidate: Star,
    fleet: Fleet
): Int {
    var score = 0

    // Main goal: get closer to target.
    val currentDistance = galaxy.gridDistance(from, target)
    val candidateDistance = galaxy.gridDistance(candidate, target)
    val progress = currentDistance - candidateDistance

    score += progress * 20

    // Avoid moving into superior player fleets.
    if (candidate.playerFleet.isPresent()) {
        val enemyStrength = fleetStrength(fleet)
        val playerStrength = fleetStrength(candidate.playerFleet)

        if (enemyStrength < playerStrength) {
            score -= 1000
        } else {
            score += 50
        }
    }

    // Prefer friendly colonies as safe staging points.
    if (candidate.hasEnemyColony()) {
        score += 15
    }

    // Avoid pointless wandering.
    if (candidate === from) {
        score -= 5
    }

    return score
}


    fun shouldAttackFleet(enemyFleet: Fleet, playerFleet: Fleet): Boolean {
        val enemyStrength = fleetStrength(enemyFleet)
        val playerStrength = fleetStrength(playerFleet)
        return enemyStrength >= playerStrength * .75 / 100
    }

  fun chooseStepTowardTarget(
    from: Star,
    target: Star,
    fleet: Fleet,
    galaxy: GalaxyState
): Star {
    val reachable = galaxy.stars.values.filter { candidate ->
        galaxy.gridDistance(from, candidate) <= computerFleetSpeed
    }

    return reachable.maxBy { candidate ->
        scoreStepTowardTarget(from, target, candidate, fleet)
    }
}

    fun enemyColonyThreatScore(star: Star): Int {
        var threat = 0

        if (star.playerFleet.isPresent()) {
            threat += 100
        }

        val nearbyPlayerFleets = galaxy.stars.values.count { other ->
            other.playerFleet.isPresent() && galaxy.gridDistance(star, other) <= 3
        }

        threat += nearbyPlayerFleets * 25

        return threat
    }

    fun fleetStrength(fleet: Fleet): Int {
        return fleet.getCorvetteTotalCount() + (fleet.getCruiserTotalCount() * 3) +
            ( fleet.getBattleShipTotalCount() * 6)  + fleet.getGalleonTotalCount()
        }

    fun moveComputerFleet(currentStar: Star, destination: Star, fleet: Fleet ) {
        var corvettesToMove = fleet.getMovableCorvetteCount()
        var cruisersToMove = fleet.getMovableCruiserCount()
        var battleshipsToMove = fleet.getMovableBattleShipCount()
        while(corvettesToMove > 0) {
            val shipMoving = currentStar.enemyFleet.removeShipFromFleetForMove(shipType.CORVETTE_ENEMY)
            if(shipMoving != null) {
                shipMoving.hasMoved = true
                destination.enemyFleet.add(shipMoving)
            }
            corvettesToMove--
        }
        while(cruisersToMove > 0) {
            val shipMoving = currentStar.enemyFleet.removeShipFromFleetForMove(shipType.CRUISER_ENEMY)
            if(shipMoving != null) {
                shipMoving.hasMoved = true
                destination.enemyFleet.add(shipMoving)
            }
            cruisersToMove--
        }
        while(battleshipsToMove > 0) {
            val shipMoving = currentStar.enemyFleet.removeShipFromFleetForMove(shipType.BATTLESHIP_ENEMY)
            if(shipMoving != null) {
                shipMoving.hasMoved = true
                destination.enemyFleet.add(shipMoving)
            }
            battleshipsToMove--
        }
    }

    fun moveColonyShips() {
        val starsWithUnmovedColonyShips = galaxy.stars.values
            .filter { star ->
                star.enemyFleet.isColonyAvailableToMove()
            }
            .toList()

        for (star in starsWithUnmovedColonyShips) {
            println("MOVING COLONY SHIPS")
            moveColonyShip(star)
        }
    }

    fun moveColonyShip(startStar: Star) {
        println("MOVING A COLONY SHIP")
        //first see if the star the ship is at has uncolonized worlds
        val unsettledPlanets = startStar.planets.values.filter {
                planet: Planet -> planet.ownerIndex == Allegiance.Unoccupied }
        if(unsettledPlanets.isNotEmpty()) {
            println("ESTABLISHING COLONY IN SYSTEM")
            val newColony = unsettledPlanets.maxBy { it.getColonyValue() }
            newColony.ownerIndex = Allegiance.Enemy
            newColony.farmers = 1u
            startStar.enemyFleet.destroyShip(shipType.COLONY_ENEMY)
        } else {
            println("COLONY SHIP MOVING")
            val destination = findBestColonizationTarget(startStar)
            if (destination == null) {
                println("No colonization target found")
                return
            }

            val shipMoving = startStar.enemyFleet.removeShipFromFleetForMove(shipType.COLONY_ENEMY)
            if( shipMoving != null) {
                shipMoving.hasMoved = true
                destination.enemyFleet.add(shipMoving)
            }
        }
    }

    fun findBestColonizationTarget(fromStar: Star): Star? {
        return galaxy.stars.values
            .filter { star ->
                star !== fromStar &&
                    galaxy.gridDistance(fromStar, star) <= computerFleetSpeed &&
                    star.planets.values.any { planet ->
                        planet.ownerIndex == Allegiance.Unoccupied
                    }
            }
            .maxByOrNull { star ->
                star.planets.values
                    .filter { it.ownerIndex == Allegiance.Unoccupied }
                    .maxOfOrNull { it.getColonyValue() } ?: Int.MIN_VALUE
            }
    }
}

