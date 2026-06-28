
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import kotlin.math.*

open class BasicScene() : Scene() {
    protected lateinit var gameFont: Font
    private lateinit var brushedMetalTitleBar: Bitmap
    private var notEnoughDialog: Container? = null
    private var showingNotEnough = false

    suspend fun loadBasicAssets() {
        gameFont = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        brushedMetalTitleBar = resourcesVfs["ui/brushedMetal.png"].readBitmap()
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

    protected suspend fun Container.addFittedBackground(path: String, padding: Double = 0.0) : Image {
        val bitmap = resourcesVfs[path].readBitmap()
        return image(bitmap) {
            val availableWidth = sceneWidth.toDouble() - padding * 2.0
            val availableHeight = sceneHeight.toDouble() - padding * 2.0
            val scaleFactor = min(
                availableWidth / bitmap.width,
                availableHeight / bitmap.height
            )

            scale(scaleFactor)
            position(
                (sceneWidth.toDouble() - bitmap.width * scaleFactor) / 2.0,
                (sceneHeight.toDouble() - bitmap.height * scaleFactor) / 2.0
            )
        }
    }

    fun showGameDialog(
        title: String,
        width: Double = sceneWidth / 2.0,
        height: Double = sceneHeight / 3.0,
        block: Container.(dialog: Container) -> Unit
    ): Container {
        val dialog = sceneContainer.container()

        val x = (sceneWidth - width) / 2.0
        val y = (sceneHeight - height) / 2.0

        dialog.position(x, y)

        // Shadow
        dialog.roundRect(width, height, 12.0, 12.0, Colors["#00000088"]) {
            position(6.0, 6.0)
        }

        // Main window body
        dialog.roundRect(width, height, 12.0, 12.0, Colors["#202838"])

        dialog.addBrushedMetalTitleBar(width)

        // Title text
        dialog.text(title, 28.0, Colors.DARKCYAN, gameFont) {
            position(20.0, 10.0)
        }

        // Content area
        val content = dialog.container {
            position(20.0, 65.0)
        }

        content.block(dialog)

        return dialog
    }

    private fun Container.addBrushedMetalTitleBar(width: Double) {
        val titleHeight = 46.0

        roundRect(width, titleHeight, 12.0, 12.0, Colors["#34404D"])
        image(brushedMetalTitleBar) {
            position(8.0, 4.0)
            setSizeScaled(width - 16.0, titleHeight - 9.0)
        }
        solidRect(width - 16.0, 1.0, Colors["#E6EEF666"]) {
            position(8.0, 4.0)
        }
        solidRect(width - 16.0, 2.0, Colors["#0A0E14AA"]) {
            position(8.0, titleHeight - 3.0)
        }
    }

    fun showChooseResearchRealmDialog(es: EmpireState, ps: PlayerState): Container {
        val playerEmpire = es.empires[Allegiance.Player.ordinal]
            ?: error("Player empire was not found")

        suspend fun chooseRealm(realm: TechRealm, dialog: Container) {
            ps.techRealmChosen = realm
            dialog.removeFromParent()
            sceneContainer.changeTo<BuyTechScene>()
        }

        return showGameDialog(
            title = "CHOOSE RESEARCH",
            width = 360.0,
            height = 430.0
        ) { dialog ->
            text("Research: ${playerEmpire.researchPoints}", 28.0, Colors.CYAN, gameFont) {
                position(0.0, 0.0)
            }

            uiVerticalStack {
                position(0.0, 48.0)
                padding = 12.0

                uiButton("COMPUTERS", width = 220.0, height = 45.0) {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { chooseRealm(TechRealm.COMPUTERS, dialog) }
                }

                uiButton("WEAPONS", width = 220.0, height = 45.0) {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { chooseRealm(TechRealm.WEAPONS, dialog) }
                }

                uiButton("DEFENSE", width = 220.0, height = 45.0) {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { chooseRealm(TechRealm.DEFENSE, dialog) }
                }

                uiButton("PROPULSION", width = 220.0, height = 45.0) {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { chooseRealm(TechRealm.PROPULSION, dialog) }
                }

                uiButton("CLOSE", width = 220.0, height = 45.0) {
                    textColor = Colors.GOLD
                    textFont = gameFont
                    onClick { dialog.removeFromParent() }
                }
            }
        }
    }

    suspend fun showNoGo(requirements: String) {
         showGameDialog("Operation Invalid", sceneWidth / 2.0,sceneHeight / 4.0) { dialog ->
                uiVerticalStack {
                    scaledWidth = sceneWidth / 2.0 - 40.0
                    text(requirements, 40.0, Colors.CYAN, gameFont)

                    uiButton("CLOSE") {
                        textFont =  gameFont
                        textColor = Colors.GOLD
                        onClick { dialog.removeFromParent() }
                    }
                }
            }
    }

    suspend fun showConfirmDialog(message: String): Boolean {
        val result = CompletableDeferred<Boolean>()

        showGameDialog(
            title = "CONFIRM",
            width = sceneWidth / 2.0,
            height = sceneHeight / 3.0
        ) { dialog ->

            text(message, 24.0, Colors.WHITE, gameFont) {
                position(0.0, 0.0)
            }

            uiHorizontalStack {
                position(0.0, 100.0)

                uiButton("YES") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        dialog.removeFromParent()

                        if (!result.isCompleted) {
                            result.complete(true)
                        }
                    }
                }

                uiButton("NO") {
                    textFont = gameFont
                    textColor = Colors.GOLD

                    onClick {
                        dialog.removeFromParent()

                        if (!result.isCompleted) {
                            result.complete(false)
                        }
                    }
                }
            }
        }

        return result.await()
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
}
