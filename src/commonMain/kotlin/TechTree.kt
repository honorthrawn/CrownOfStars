import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*

class TechTree {
    var computersTree: MutableList<ComputerTech> = mutableListOf()
    var weaponsTree: MutableList<WeaponsTech> = mutableListOf()
    var defenseTree: MutableList<DefenseTech> = mutableListOf()
    var propulsionTree: MutableList<PropulsionTech> = mutableListOf()

    suspend fun loadTrees() {
        loadComputers()
        loadWeapons()
        loadDefenses()
        loadDrives()
    }

    suspend fun loadComputers() {
        val computerList = resourcesVfs["tech/computers.txt"].readLines(UTF8)
        //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
        val sep = "|"
        for (record in computerList) {
            var fields = record.split(sep)
            if(fields.count() == 8) {
                val id = fields[0].toInt()
                val key = fields[1].trim()
                val name = fields[2].trim()
                val desc = fields[3].trim()
                val cost = fields[4].toUInt()
                val start = fields[5].toBoolean()
                val accuracy = fields[6].toInt()
                val initiative = fields[7].toInt()
                val computerTech = ComputerTech(id, key, name, desc, cost, start, accuracy, initiative)
                println("READ: ${id} ${name} ${desc} C ${cost} ${start} A ${accuracy} I ${initiative}")
                computersTree.add(computerTech)
            }
        }
    }

    suspend fun loadWeapons() {
        val weaponList = resourcesVfs["tech/weapons.txt"].readLines(UTF8)
        for (record in weaponList) {
            //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
            val sep = "|"
            var fields = record.split(sep)
            if(fields.count() == 8) {
                val id = fields[0].toInt()
                val key = fields[1].trim()
                val name = fields[2].trim()
                val desc = fields[3].trim()
                val cost = fields[4].toUInt()
                val start = fields[5].toBoolean()
                val lowDamage = fields[6].toInt()
                val highDamage = fields[7].toInt()
                val weaponTech = WeaponsTech(id, key, name, desc, cost, start, lowDamage, highDamage)
                weaponsTree.add(weaponTech)
            }
        }
    }

    suspend fun loadDefenses() {
        val defenseList = resourcesVfs["tech/defenses.txt"].readLines(UTF8)
        for (record in defenseList) {
            //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
            val sep = "|"
            var fields = record.split(sep)
            if(fields.count() == 8) {
                val id = fields[0].toInt()
                val key = fields[1].trim()
                val name = fields[2].trim()
                val desc = fields[3].trim()
                val cost = fields[4].toUInt()
                val start = fields[5].toBoolean()
                val evasion = fields[6].toInt()
                val damageSoak = fields[7].toInt()
                val defenseTech = DefenseTech(id, key, name, desc, cost, start, evasion, damageSoak)
                defenseTree.add(defenseTech)
            }
        }
    }

    suspend fun loadDrives() {
        val driveList = resourcesVfs["tech/drives.txt"].readLines(UTF8)
        //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
        val sep = "|"
        for (record in driveList) {
            var fields = record.split(sep)
            if(fields.count() == 7) {
                val id = fields[0].toInt()
                val key = fields[1].trim()
                val name = fields[2].trim()
                val desc = fields[3].trim()
                val cost = fields[4].toUInt()
                val start = fields[5].toBoolean()
                val speed = fields[6].toInt()
                val driveTech = PropulsionTech(id, key, name, desc, cost, start, speed)
                println("READ: ${id} ${name} ${desc} C ${cost} ${start} S ${speed}")
                propulsionTree.add(driveTech)
            }
        }
    }

    fun findTech(id: Int, realm: TechRealm) : Tech? {

        var foundTech : Tech? = null

        val tree = when(realm) {
            TechRealm.COMPUTERS -> computersTree
            TechRealm.WEAPONS -> weaponsTree
            TechRealm.DEFENSE -> defenseTree
            TechRealm.PROPULSION -> propulsionTree
        }

        for(tech in tree) {
            if(tech.id == id) {
                foundTech = tech
                break
            }
        }
        return foundTech
    }

    fun getUndiscoveredTechs(foundTechIds: List<Int>) : List<Tech> {
        val foundSet = foundTechIds.toSet()
        val undiscoveredTechs = mutableListOf<Tech>()

        undiscoveredTechs.addAll(computersTree.filter { tech -> tech.id !in foundSet })
        undiscoveredTechs.addAll(defenseTree.filter { tech -> tech.id !in foundSet })
        undiscoveredTechs.addAll(weaponsTree.filter { tech -> tech.id !in foundSet })
        undiscoveredTechs.addAll(propulsionTree.filter { tech -> tech.id !in foundSet })

        return undiscoveredTechs
    }
}
