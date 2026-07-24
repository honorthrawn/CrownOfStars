
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class UnexploredScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addBackground("ui/unexploredSpace.png")
        text("UNEXPLORED SYSTEM", textSize = 36.0, Colors.CYAN, gameFont) {
              centerXOnStage()
                y = 220.0
        }

            text("Send ships to reveal this system.", textSize = 20.0, Colors.CYAN, gameFont) {
                centerXOnStage()
                y = 280.0
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = gameFont
                onClick { sceneContainer.changeTo<StarsScene>() }
            }
        }
    }
