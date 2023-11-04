import rule.*

class AICore(val gs: GalaxyState, val es: EmpireState){

    private var popRule: Rule<Empire>
    private var assignRule: Rule<Planet>
    private var buildColonyShipRule: Rule<Empire>
    private var dispatchColonyShip: Rule<Fleet>
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
            condition = { it.organicPoints >= 50u }
            action = {
                println("populationAddRule is fired")
                growPopulation()
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
            condition = { it.getColonyShipCount() > 0 }
            action = { moveColonyShip() }
        }

        assignRule = rule("assignPopulationRule") {
            name = "assignPopulationRule"
            description = "Rule to assign workers if any spare"
            condition = { it.ownerIndex == Allegiance.Enemy  && it.workerPool > 0u }
            action = {
                println("assignPopulationRule is fired")
                assignPopulation()
                }
        }
    }

    private fun assignPopulation() {
        var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        for( star in aiStars) {
            for (planet in star.planets.values) {
                while(planet.ownerIndex == Allegiance.Enemy && planet.workerPool > 0u )
                {
                    if(planet.farmers > planet.shipbuilders)
                    {
                        planet.shipbuilders++
                        planet.workerPool--
                    } else
                    {
                        planet.farmers++
                        planet.workerPool--
                    }
                }
            }
        }
    }

    private fun growPopulation() {
       //Find world and grow it
       var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        for( star in aiStars) {
            for (planet in star.planets.values) {
                if(planet.ownerIndex == Allegiance.Enemy)
                {
                    es.empires[Allegiance.Enemy.ordinal]!!.addPopulation()
                    planet.addPopulation(1u)
                }
            }
        }
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
        var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        if(aiStars.isNotEmpty()) {
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

    fun moveColonyShip() {

    }

    fun takeTurn() {
        //If we can build colony ships, build them
        var builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while(builtColony) {
            builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }

        //If we have colony ships, move them and/or establish colonies
        var allStars = gs.stars.values
        for( star in allStars) {
            dispatchColonyShip.fire(star.enemyFleet)
        }

        //Add population if we can
        var popAdded = popRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while(popAdded)
        {
            popAdded = popRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }

        //Assign workers for next turn
        var aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        for( star in aiStars) {
            for (planet in star.planets.values) {
                if (planet.ownerIndex == Allegiance.Enemy) {
                    assignRule.fire(planet)
                }
            }
        }


    }
}
