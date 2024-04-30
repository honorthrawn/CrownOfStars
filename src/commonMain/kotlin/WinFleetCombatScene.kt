
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class WinFleetCombatScene(val gs: GalaxyState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/fleetVictorious.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val header = text(
            "Battle at ${gs.stars[ps.activePlayerStar]!!.name} Rounds: ${ps.totalRounds}",
            25.00,
            Colors.GOLD,
            font
        ) {
            alignTopToTopOf(background)
            centerXOnStage()
        }
        val line1 = text("We won the battle", 25.00, Colors.CYAN, font) {
            alignTopToBottomOf(header)
            centerXOnStage()
        }
        val line2 =
            text("We lost ${ps.shipsLost} ships from ${ps.totalDamgeReceived} damage", 25.00, Colors.CYAN, font) {
                alignTopToBottomOf(line1)
                centerXOnStage()
            }
        val line3 = text(
            "We destroyed ${ps.enemyShipsDestroyed} ships from ${ps.totalDamageDealt} damage",
            25.00,
            Colors.CYAN,
            font
        ) {
            alignTopToBottomOf(line2)
            centerXOnStage()
        }

        uiButton("CLOSE") {
            alignTopToBottomOf(line3)
            centerXOnStage()
            textColor = Colors.GOLD
            textFont = font
            onClick { ps.musicSceneContainer?.changeTo<MusicScene>()
                sceneContainer.changeTo<StarsScene>() }
        }
    }
}
