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
    }

    suspend fun loadComputers() {
        val computerList = resourcesVfs["tech/computers.txt"].readLines(UTF8)
        //Don't know why couldn't get tab to work but \t and \\t didn't work.   So, I just decided to use |
        val sep = "|"
        for (record in computerList) {
            var fields = record.split(sep)
            if(fields.count() == 7) {
                val id = fields[0].toInt()
                val name = fields[1].trim()
                val desc = fields[2].trim()
                val cost = fields[3].toUInt()
                val start = fields[4].toBoolean()
                val accuracy = fields[5].toInt()
                val initiative = fields[6].toInt()
                val computerTech = ComputerTech(id, name, desc, cost, start, accuracy, initiative)
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
            if(fields.count() == 7) {
                val id = fields[0].toInt()
                val name = fields[1]
                val desc = fields[2]
                val cost = fields[3].toUInt()
                val start = fields[4].toBoolean()
                val lowDamage = fields[5].toInt()
                val highDamage = fields[6].toInt()
                val weaponTech = WeaponsTech(id, name, desc, cost, start, lowDamage, highDamage)
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
            if(fields.count() == 7) {
                val id = fields[0].toInt()
                val name = fields[1]
                val desc = fields[2]
                val cost = fields[3].toUInt()
                val start = fields[4].toBoolean()
                val evasion = fields[5].toInt()
                val damageSoak = fields[6].toInt()
                val defenseTech = DefenseTech(id, name, desc, cost, start, evasion, damageSoak)
                defenseTree.add(defenseTech)
            }
        }
    }

}
