import rule.*

class ComputerPlayerCore(val gs: GalaxyState, val es: EmpireState, val techs: TechTree){
    //Limited resources, can't really do everything all the time.   So different governors/ministers run randomly based on
    //aggression level to determine what computer player will spend points on,
    private var laborInitialized = false
    private var governors = ComputerGovernors()
    private var laborGovernor = LaborGovernor()
    private var shipFactory = shipFactory()
    private var popRule: Rule<Planet>
    private var assignRule: Rule<Planet>
    private var buildColonyShipRule: Rule<Empire>
    private var buildCorvetteRule: Rule<Empire>
    private var buildCruiserRule: Rule<Empire>
    private var buildBattleshipRule: Rule<Empire>
    private var buildBaseRule: Rule<Empire>
    private lateinit var assessment: EmpireAssessment
    private lateinit var colonyCosts: shipCosts
    private lateinit var corvetteCosts: shipCosts
    private lateinit var cruiserCosts: shipCosts
    private lateinit var battleshipCosts: shipCosts
    private lateinit var galleonCosts: shipCosts

    init {
        buildCorvetteRule = rule("buildCorvettesRule") {
            name = "buildCorvettesRule"
            description = "start cranking out warships"
            condition = { canbuildShip(shipType.CORVETTE_ENEMY, it)}
            action = { buildShip(shipType.CORVETTE_ENEMY) }
        }

        buildCruiserRule = rule("buildCruisersRule") {
            name = "buildCruisersRule"
            description = "build cruiser"
            condition = { canbuildShip(shipType.CRUISER_ENEMY, it)}
            action = { buildShip(shipType.CRUISER_ENEMY) }
        }

        buildBattleshipRule = rule("buildBattleShipsRule") {
            name = "buildBattleShipsRule"
            description = "build battleships"
            condition = { canbuildShip(shipType.BATTLESHIP_ENEMY, it)}
            action = { buildShip(shipType.BATTLESHIP_ENEMY) }
        }


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

        assignRule = rule("assignPopulationRule") {
            name = "assignPopulationRule"
            description = "Rule to assign/reassign workers according to weights"
            condition = { it.ownerIndex == Allegiance.Enemy  }
            action = { assignPopulation(it, assessment.posture) }
        }

        buildBaseRule = rule("buildDefenseBaseRule") {
            name = "buildDefenseBaseRule"
            description = "Rule to build base if there is enough resources"
            condition = { es.empires[Allegiance.Enemy.ordinal]!!.defensePoints >= 50u }
            action = { buildDefenseBase() }
        }
    }

