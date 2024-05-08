import kotlinx.serialization.*

@Serializable
enum class Allegiance {
    Unoccupied,
    Player,
    Enemy
}

@Serializable
data class Empire(val id: Allegiance) {
    var shipPoints =  0u
    var organicPoints = 0u
    var researchPoints = 0u
    var defensePoints = 0u

    var techTags: MutableList<Int> = mutableListOf()

    fun addProduction(gs: GalaxyState) {
        for( star in gs.stars.values) {
            for( planet in star.planets.values) {
                if(planet.ownerIndex == id ) {
                    shipPoints += planet.GetShipProduction()
                    organicPoints += planet.GetOrganicProduction()
                    researchPoints += planet.GetResearchProduction()
                    defensePoints += planet.GetDefenseProduction()
                }
            }

            //Star bonus
            if(star.getAllegiance() == id) {
                when(star.type)  {
                    StarType.YELLOW -> researchPoints += 2u
                    StarType.BLUE -> shipPoints += 4u
                    StarType.RED -> organicPoints += 4u
                }
            }
        }
    }

    fun addPopulation(): Boolean {
        var retval = false
        if(organicPoints >= 50u)  {
            organicPoints -= 50u
            retval = true
        }
        return retval
    }

    fun buildBase(): Boolean {
        var retval = false
        if(defensePoints >= 50u) {
            defensePoints -= 50u
            retval = true
        }
        return retval
    }

    fun buyShip(costs: shipCosts): Boolean {
        var retval = false
        if(shipPoints >= costs.metal && organicPoints >= costs.organics) {
            shipPoints -= costs.metal
            organicPoints -= costs.organics
            retval = true
        }
        return retval
    }

    fun popTagsStartingTechs(techTree: TechTree) {
        for( computer in techTree.computersTree) {
            if (computer.starting) {
                techTags.add(computer.id)
            }
        }

        for (weapon in techTree.weaponsTree) {
            if(weapon.starting) {
                techTags.add(weapon.id)
            }
        }
    }

    fun buyTech(tech: Tech) : Boolean {
        var retval = false
        if(researchPoints >= tech.cost ) {
            researchPoints -= tech.cost
            techTags.add(tech.id)
            retval = true
        }
        return retval
    }

    fun canBuyTech(tech: Tech) : Boolean {
        var retval = false
        if(researchPoints >= tech.cost ) {
            retval = true
        }
        return retval
        }
    }
