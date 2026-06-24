import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class YouLostScene() : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addFittedBackground("ui/militarydefeat.png", 32.00)
        //ps.musicSceneContainer?.changeTo<WarMusicScene>()
        val yPos = 0.00
        uiButton("DONE") {
            y = yPos
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {
                sceneContainer.changeTo<MainMenu>()
            }
        }
    }
}
