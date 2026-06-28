import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class RagnarokAftermathScene(val gs: GalaxyState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()

        solidRect(sceneWidth, sceneHeight, Colors.BLACK)
        val blackHole = addFittedBackground("stars/black_hole.png", 120.0)
        blackHole.alpha = 0.55

        val starName = gs.stars[ps.activePlayerStar]!!.name
        val crawlLines = listOf(
            "The star screams without sound.",
            "",
            "The colonies vanish",
            "in a flash of impossible gravity.",
            "",
            "Where $starName once burned,",
            "only a wound remains."
        )

        val fontSize = 28.0
        val lineGap = 12.0
        val sectionGap = 34.0
        val crawl = container()
        var yPos = 0.0

        crawlLines.forEach { line ->
            if (line.isBlank()) {
                yPos += sectionGap
            } else {
                val text = crawl.text(
                    line,
                    textSize = fontSize,
                    color = Colors["#FFD8D8"],
                    font = gameFont,
                    autoScaling = false
                )

                text.position((sceneWidth.toDouble() - text.width) / 2.0, yPos)
                yPos += fontSize + lineGap
            }
        }

        crawl.position(0.0, scaledHeight - fontSize - 150.0)
        crawl.addUpdater { dt ->
            crawl.position(crawl.x, crawl.y - 28.0 * dt.seconds)
        }

        uiButton("CONTINUE", width = 150.0, height = 48.0) {
            textFont = gameFont
            textColor = Colors.GOLD
            position(48.0, 48.0)
            onClick {
                sceneContainer.changeTo<StarsScene>()
            }
        }
    }
}
