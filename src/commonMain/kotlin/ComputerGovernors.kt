
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import kotlin.random.*

enum class Governor {
    GROWTH,
    EXPANSION,
    LABOR,
    RESEARCH,
    SHIPBUILDER,
    BASEBUILDER
}

enum class StrategicPosture {
    TURTLE,
    EXPAND,
    BALANCED,
    AGGRESSIVE
}

data class RangeGovernor(
    val startRange: Int,
    val endRange: Int,
    val action: String
)

data class Weights(
    val farmers: Double,
    val shipBuilders: Double,
    val researchers: Double,
    val baseBuilders: Double,
)

class LaborGovernor {

    private lateinit var turtleWeight: Weights
    private lateinit var lowWeight: Weights
    private lateinit var mediumWeight: Weights
    private lateinit var highWeight: Weights

    private suspend fun parseFile(file: String) : Weights {
        val fileContent = resourcesVfs[file].readLines(UTF8)
        return fileContent
            .filter { it.isNotBlank() } // Filter out empty or blank lines
            .map { line ->
                val parts = line.split("|")
                Weights(
                    farmers = parts[0].trim().toDouble(),
                    shipBuilders = parts[1].trim().toDouble(),
                    researchers = parts[2].trim().toDouble(),
                    baseBuilders = parts[3].trim().toDouble(),
                )
            }.toList()[0]
    }

    private suspend fun getWeights(agroLevel: StrategicPosture): Weights {
        val weights =  when(agroLevel) {
            StrategicPosture.EXPAND, -> parseFile("computerplayer/LowWeight.txt")
            StrategicPosture.BALANCED -> parseFile("computerplayer/MediumWeight.txt")
            StrategicPosture.AGGRESSIVE -> parseFile("computerplayer/HighWeight.txt")
            StrategicPosture.TURTLE -> parseFile("computerplayer/TurtleWeight.txt")
        }
        return weights
    }

    fun assignPopulation(planet: Planet, posture: StrategicPosture) {
        val weights = when(posture) {
                StrategicPosture.TURTLE -> turtleWeight
                StrategicPosture.EXPAND, -> lowWeight
                StrategicPosture.BALANCED -> mediumWeight
                StrategicPosture.AGGRESSIVE -> highWeight
            }

            val unassigned = planet.workerPool.toInt()
            val alloc = allocateByWeights(unassigned, weights)

            planet.farmers += alloc.getValue("farmers").coerceAtLeast(0).toUInt()
            planet.shipbuilders += alloc.getValue("ship").coerceAtLeast(0).toUInt()
            planet.scientists += alloc.getValue("research").coerceAtLeast(0).toUInt()
            planet.defworkers += alloc.getValue("bases").coerceAtLeast(0).toUInt()
            planet.workerPool = 0u
    }

    suspend fun init() {
        turtleWeight = getWeights(StrategicPosture.TURTLE)
        lowWeight = getWeights(StrategicPosture.EXPAND)
        mediumWeight = getWeights(StrategicPosture.BALANCED)
        highWeight = getWeights(StrategicPosture.AGGRESSIVE)
    }

    /**
     * Split `pool` workers into 4 integer buckets according to weights.
     * Works even if weights don't sum to 1.0 (it scales by total).
     * Tie-break is deterministic by the order in `priorityOrder`.
     */
    private fun allocateByWeights(
        pool: Int,
        w: Weights,
        priorityOrder: List<String> = listOf("ship", "research", "bases", "farmers") // your tie-break
    ): Map<String, Int> {
        if (pool <= 0) return mapOf("farmers" to 0, "ship" to 0, "research" to 0, "bases" to 0)

        val total = (w.farmers + w.shipBuilders + w.researchers + w.baseBuilders).let { if (it <= 0.0) 1.0 else it }

        val targets = listOf(
            "farmers"  to pool * (w.farmers      / total),
            "ship"     to pool * (w.shipBuilders / total),
            "research" to pool * (w.researchers  / total),
            "bases"    to pool * (w.baseBuilders / total)
        )

        val floors = targets.associate { it.first to it.second.toInt() }.toMutableMap()
        var used = floors.values.sum()
        var rem = pool - used

        if (rem > 0) {
            // Sort by fractional part desc, then by your priorityOrder
            val frac = targets
                .map { Triple(it.first, it.second - it.second.toInt(), it.second) }
                .sortedWith(
                    compareByDescending<Triple<String, Double, Double>> { it.second }
                        .thenBy { priorityOrder.indexOf(it.first).let { i -> if (i == -1) Int.MAX_VALUE else i } }
                )

            var i = 0
            while (rem > 0 && i < frac.size) {
                val role = frac[i].first
                floors[role] = floors.getValue(role) + 1
                rem--; i++
            }
        }

        return floors
    }
}


class ComputerGovernors {

    private suspend fun parseFile(file: String): List<RangeGovernor> {
        val fileContent = resourcesVfs[file].readLines(UTF8)
        return fileContent
            .filter { it.isNotBlank() } // Filter out empty or blank lines
            .map { line ->
                val parts = line.split("|")
                RangeGovernor(
                    startRange = parts[0].trim().toInt(),
                    endRange = parts[1].trim().toInt(),
                    action = parts[2].trim()
                )
            }.toList()
    }

    private fun determineActiveGovernor(rangeGovernor: List<RangeGovernor>) : Governor {
        val roll = Random.nextInt(1, 101) // Generates a random number between 1 and 100
        println("Rolled: $roll")

        for (rangeAction in rangeGovernor) {
            if (roll in rangeAction.startRange..rangeAction.endRange) {
                println("Performing action: ${rangeAction.action}")
                return Governor.valueOf(rangeAction.action.trim())
            }
        }
        return Governor.LABOR
    }

    private suspend fun getGovernorRanges(agroLevel: StrategicPosture) : List<RangeGovernor> {
        val range = when(agroLevel) {
            StrategicPosture.EXPAND -> parseFile("computerplayer/LowAggression.txt")
            StrategicPosture.BALANCED -> parseFile("computerplayer/MediumAggression.txt")
            StrategicPosture.AGGRESSIVE -> parseFile("computerplayer/HighAggression.txt")
            StrategicPosture.TURTLE -> parseFile("computerplayer/TurtleAggression.txt")
        }
        return range
    }

    suspend fun getNextGovernor(agroLevel: StrategicPosture) : Governor {
        return(determineActiveGovernor(getGovernorRanges(agroLevel)))
    }
}
