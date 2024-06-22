class BonusCalculator(val es: EmpireState, val techTree: TechTree) {
    suspend fun getInitiativeBonus(side: Allegiance) : Int {
        var initiativeBonus = -1
        val techList = es.empires[side.ordinal]?.techTags
        if (techList != null) {
            for(techid in techList) {
                val tech = techTree.findTech(techid, TechRealm.COMPUTERS)
                if(tech != null) {
                    val computer = tech as ComputerTech
                    if(computer.initiative > initiativeBonus) {
                        initiativeBonus = computer.initiative
                    }
                }
            }
        }
        return initiativeBonus
    }

    suspend fun getAccuracyBonus(side: Allegiance) : Int {
        var accuracyBonus = -1
        val techList = es.empires[side.ordinal]?.techTags
        if (techList != null) {
            for(techid in techList) {
                val tech = techTree.findTech(techid, TechRealm.COMPUTERS)
                if(tech != null) {
                    val computer = tech as ComputerTech
                    if(computer.accuracy > accuracyBonus) {
                        accuracyBonus = computer.accuracy
                    }
                }
            }
        }
        return accuracyBonus
    }

    suspend fun getDamageCode(side: Allegiance) : Triple<Int,Int,String> {
        var lowDamage = -1
        var highDamage = -1
        var name = "Opgun"
        val techList = es.empires[side.ordinal]?.techTags
        if (techList != null) {
            for(techid in techList) {
                val tech = techTree.findTech(techid, TechRealm.WEAPONS)
                if(tech != null) {
                    val weapon = tech as WeaponsTech
                    if(weapon.lowDamage > lowDamage || weapon.highDamage > highDamage) {
                        lowDamage = weapon.lowDamage
                        highDamage = weapon.highDamage
                        name = weapon.name
                    }
                }
            }
        }
        return(Triple(lowDamage, highDamage, name))
    }

    suspend fun getEvasion(side: Allegiance) : Int {
        //This stacks unlike some other techs
        var evasionBonus = 0
        val techList = es.empires[side.ordinal]?.techTags
        if (techList != null) {
            for (techid in techList) {
                val tech = techTree.findTech(techid, TechRealm.DEFENSE)
                if (tech != null) {
                    val def = tech as DefenseTech
                    evasionBonus += def.evasion
                }
            }
        }
        return evasionBonus
    }

    suspend fun getDamageSoaked(side: Allegiance) : Int {
        var soak = 0
        val techList = es.empires[side.ordinal]?.techTags
        if (techList != null) {
            for (techid in techList) {
                val tech = techTree.findTech(techid, TechRealm.DEFENSE)
                if (tech != null) {
                    val def = tech as DefenseTech
                    soak += def.damageSoak
                }
            }
        }
        return soak
    }
}
