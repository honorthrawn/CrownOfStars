
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class ChooseResearchRealm(val es: EmpireState, val ps: PlayerState) : BasicScene() {
    private lateinit var researchPoints: Text

    override suspend fun SContainer.sceneMain() {
        loadBasicAssets()
        addBackground("ui/chooseResearch.jpg")

        uiVerticalStack {
            centerXOnStage()
            centerYOnStage()
            padding = 50.00
            scaledWidth = sceneWidth.toDouble()

            text("Choose realm to buy techs from next", 50.00, Colors.GOLD, gameFont) {
            }

            uiButton("COMPUTERS") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.techRealmChosen = TechRealm.COMPUTERS
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("WEAPONS") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.techRealmChosen = TechRealm.WEAPONS
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("DEFENSE") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.techRealmChosen = TechRealm.DEFENSE
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("PROPULSION") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick {
                    ps.techRealmChosen = TechRealm.PROPULSION
                    sceneContainer.changeTo<BuyTechScene>()
                }
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick { sceneContainer.changeTo<StarsScene>()
                }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
                font = gameFont
            }
        }
    }
}
