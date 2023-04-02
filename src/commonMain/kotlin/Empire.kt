import kotlinx.serialization.*

@Serializable
enum class Allegiance
{
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

    fun addProduction(gs: GalaxyState)
    {
        for( star in gs.stars.values)
        {
            for( planet in star.planets.values)
            {
                if(planet.ownerIndex == id )
                {
                    shipPoints += planet.GetShipProduction()
                    organicPoints += planet.GetOrganicProduction()
                    researchPoints += planet.GetResearchProduction()
                    defensePoints += planet.GetDefenseProduction()
                }
            }

            //Star bonus
            if(star.getAllegiance() == id)
            {
                when(star.type)
                {
                    StarType.YELLOW -> researchPoints += 2u
                    StarType.BLUE -> shipPoints += 4u
                    StarType.RED -> organicPoints += 4u
                }
            }
        }
    }

    fun addPopulation(): Boolean {
        var retval = false
        if(organicPoints >= 50u)
        {
            organicPoints -= 50u
            retval = true
        }
        return retval
    }

    fun buildBase(): Boolean {
        var retval = false
        if(defensePoints >= 50u)
        {
            defensePoints -= 50u
            retval = true
        }
        return retval
    }
}
