
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*


class TechInfoScene(val techTree: TechTree, val ps: PlayerState)  : BasicScene() {

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val imageName = when (ps.techRealmChosen) {
            TechRealm.COMPUTERS -> "tech/futureComputer.jpg"
            TechRealm.WEAPONS -> "tech/weapon.jpg"
            TechRealm.DEFENSE -> "tech/armor.jpg"
            TechRealm.PROPULSION -> "tech/propulsion.jpg"
        }

        val background = image(resourcesVfs[imageName].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val tech = techTree.findTech(ps.techQueryId, ps.techRealmChosen)
        if (tech != null) {
            val costMessage = "Cost: ${tech.cost}"
            uiVerticalStack {
                alignTopToTopOf(background)
                padding = 50.00

                text(tech.name, 30.00, Colors.GOLD, font)
                text(tech.description, 30.00, Colors.GOLD, font)
                text(costMessage, 30.00, Colors.GOLD, font)
                when(ps.techRealmChosen) {
                    TechRealm.COMPUTERS -> {
                        val computer = tech as ComputerTech
                        val initMessage = "Initiative: ${computer.initiative}"
                        val accuraayMessage = "Accuracy: ${computer.accuracy}"
                        text(initMessage, 30.00, Colors.GOLD, font)
                        text(accuraayMessage, 30.00, Colors.GOLD, font)
                    }
                    TechRealm.WEAPONS -> {
                        val weapon = tech as WeaponsTech
                        val damageMessage = "Damage: ${weapon.lowDamage} to ${weapon.highDamage}"
                        text(damageMessage, 30.00, Colors.GOLD, font)
                    }
                    TechRealm.DEFENSE -> {
                        val defense = tech as DefenseTech
                        val soakMessage = "Absorbs: ${defense.damageSoak}"
                        val evadeMesage = "Evasion: ${defense.evasion}"
                        text(soakMessage, 30.00, Colors.GOLD, font)
                        text(evadeMesage, 30.00, Colors.GOLD, font)
                    }
                    TechRealm.PROPULSION -> {
                        val drive = tech as PropulsionTech
                        val message = "Speed: ${drive.speed}"
                        text(message, 30.00, Colors.GOLD, font)
                    }
                }
                uiButton("BACK") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { sceneContainer.changeTo<BuyTechScene>() }
                }
                uiButton("MAP") {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { sceneContainer.changeTo<StarsScene>() }
                }
            }

        }
    }

}
