import kotlinx.serialization.Serializable
import kotlin.random.*

@Serializable
enum class PlanetType
{
    TOXIC,
    OCEAN,
    TERRAN,
    DESSERT,
    VOLCANIC,
    BARREN,
    SUPERTERRAN,
    TROPICAL
}

@Serializable
enum class WorkerType
{
    FARMING,
    SHIPS,
    DEFENSE,
    SCIENCE
}

@Serializable
data class Planet(val star: String)
{
    var type: PlanetType = PlanetType.TOXIC
    var name: String = "VENUS"
    var workerPool: UInt = 0u
    var farmers: UInt = 0u
    var shipbuilders: UInt = 0u
    var defworkers: UInt = 0u
    var scientists: UInt = 0u
    var ownerIndex: Allegiance = Allegiance.Unoccupied
    var defenseBases: UInt = 0u
    var turnsLeftTerraform: Int = -1 //-1 indicates not being terraformed

    fun roll(pos: Int)
    {
        type = PlanetType.values()[Random.nextInt(0, PlanetType.values().count())]

        val numeral = when( pos) {
            1 -> "I"
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            else -> "PRIME"
        }
        name = "$star $numeral"
    }

    fun increaseWorker(workertype: WorkerType) {
        if(workerPool > 0u) {
            when (workertype) {
                WorkerType.FARMING -> farmers++
                WorkerType.SHIPS -> shipbuilders++
                WorkerType.DEFENSE -> defworkers++
                WorkerType.SCIENCE -> scientists++
            }
            workerPool--
        }
    }

    fun decreaseWorker(workertype: WorkerType) {
        when (workertype) {
            WorkerType.FARMING -> {
                if (farmers <= 0u)
                    return
                farmers--
            }

            WorkerType.SHIPS -> {
                if (shipbuilders <= 0u)
                    return
                shipbuilders--
            }

            WorkerType.DEFENSE -> {
                if (defworkers <= 0u)
                    return
                defworkers--
            }

            WorkerType.SCIENCE -> {
                if (scientists <= 0u)
                    return
                scientists--
            }
        }
        workerPool++
    }

    fun addPopulation(inc: UInt)
    {
        workerPool += inc
    }

    fun addBase(inc: UInt) {
        defenseBases += inc
    }


    //Should be called whenever there is a new turn.   Important for terraforming logic
    fun nextTurn()
    {
        if(turnsLeftTerraform > 1)
        {
            turnsLeftTerraform--
        } else if(turnsLeftTerraform == 1)
        {
            //Terraforming complete change counter back to not being terraformed
            turnsLeftTerraform = -1
            type = when(type) {
                PlanetType.TOXIC -> PlanetType.BARREN
                PlanetType.OCEAN -> PlanetType.TERRAN
                PlanetType.TERRAN -> PlanetType.SUPERTERRAN
                PlanetType.DESSERT -> PlanetType.TROPICAL
                PlanetType.VOLCANIC -> PlanetType.DESSERT
                PlanetType.BARREN -> PlanetType.VOLCANIC
                PlanetType.SUPERTERRAN -> PlanetType.SUPERTERRAN //Cannot be terraformed further
                PlanetType.TROPICAL -> PlanetType.SUPERTERRAN
            }
        }
        //If we aren't terraforming, there's nothing to do here
    }

    fun startTerraforming() {
        turnsLeftTerraform = when(type) {
            PlanetType.TOXIC -> 25
            PlanetType.OCEAN -> 10
            PlanetType.TERRAN -> 10
            PlanetType.DESSERT -> 15
            PlanetType.VOLCANIC -> 15
            PlanetType.BARREN -> 5
            PlanetType.SUPERTERRAN -> -1
            PlanetType.TROPICAL -> 5
        }
    }
}
