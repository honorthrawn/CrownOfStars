
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class LoseFleetCombatScene(val gs: GalaxyState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneMain() {
        loadBasicAssets()
        val background = addBackground("ui/fleetdefeated.jpg")

        val header = text(
            "Battle at ${gs.stars[ps.activePlayerStar]!!.name} Rounds: ${ps.totalRounds}",
            25.00,
            Colors.GOLD,
            gameFont
        ) {
            alignTopToTopOf(background)
            centerXOnStage()
        }
        val line1 = text("We lost the battle", 25.00, Colors.CYAN, gameFont) {
            alignTopToBottomOf(header)
            centerXOnStage()
        }
        val line2 =
            text("We lost ${ps.shipsLost} ships from ${ps.totalDamgeReceived} damage", 25.00, Colors.CYAN, gameFont) {
                alignTopToBottomOf(line1)
                centerXOnStage()
            }
        val line3 = text(
            "We destroyed ${ps.enemyShipsDestroyed} ships from ${ps.totalDamageDealt} damage",
            25.00,
            Colors.CYAN,
            gameFont
        ) {
            alignTopToBottomOf(line2)
            centerXOnStage()
        }

        uiButton("CLOSE") {
            alignTopToBottomOf(line3)
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = gameFont
            onClick { ps.musicSceneContainer?.changeTo<MusicScene>()
                sceneContainer.changeTo<StarsScene>() }
        }
    }
}
