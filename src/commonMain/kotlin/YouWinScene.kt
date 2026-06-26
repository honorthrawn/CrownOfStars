import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class YouWinScene(val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addBackground(ps.determineCrown())
        //ps.musicSceneContainer?.changeTo<WarMusicScene>()
        var yPos = 0.00
        val padding = 10.00

        text(
            "YOU WON",
            25.00,
            Colors.GOLD,
            gameFont
        ) {
            y = yPos
            yPos += (height + padding)
            centerXOnStage()
        }

        text(
            "Through wisdom you won the Steward Crown",
            25.00,
            Colors.GOLD,
            gameFont
        ) {
            y = yPos
            yPos += (height + padding)
            centerXOnStage()
        }

        uiButton("STATS") {
            y = yPos
            yPos += (height + padding)
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {
                sceneContainer.changeTo<MainMenu>()
            }
        }

        uiButton("DONE") {
            y = yPos
            yPos += (height + padding)
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {
                sceneContainer.changeTo<MainMenu>()
            }
        }
    }
}
