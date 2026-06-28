import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class StatsScene(val gs: GalaxyState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addBackground(ps.determineCrown())

        var yPos = 0.00
        val padding = 8.00

        text("FINAL STATISTICS", 25.00, Colors.GOLD, gameFont) {
            y = yPos
            yPos += height + padding
            centerXOnStage()
        }

        yPos += padding
        yPos = addStatistics(yPos, padding)

        uiButton("DONE") {
            y = yPos + padding
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick {
                sceneContainer.changeTo<MainMenu>()
            }
        }
    }

    private fun SContainer.addStatistics(startY: Double, padding: Double): Double {
        var yPos = startY
        val lines = listOf(
            "Turns / years elapsed: ${gs.turnsElapsed()}",
            "Total damage dealt: ${ps.totalDamageDealt}",
            "Total damage received: ${ps.totalDamageRecieved}",
            "Total ships lost: ${ps.totalShipsLost}",
            "Total enemy ships destroyed: ${ps.totalEnemyShipsDestroyed}",
            "Colonists lost: ${ps.colonistsLost}",
            "Regiments lost: ${ps.regimentsLost}",
            "Enemy population killed: ${ps.enemyPopulationKilled}",
            "Black holes created by player: ${ps.blackHolesCreatedByPlayer}",
            "Black holes created by enemy: ${ps.blackHolesCreatedByEnemy}",
            "Colonies lost: ${ps.coloniesLost}",
            "Colonies established: ${ps.coloniesEstablished}"
        )

        lines.forEach { line ->
            text(line, 19.00, Colors.CYAN, gameFont) {
                y = yPos
                yPos += height + padding
                centerXOnStage()
            }
        }

        return yPos
    }
}
