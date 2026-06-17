
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

open class BasicScene() : Scene() {
    protected lateinit var gameFont: Font
    private var notEnoughDialog: Container? = null
    private var confirmationDialog: Container? = null
    private var showingNotEnough = false

    suspend fun loadBasicAssets() {
        gameFont = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
    }

    protected suspend fun Container.addDefaultBackground() : Image {
        return image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
    }

    protected suspend fun Container.addBackground(path: String) : Image {
        return image(resourcesVfs[path].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }
    }

    suspend fun showNoGo(requirements: String) {
        if (!showingNotEnough) {
            notEnoughDialog = sceneContainer.container {
                centerOnStage()

                roundRect(
                    sceneWidth / 2.0,
                    sceneHeight / 4.0,
                    5.0,
                    5.0,
                    Colors.BLACK
                )

                uiVerticalStack {
                    position(20.0, 20.0)
                    scaledWidth = sceneWidth / 2.0 - 40.0

                    text(requirements, 50.0, Colors.CYAN, gameFont)

                    uiButton("CLOSE") {
                        textFont =  gameFont
                        textColor = Colors.GOLD
                        onClick { closeMessage() }
                    }
                }
            }

            showingNotEnough = true
        }
    }

    suspend fun showConfirmDialog(msg: String) {
        confirmationDialog?.removeFromParent()

        val line1: String
        val line2: String

        if (msg.length > 40) {
            line1 = msg.substring(0, 40)
            line2 = msg.substring(40)
        } else {
            line1 = msg
            line2 = ""
        }

        confirmationDialog = sceneContainer.container {
            centerOnStage()

            roundRect(
                sceneWidth / 2.0,
                sceneHeight / 4.0,
                5.0,
                5.0,
                Colors.BLACK
            )

            uiVerticalStack {
                position(20.0, 20.0)
                scaledWidth = sceneWidth / 2.0 - 40.0

                text(line1, 20.0, Colors.CYAN, gameFont)

                if (line2.isNotBlank()) {
                    text(line2, 20.0, Colors.CYAN, gameFont)
                }

                uiButton("NO") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        confirmationDialog?.removeFromParent()
                        confirmationDialog = null
                    }
                }

                uiButton("YES") {
                    textFont = gameFont
                    textColor = Colors.GOLD
                    onClick {
                        confirmationDialog?.removeFromParent()
                        confirmationDialog = null
                        actionConfirmed()
                    }
                }
            }
        }
    }


    fun showHighlight(target: View, color: RGBA): RoundRect {
        val parent = target.parent
            ?: throw IllegalStateException("Cannot highlight a view with no parent")

        return parent.roundRect(
            target.scaledWidth + 8.0,
            target.scaledHeight + 8.0,
            rx = 10.0,
            ry = 10.0,
            fill = Colors.TRANSPARENT_BLACK,
            stroke = color,
            strokeThickness = 3.0
        ) {
            position(target.x - 4.0, target.y - 4.0)
        }
    }

    override suspend fun sceneBeforeLeaving() {
        showingNotEnough = false
        notEnoughDialog?.removeFromParent()
    }

    private fun closeMessage() {
        showingNotEnough = false
        notEnoughDialog?.removeFromParent()
        notEnoughDialog = null
    }

    open suspend fun actionConfirmed() {
    }
}
