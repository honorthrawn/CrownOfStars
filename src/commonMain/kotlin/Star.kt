import kotlin.random.*

enum class StarType
{
    YELLOW,
    BLUE,
    RED
}

data class Star(val name: String)
{
    var type: StarType = StarType.YELLOW
    var planets = mutableMapOf<Int, Planet>()

    fun roll()
    {
        type = StarType.values()[Random.nextInt(0, StarType.values().count())]
        //val numPlanets = Random.nextInt(1,4)
        val numPlanets = 4
        for( i in 1..numPlanets)
        {
            val planetRolled = Planet(name)
            planetRolled.roll(i)
            planets[i-1] = planetRolled
        }
    }
}
