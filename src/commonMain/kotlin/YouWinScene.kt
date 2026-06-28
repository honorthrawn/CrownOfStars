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

        yPos += padding
        yPos = addFinalStatistics(yPos, padding)

        uiButton("STATS") {
            y = yPos
            yPos += (height + padding)
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {
                sceneContainer.changeTo<StatsScene>()
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

    private fun SContainer.addFinalStatistics(startY: Double, padding: Double): Double {
        var yPos = startY
        val lines = listOf(
            "FINAL STATISTICS",
            "Enemy population killed: ${ps.enemyPopulationKilled}",
            "Black holes created by player: ${ps.blackHolesCreatedByPlayer}",
            "Black holes created by enemy: ${ps.blackHolesCreatedByEnemy}",
            "Colonies lost: ${ps.coloniesLost}",
            "Colonies established: ${ps.coloniesEstablished}"
        )

        lines.forEachIndexed { index, line ->
            text(line, 21.00, if (index == 0) Colors.GOLD else Colors.CYAN, gameFont) {
                y = yPos
                yPos += height + padding
                centerXOnStage()
            }
        }

        return yPos
    }
}
