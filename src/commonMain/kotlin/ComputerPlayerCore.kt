
class ComputerPlayerCore(
    val gs: GalaxyState,
    val es: EmpireState,
    val techs: TechTree,
    private val bonusCalculator: BonusCalculator
) {
    val MIN_COLONIZED_WORLDS_FOR_DOMINANCE = 12
    val DOMINANCE_PERCENT = 70.0
    //Limited resources, can't really do everything all the time.   So different governors/ministers run randomly based on\
    //aggression level to determine what computer player will spend points on,
    private var laborInitialized = false
    private var governors = ComputerGovernors()
    private var admiral = ComputerAdmiral(gs, bonusCalculator)
    private var laborGovernor = LaborGovernor()
    private var shipFactory = shipFactory()
    private lateinit var assessment: EmpireAssessment
    private lateinit var colonyCosts: shipCosts
    private lateinit var corvetteCosts: shipCosts
    private lateinit var cruiserCosts: shipCosts
    private lateinit var battleshipCosts: shipCosts
    private lateinit var galleonCosts: shipCosts

    private val enemyEmpire: Empire
        get() = es.empires[Allegiance.Enemy.ordinal]!!

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
            shipType.CORVETTE_ENEMY -> enemyEmpire.buyShip(corvetteCosts)
            shipType.CRUISER_ENEMY -> enemyEmpire.buyShip(cruiserCosts)
            shipType.BATTLESHIP_ENEMY -> enemyEmpire.buyShip(battleshipCosts)
            shipType.GALLEON_ENEMY -> enemyEmpire.buyShip(galleonCosts)
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
        enemyEmpire.addPopulation()
        planet.addPopulation(1u)
    }

    private fun canGrowPopulation(planet: Planet): Boolean {
        return enemyEmpire.organicPoints >= 50u && planet.canGrowPopulation()
    }

    private fun canBuildShip(type: shipType, empire: Empire) : Boolean {
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

        return empire.shipPoints >= costToBuild.metal && empire.organicPoints >= costToBuild.organics
    }

    private fun buildColonyShip() {
        println("BUILDING A COLONY SHIP")
        val aiStars = gs.stars.values.filter { star: Star -> star.getAllegiance() == Allegiance.Enemy }
        if(aiStars.isNotEmpty()) {
            enemyEmpire.buyShip(colonyCosts)
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

    private suspend fun initializeLaborGovernor() {
        if (!laborInitialized) {
            laborGovernor.init()
            laborInitialized = true
        }
    }

    fun runGrowth() {
        //Add population if we can & assign workers
        val allStars = gs.stars.values
        for( star in allStars) {
            val aiPlanets = star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy }
            for (planet in aiPlanets) {
                if (canGrowPopulation(planet)) {
                    growPopulation(planet)
                }
            }
        }
    }

    fun runExpansion(maxTotalColonyShips: Int) {
        if (assessment.colonyShipCount >= maxTotalColonyShips) return

        var shipsBuilt = 0
        while (
            assessment.colonyShipCount + shipsBuilt < maxTotalColonyShips &&
            canBuildShip(shipType.COLONY_ENEMY, enemyEmpire)
        ) {
            buildColonyShip()
            shipsBuilt += 1
        }
    }

    fun runLabor() {
        val allStars = gs.stars.values
        for( star in allStars) {
            val aiPlanets = star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy }
            for (planet in aiPlanets) {
                assignPopulation(planet, assessment.posture)
            }
        }
    }

    fun runResearch() {
        println("COMPUTER PLAYER BUYS TECH")
        val choice = getComputerResearchChoice()
        if(choice == null) {
            println("COMPUTER PLAYER HAS NO TECH TO BUY")
        } else {
            if(enemyEmpire.canBuyTech(choice)) {
                enemyEmpire.buyTech(choice)
            }
        }
    }

    fun getComputerResearchChoice() : Tech? {
        //FOR NOW, just going to buy the cheapest advancement
        val undiscoveredTechs = techs.getUndiscoveredTechs(enemyEmpire.techTags)
        return(undiscoveredTechs.minWithOrNull( compareBy<Tech> { it.cost } ))
    }

    fun runShipBuilders(maxShipsPerType: Int) {
        println("COMPUTER PLAYER BUILDING SHIPS")
        val shipTypes = listOf(
            shipType.BATTLESHIP_ENEMY,
            shipType.CRUISER_ENEMY,
            shipType.CORVETTE_ENEMY
        )

        for (type in shipTypes) {
            var shipsBuilt = 0
            while (shipsBuilt < maxShipsPerType && canBuildShip(type, enemyEmpire)) {
                buildShip(type)
                shipsBuilt += 1
            }
        }
    }

    fun runBaseBuilders(maxBases: Int) {
        println("COMPUTER PLAYER BUILDING BASES")
        var basesBuilt = 0
        while (basesBuilt < maxBases && enemyEmpire.defensePoints >= 50u) {
            buildDefenseBase()
            basesBuilt += 1
        }
    }


    fun buildDefenseBase() {
        //Choose the planet to build the base.   For now, we will go with wherever has least bases.   If there is a tie
        //on least bases, then go with the one with max population
        val allStars = gs.stars.values
        val aiPlanets = mutableListOf<Planet>()
        for( star in allStars ) {
            aiPlanets.addAll(star.planets.values.filter { planet: Planet -> planet.ownerIndex == Allegiance.Enemy })
        }
        val fewest = aiPlanets.minWithOrNull(compareBy<Planet> { it.defenseBases }
            .thenByDescending { it.getTotalPopulation() } )
        //Build the base and deduct resources:
        if(fewest != null) {
            if(enemyEmpire.buildBase()) {
                fewest.addBase(1u)
            }
        }
    }

    suspend fun takeTurn() {
        assessment = assessEmpire()

        println("[AI] Posture: ${assessment.posture}")
        println("[AI] Worlds: enemy=${assessment.computerWorldCount}, player=${assessment.playerWorldCount}, empty=${assessment.unoccupiedWorldCount}")

        if (!::colonyCosts.isInitialized) {
            setShipCosts()
        }

        initializeLaborGovernor()

        println("[AI] HAS: ORGANICS: ${enemyEmpire.organicPoints} METAL:  ${enemyEmpire.shipPoints}")

        runMandatoryPriorities()

        assessment = assessEmpire()
        runStrategicPriorities()

        assessment = assessEmpire()
        println("COMPUTER ADMIRAL RUNNING")
        admiral.issueShipOrders()
        println("COMPUTER ADMIRAL DONE")
    }

    fun runMandatoryPriorities() {
        runLabor()
        runResearch()
    }

    fun runStrategicPriorities() {
        when (assessment.posture) {
            StrategicPosture.EXPAND -> {
                runExpansion(2)
                runGrowth()
            }

            StrategicPosture.BALANCED -> {
                runGrowth()
                runShipBuilders(1)
            }

            StrategicPosture.AGGRESSIVE -> {
                runShipBuilders(2)
                runBaseBuilders(1)
            }

            StrategicPosture.TURTLE -> {
                runBaseBuilders(2)
                runShipBuilders(1)
                runGrowth()
            }
        }
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

            colonyShipCount += star.enemyFleet.getMovableColonyShipCount()
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

    fun inferStrategicPosture(
        computerWorlds: Int,
        playerWorlds: Int,
        unoccupiedWorlds: Int,
        totalWorlds: Int
    ): StrategicPosture {
        if (totalWorlds <= 0) return StrategicPosture.BALANCED

        val unoccupiedRatio = unoccupiedWorlds.toDouble() / totalWorlds.toDouble()

        return when {
            // Lots of room left: keep expanding.
            unoccupiedRatio > 0.50 -> StrategicPosture.EXPAND

            // Enemy is far ahead.
            playerWorlds > computerWorlds * 1.5 -> StrategicPosture.TURTLE

            // AI is far ahead.
            computerWorlds > playerWorlds * 1.5 -> StrategicPosture.AGGRESSIVE

            // Some room remains, but not wide open.
            unoccupiedRatio > 0.25 -> StrategicPosture.EXPAND

            else -> StrategicPosture.BALANCED
        }
    }

    fun checkForVictory(): Allegiance? {
        val assessment = assessEmpire()

        // Victory condition 1: No colonies/planets left, you loose.
        if(assessment.playerWorldCount == 0) {
            return Allegiance.Enemy
        }
        else if(assessment.computerWorldCount == 0) {
            return Allegiance.Player
        }

        // Victory condition 2: Dominance Victory
        val totalColonized = assessment.playerWorldCount + assessment.computerWorldCount

        if (totalColonized < MIN_COLONIZED_WORLDS_FOR_DOMINANCE) {
            return null
        }

        val playerPercent = assessment.playerWorldCount * 100 / totalColonized
        val enemyPercent = assessment.computerWorldCount * 100 / totalColonized

        return when {
            playerPercent >= DOMINANCE_PERCENT -> Allegiance.Player
            enemyPercent >= DOMINANCE_PERCENT -> Allegiance.Enemy
            else -> null
        }
    }

}
