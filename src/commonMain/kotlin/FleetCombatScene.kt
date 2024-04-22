
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class FleetCombatScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : BasicScene() {
    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/nebula.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val header = text("Battle at ${gs.stars[ps.activePlayerStar]!!.name}", 50.00, Colors.CYAN, font)  {
            alignTopToTopOf(background)
            centerXOnStage()
        }

        val usefulHeight = sceneHeight.toDouble() - header.scaledHeight

        uiVerticalStack {
            alignTopToBottomOf(header)
            positionX(150 )
            padding = 25.00
            scaledHeight = usefulHeight

            image(resourcesVfs["ships/Human-Spacestation.png"].readBitmap()) {
                //setSizeScaled(50.0, 50.0)
                // rotation(Angle.Companion.fromDegrees(90))
                //scaledHeight = usefulHeight / 7.0
            }

            image(resourcesVfs["ships/Human-Battlecruiser.png"].readBitmap()) {
                //etSizeScaled(50.0, 50.0)
                //rotation(Angle.Companion.fromDegrees(90))
                //scale(0.25, 0.25)
                //scaledHeight = usefulHeight / 7.0
            }

            image(resourcesVfs["ships/Human-Corvette.png"].readBitmap()) {
                //setSizeScaled(50.0, 50.0)
                //rotation(Angle.Companion.fromDegrees(90))
                //scale(0.25, 0.25)
                //scaledHeight = usefulHeight / 7.0
            }

            image(resourcesVfs["ships/Human-Cruiser.png"].readBitmap()) {
               // setSizeScaled(50.0, 50.0)
                //rotation(Angle.Companion.fromDegrees(90))
                //scale(0.25, 0.25)
                //scaledHeight = usefulHeight / 7.0
            }

            image(resourcesVfs["ships/Human-Battleship.png"].readBitmap()) {
               // setSizeScaled(50.0, 50.0)
               // rotation(Angle.Companion.fromDegrees(90))
                //scale(0.25, 0.25)
                //scaledHeight = usefulHeight / 7.0
            }

            image(resourcesVfs["ships/Human-Frigate.png"].readBitmap()) {
               // setSizeScaled(50.0, 50.0)
              //  rotation(Angle.Companion.fromDegrees(90))
                //scale(0.25, 0.25)
               // scaledHeight = usefulHeight / 7.0
            }
        }

        uiVerticalStack {
           // alignTopToBottomOf(header)
            alignRightToRightOf(background)
            //padding = 20.00
          //  scaledHeight = usefulHeight
            image(resourcesVfs["ships/Frigate.png"].readBitmap()) {
                scale(0.5, 0.5)
            }

            image(resourcesVfs["ships/Corvette.png"].readBitmap()) {
                scale(0.5, 0.5)
            }

            image(resourcesVfs["ships/Cruiser.png"].readBitmap()) {
                scale(0.5, 0.5)
            }

            image(resourcesVfs["ships/Battleship.png"].readBitmap()) {
                scale(0.5, 0.5)
            }

            image(resourcesVfs["ships/Destroyer.png"].readBitmap()) {
                scale(0.5, 0.5)
            }
        }
    }
}