    private fun buildShip(type: shipType) {
        println("BUILDING A WARSHIP")

        val newShip = shipFactory.getShip(type)

        when(type) {
            shipType.TERRAFORMATTER_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.COLONY_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.CORVETTE_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.CRUISER_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.BATTLESHIP_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.GALLEON_HUMAN -> println("SHOULD NEVER BE HERE")
            shipType.COLONY_ENEMY -> println("COLONY SHIPS SHOULD BE BUILT BY OTHER FUNCTION")
            shipType.CORVETTE_ENEMY -> es.empires[Allegiance.Enemy.ordinal]!!.buyShip(corvetteCosts)
            shipType.CRUISER_ENEMY -> es.empires[Allegiance.Enemy.ordinal]!!.buyShip(cruiserCosts)
            shipType.BATTLESHIP_ENEMY -> es.empires[Allegiance.Enemy.ordinal]!!.buyShip(battleshipCosts)
            shipType.GALLEON_ENEMY -> es.empires[Allegiance.Enemy.ordinal]!!.buyShip(galleonCosts)
            }

        val musterStar = gs.stars.values.last { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        musterStar.enemyFleet.add(newShip)
    }

    private fun assignPopulation(planet: Planet,  posture: StrategicPosture) {
        println("ASSIGNING POPS")
        laborGovernor.assignPopulation( planet, posture )
    }

    private fun growPopulation(planet: Planet) {
        println("GROWING POPS")
        es.empires[Allegiance.Enemy.ordinal]!!.addPopulation()
        planet.addPopulation(1u)
    }

    private fun canbuildShip(type: shipType, empire: Empire) : Boolean {
        var retval = false
        val costToBuild: shipCosts = when(type) {
            shipType.COLONY_ENEMY -> colonyCosts
            shipType.CORVETTE_ENEMY -> corvetteCosts
            shipType.CRUISER_ENEMY -> cruiserCosts
            shipType.BATTLESHIP_ENEMY -> battleshipCosts
            shipType.GALLEON_ENEMY -> galleonCosts
            //These don't matter, AI won't be building human ships
            shipType.TERRAFORMATTER_HUMAN -> colonyCosts
            shipType.COLONY_HUMAN -> colonyCosts
            shipType.CORVETTE_HUMAN -> colonyCosts
            shipType.CRUISER_HUMAN -> colonyCosts
            shipType.BATTLESHIP_HUMAN -> colonyCosts
            shipType.GALLEON_HUMAN -> colonyCosts
        }
        if(empire.shipPoints >= costToBuild.metal && empire.organicPoints >= costToBuild.organics) {
            retval = true
        }
        return retval
    }

     private fun buildColonyShip() {
        println("BUILDING A COLONY SHIP")
        val aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        if(aiStars.isNotEmpty()) {
            es.empires[Allegiance.Enemy.ordinal]!!.buyShip(colonyCosts)
            val newColonyShip = shipFactory.getShip(shipType.COLONY_ENEMY)
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

    private fun findBestColonizationTarget(): Star? {
        return gs.stars.values
            .filter { star ->
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

    private fun moveColonyShip(startStar: Star) {
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
           /* //For now, do the simple thing and select the next star system - slowing marching backwards towards Sol
            val x = startStar.xloc
            val y = startStar.yloc
            val startloc = (y * 10 + x)
            var destination = startloc - 1
            //Probably won't happen but just in case
            if(destination <= 0) {
                destination = 0
            }*/
            val destination = findBestColonizationTarget()
            if (destination == null) {
                println("No colonization target found")
                return
            }

            val shipMoving = startStar.enemyFleet.removeShipFromFleetForMove(shipType.COLONY_ENEMY)
            shipMoving.hasMoved = true
            destination.enemyFleet.add(shipMoving)
        }
    }

    fun runGrowthRules() {
        //Add population if we can & assign workers
        val allStars = gs.stars.values
        for( star in allStars) {
            val aiPlanets = star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy }
            for (planet in aiPlanets) {
                popRule.fire(planet)
            }
        }
    }

      fun runExpansionRules(maxTotalColonyShips: Int) {
        if (assessment.colonyShipCount >= maxTotalColonyShips) return

        var shipsBuilt = 0
        var builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)

        while (builtColony) {
            shipsBuilt += 1

            if (assessment.colonyShipCount + shipsBuilt >= maxTotalColonyShips) {
                break
            }

            builtColony = buildColonyShipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }
      }

    fun runLaborRules() {
        val allStars = gs.stars.values
        for( star in allStars) {
            val aiPlanets = star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy }
            for (planet in aiPlanets) {
                assignRule.fire(planet)
            }
        }
    }

    fun runResearchRules() {
        println("COMPUTER PLAYER BUYS TECH")
        val choice = getComputerResearchChoice()
        if(choice == null) {
            println("COMPUTER PLAYER HAS NO TECH TO BUY")
        } else {
            if(es.empires[Allegiance.Enemy.ordinal]!!.canBuyTech(choice)) {
                es.empires[Allegiance.Enemy.ordinal]!!.buyTech(choice)
            }
        }
    }

    fun getComputerResearchChoice() : Tech? {
        //FOR NOW, just going to buy the cheapest advancement
        val undiscoveredTechs = techs.getUndiscoveredTechs(es.empires[Allegiance.Enemy.ordinal]!!.techTags)
        return(undiscoveredTechs.minWithOrNull( compareBy<Tech> { it.cost } ))
    }

    fun runShipBuilderRules(maxShipsPerType: Int) {
        println("COMPUTER PLAYER BUILDING SHIPS")
        var shipsBuilt = 0
        var builtBB = buildBattleshipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while( builtBB ) {
            shipsBuilt += 1
            if(shipsBuilt == maxShipsPerType) {
                break;
            }
            builtBB = buildBattleshipRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }

        shipsBuilt = 0
        var builtCruiser = buildCruiserRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while( builtCruiser ) {
            shipsBuilt += 1
            if(shipsBuilt == maxShipsPerType) {
                break;
            }
            builtCruiser = buildCruiserRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }

        shipsBuilt = 0
        var builtCorvette = buildCorvetteRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while( builtCorvette ) {
            shipsBuilt += 1
            if(shipsBuilt == maxShipsPerType) {
                break;
            }
            builtCorvette = buildCorvetteRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }
    }

    fun runBaseBuilderRules(maxBases: Int) {
        println("COMPUTER PLAYER BUILDING BASES")
        var basesBuilt = 0
        var builtDefBase = buildBaseRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while(builtDefBase) {
            basesBuilt += 1
            if(basesBuilt == maxBases) {
                break;
            }
            builtDefBase = buildBaseRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }
    }


    fun buildDefenseBase() {
        //Choose the planet to build the base.   For now, we will go with wherever has least bases.   If there is a tie
        //on least bases, then go with the one with max population
        val allStars = gs.stars.values
        val aiPlanets = mutableListOf<Planet>()
        for( star in allStars ) {
            aiPlanets.addAll(star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy });
        }
        val fewest = aiPlanets.minWithOrNull(compareBy<Planet> { it.defenseBases }
            .thenByDescending { it.getTotalPopulation() } )
        //Build the base and deduct resources:
        if(fewest != null) {
            if(es.empires[Allegiance.Enemy.ordinal]!!.buildBase()) {
                fewest.addBase(1u)
            }
        }
    }

