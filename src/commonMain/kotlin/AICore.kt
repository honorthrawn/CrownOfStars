import rule.*

class AICore(val gs: GalaxyState, val es: EmpireState){

    private var popRule: Rule<Planet>
    private var assignRule: Rule<Planet>
    private var buildColonyShipRule: Rule<Empire>
    private var dispatchColonyShip: Rule<Star>
    private var shipFactory = shipFactory()
    private lateinit var colonyCosts: shipCosts
    private lateinit var corvetteCosts: shipCosts
    private lateinit var cruiserCosts: shipCosts
    private lateinit var battleshipCosts: shipCosts
    private lateinit var galleonCosts: shipCosts

    init {
        popRule = rule("populationAddRule") {
            name = "populationAddRule"
            description = "This is the rule for adding population"
            condition = { es.empires[Allegiance.Enemy.ordinal]!!.organicPoints >= 50u && it.canGrowPopulation() }
            action = {
                growPopulation(it)
            }
        }

        buildColonyShipRule = rule("buildColonyShipRule") {
            name = "buildColonyShipRule"
            description = "This is the rule for building colony ship"
            condition = { canbuildShip(shipType.COLONY_ENEMY, it) }
            action = { buildColonyShip() }
        }

        dispatchColonyShip = rule("dispatchColonyShipRule") {
            name = "dispatchColonyShipRule"
            description = "This is the rule for moving colony ships"
            condition = { it.enemyFleet.getColonyShipCount() > 0 }
            action = { moveColonyShip(it) }
        }

        assignRule = rule("assignPopulationRule") {
            name = "assignPopulationRule"
            description = "Rule to assign workers if any spare"
            condition = { it.ownerIndex == Allegiance.Enemy  && it.workerPool > 0u }
            action = {
                assignPopulation()
                }
        }
    }

    private fun assignPopulation() {
        println("ASSIGNING POPS")
        var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        for( star in aiStars) {
            for (planet in star.planets.values) {
                while(planet.ownerIndex == Allegiance.Enemy && planet.workerPool > 0u ) {
                    if(planet.farmers > planet.shipbuilders) {
                        planet.shipbuilders++
                        planet.workerPool--
                    } else {
                        planet.farmers++
                        planet.workerPool--
                    }
                }
            }
        }
    }

    private fun growPopulation(planet: Planet) {
        println("GROWING POPS")
        es.empires[Allegiance.Enemy.ordinal]!!.addPopulation()
        planet.addPopulation(1u)
    }

    private fun canbuildShip(type: shipType, empire: Empire) : Boolean {
        var retval = false
        var costToBuild: shipCosts
        when(type) {
            shipType.COLONY_ENEMY -> costToBuild = colonyCosts;
            shipType.CORVETTE_ENEMY -> costToBuild = corvetteCosts;
            shipType.CRUISER_ENEMY -> costToBuild = cruiserCosts;
            shipType.BATTLESHIP_ENEMY -> costToBuild = battleshipCosts;
            shipType.GALLEON_ENEMY -> costToBuild = galleonCosts;
            //These don't matter, AI won't be building human ships
            shipType.TERRAFORMATTER_HUMAN -> costToBuild = colonyCosts;
            shipType.COLONY_HUMAN -> costToBuild = colonyCosts;
            shipType.CORVETTE_HUMAN -> costToBuild = colonyCosts;
            shipType.CRUISER_HUMAN -> costToBuild = colonyCosts;
            shipType.BATTLESHIP_HUMAN -> costToBuild = colonyCosts;
            shipType.GALLEON_HUMAN -> costToBuild = colonyCosts;
        }
        if(empire.shipPoints >= costToBuild.metal && empire.organicPoints >= costToBuild.organics) {
            retval = true
        }
        return retval
    }

     private fun buildColonyShip() {
        println("BUILDING A COLONY SHIP")
        var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        if(aiStars.isNotEmpty()) {
            es.empires[Allegiance.Enemy.ordinal]!!.buyShip(colonyCosts)
            var newColonyShip = shipFactory.getShip(shipType.COLONY_ENEMY)
            aiStars[0].enemyFleet.add(newColonyShip)
        }
    }

    suspend fun setShipCosts() {
        shipFactory.init()
        colonyCosts = getCosts(shipType.COLONY_ENEMY)
        corvetteCosts = getCosts(shipType.CORVETTE_ENEMY)
        cruiserCosts = getCosts(shipType.CRUISER_ENEMY)
        battleshipCosts = getCosts(shipType.BATTLESHIP_ENEMY)
        galleonCosts = getCosts(shipType.GALLEON_ENEMY)
    }

    private fun moveColonyShip(startStar: Star) {
        println("MOVING A COLONY SHIP")
        //first see if the star the ship is at has uncolonized worlds
        var unsettledPlanets = startStar.planets.values.filter {
            planet: Planet -> planet.ownerIndex == Allegiance.Unoccupied }
        if(unsettledPlanets.isNotEmpty()) {
            println("ESTABLISHING COLONY IN SYSTEM")
            //TODO: Bad -- could be colonizing barren or toxic world when plenty of good worlds around
            unsettledPlanets[0].ownerIndex = Allegiance.Enemy
            unsettledPlanets[0].farmers = 1u
            startStar.enemyFleet.destroyShip(shipType.COLONY_ENEMY)
        } else {
            println("COLONY SHIP MOVING")
            //For now, do the simple thing and select the next star system - slowing marching backwards towards Sol
            val x = startStar.xloc
            val y = startStar.yloc
            val startloc = (y * 10 + x)
            var destination = startloc - 1
            //Probably won't happen but just in case
            if(destination <= 0) {
                destination = 0
            }
            println("Destination sector: $destination")
            //TODO This is probably bad.   If there is a player fleet, they will be able to blow the AI colonizer out
            //of space
            var shipMoving = startStar.enemyFleet.removeShipFromFleetForMove(shipType.COLONY_ENEMY)
            if (shipMoving != null) {
                shipMoving.hasMoved = true
                gs.stars[destination]!!.enemyFleet.add(shipMoving)
            }
        }
    }

    fun takeTurn() {
        println("AI PLAYER RUNNING")
        println("AI HAS: ORGANICS: ${es.empires[Allegiance.Enemy.ordinal]!!.organicPoints} METAL:  ${es.empires[Allegiance.Enemy.ordinal]!!.shipPoints}")
        //If we can build colony ships, build them
        var builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while(builtColony) {
            builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }

        //If we have colony ships, move them and/or establish colonies
        var allStars = gs.stars.values
        for( star in allStars) {
            //make a loop in case more than one colony ship in system
            while( dispatchColonyShip.fire(star) ) {

            }
        }

        //Add population if we can & assign workers
        for( star in allStars) {
            var aiPlanets = star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy }
            for (planet in aiPlanets) {
                popRule.fire(planet)
                assignRule.fire(planet)
            }
        }



    }
}
