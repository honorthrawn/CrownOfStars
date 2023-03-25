import rule.*

class AICore(val gs: GalaxyState, val es: EmpireState){

    var popRule: Rule<Empire>
    var assignRule: Rule<Planet>
    init {
        popRule = rule<Empire>("populationAddRule") {
            name = "populationAddRule"
            description = "This is the rule for adding population"
            condition = { it.organicPoints >= 50u }
            action = {
                println("populationAddRule is fired")
                growPopulation()
            }
        }

        assignRule = rule<Planet>("assignPopulationRule") {
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

    fun takeTurn() {
        var popAdded = popRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        while(popAdded)
        {
            popAdded = popRule.fire(es.empires[Allegiance.Enemy.ordinal]!!)
        }
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
