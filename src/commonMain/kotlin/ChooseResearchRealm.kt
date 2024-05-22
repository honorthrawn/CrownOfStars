
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ChooseResearchRealm(val es: EmpireState, val ps: PlayerState) : BasicScene() {
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
                onClick {
                    ps.techRealmChosen = TechRealm.COMPUTERS
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("WEAPONS") {
                textColor = Colors.GOLD
                textFont = font
                onClick {
                    ps.techRealmChosen = TechRealm.WEAPONS
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("DEFENSE") {
                textColor = Colors.GOLD
                textFont = font
                onClick {
                    ps.techRealmChosen = TechRealm.DEFENSE
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("PROPULSION") {
                textColor = Colors.GOLD
                textFont = font
                onClick {
                    ps.techRealmChosen = TechRealm.PROPULSION
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>()
                }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
            }
        }
    }
}
