import kotlinx.serialization.Serializable

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

    fun addProduction(gs: GalaxyState)
    {
        for( star in gs.stars.values)
        {
            for( planet in star.planets.values)
            {
                if(planet.ownerIndx == id )
                {
                    shipPoints += planet.shipbuilders;
                    organicPoints += planet.farmers;
                    researchPoints += planet.scientists;
                }
            }
        }
    }
}
