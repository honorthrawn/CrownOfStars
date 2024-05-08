
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ChooseResearchRealm(val es: EmpireState) : BasicScene() {
    private lateinit var researchPoints: Text

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/chooseResearch.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        uiVerticalStack {
            alignTopToTopOf(background)
            padding = 50.00
            scaledWidth = sceneWidth.toDouble()

            text("Choose realm to buy techs from next", 50.00, Colors.GOLD, font) {
            }

            uiButton("COMPUTERS") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<ComputerResearchScene>() }
            }

            uiButton("WEAPONS") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<WeaponsResearchScene>()  }
            }

            uiButton("DEFENSE") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<DefenseResearchScene>()  }
            }

            uiButton("PROPULSION") {
                textColor = Colors.GOLD
                textFont = font
               //  onClick {  }
            }


            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>() }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
            }
        }
    }
}
