import kotlinx.serialization.*

/*
enum class TechRealm {
    COMPUTERS,
    WEAPONS,
    DEFENSE,
    PROPULSION
}*/

sealed class Tech() {
  //  abstract var realm: TechRealm
    abstract var id: Int
    abstract var name: String
    abstract var description: String
    abstract var cost: UInt
    abstract var starting: Boolean
}

@Serializable
data class ComputerTech(
    override var id: Int,
    override var name: String, override var description: String,
    override var cost: UInt, override var starting: Boolean, var accuracy: Int, var initiative: Int)
    : Tech()


@Serializable
data class WeaponsTech(
    override var id: Int,
    override var name: String, override var description: String,
    override var cost: UInt, override var starting: Boolean, var lowDamage: Int, var highDamage: Int)
    : Tech()


@Serializable
data class DefenseTech(
    override var id: Int,
    override var name: String, override var description: String,
    override var cost: UInt, override var starting: Boolean, var evasion: Int, var damageSoak: Int)
    : Tech()


@Serializable
data class PropulsionTech(
    override var id: Int,
    override var name: String, override var description: String,
    override var cost: UInt, override var starting: Boolean, var speed: Int)
    : Tech()