    suspend fun takeTurn() {
        assessment = assessEmpire()

        println("[AI] Posture: ${assessment.posture}")
        println("[AI] Worlds: enemy=${assessment.computerWorldCount}, player=${assessment.playerWorldCount}, empty=${assessment.unoccupiedWorldCount}")
        println("COMPUTER HAS: ORGANICS: ${es.empires[Allegiance.Enemy.ordinal]!!.organicPoints} METAL:  ${es.empires[Allegiance.Enemy.ordinal]!!.shipPoints}")

        if (!::colonyCosts.isInitialized) {
            setShipCosts()
        }

        if (!laborInitialized) {
            laborGovernor.init()
            laborInitialized = true
        }

        /*
        val newGovernor = governors.getNextGovernor(assessment.posture)
        when (newGovernor) {
            Governor.GROWTH -> runGrowthRules()
            Governor.EXPANSION -> runExpansionRules()
            Governor.LABOR -> runLaborRules()
            Governor.RESEARCH -> runResearchRules()
            Governor.SHIPBUILDER -> runShipBuilderRules()
            Governor.BASEBUILDER -> runBaseBuilderRules()
        }*/
        runMandatoryRules()
        runPriorityRules()
        evaluateShipOrders()
    }

    fun runMandatoryRules() {
        runLaborRules()
        runResearchRules()
    }

    fun runPriorityRules() {
        when (assessment.posture) {
            StrategicPosture.EXPAND -> {
                runExpansionRules(2)
                runGrowthRules()
            }

            StrategicPosture.BALANCED -> {
                runGrowthRules()
                runShipBuilderRules(1)
            }

            StrategicPosture.AGGRESSIVE -> {
                runShipBuilderRules(2)
                runBaseBuilderRules(1)
            }

            StrategicPosture.TURTLE -> {
                runBaseBuilderRules(2)
                runShipBuilderRules(1)
                runGrowthRules()
            }
        }
    }

    fun evaluateShipOrders() {
        println("COMPUTER ADMIRAL RUNNING")

        //If we have colony ships, move them and/or establish colonies
        val allStars = gs.stars.values
        for (star in allStars) {
            if(star.enemyFleet.getColonyShipCount() > 0) {
                println("MOOVING COLONY SHIPS")
                moveColonyShip(star)
            }
        }
        println("COMPUTER ADMIRAL DONE")
    }

    private fun assessEmpire(): EmpireAssessment {
        val allStars = gs.stars.values

        val computerWorlds = mutableListOf<Planet>()
        val playerWorlds = mutableListOf<Planet>()
        val unoccupiedWorlds = mutableListOf<Planet>()

        var colonyShipCount = 0
        var warshipCount = 0
        var playerWarshipCount = 0

        for (star in allStars) {
            computerWorlds.addAll(star.planets.values.filter { it.ownerIndex == Allegiance.Enemy })
            playerWorlds.addAll(star.planets.values.filter { it.ownerIndex == Allegiance.Player })
            unoccupiedWorlds.addAll(star.planets.values.filter { it.ownerIndex == Allegiance.Unoccupied })

            colonyShipCount += star.enemyFleet.getColonyShipCount()
            // warshipCount += star.enemyFleet.getWarshipCount()
            // playerWarshipCount += star.playerFleet.getWarshipCount()
        }

        val totalWorlds = computerWorlds.size + playerWorlds.size + unoccupiedWorlds.size

        val posture = inferStrategicPosture(
            computerWorlds.size,
            playerWorlds.size,
            unoccupiedWorlds.size,
            totalWorlds
        )

        return EmpireAssessment(
            computerWorldCount = computerWorlds.size,
            playerWorldCount = playerWorlds.size,
            unoccupiedWorldCount = unoccupiedWorlds.size,
            totalWorldCount = totalWorlds,
            colonyShipCount = colonyShipCount,
            warshipCount = warshipCount,
            playerWarshipCount = playerWarshipCount,
            posture = posture
        )
    }

    fun inferStrategicPosture(computerWorlds: Int, playerWorlds: Int, unoccupiedWorlds : Int, totalWorlds : Int ) : StrategicPosture {
        var retval = StrategicPosture.EXPAND //Start with assumption it is early game
        if( unoccupiedWorlds <= totalWorlds * .50) {
            retval = StrategicPosture.BALANCED
        }
        if( unoccupiedWorlds <= totalWorlds * .25) {
            retval = StrategicPosture.AGGRESSIVE
        }
        if (computerWorlds > playerWorlds * 1.5) {
            retval = StrategicPosture.AGGRESSIVE
        }
        if (playerWorlds > computerWorlds * 1.5) {
            retval = StrategicPosture.TURTLE // turtle mode
        }
        return retval
    }
}
