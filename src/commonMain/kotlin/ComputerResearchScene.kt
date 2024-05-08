
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class ComputerResearchScene(val es: EmpireState, val techTree: TechTree)  : BasicScene() {

    private lateinit var researchPoints: Text
    private lateinit var computerBought: ComputerTech

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/chooseResearch.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        uiVerticalStack {
            alignTopToTopOf(background)
            centerXOnStage()
            centerYOnStage()
            padding = 50.00

            for (computer in techTree.computersTree) {
                uiButton(computer.name) {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buyTech(computer) }
                }
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<ChooseResearchRealm>() }
            }

            uiButton("MAP") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>() }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
            }

        }
    }

    suspend fun buyTech(computer: ComputerTech) {
        if(!es.empires[Allegiance.Player.ordinal]?.canBuyTech(computer)!!) {
            showNoGo("Not enough Research to buy this!")
        } else{
            //, resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
            computerBought = computer
            showConfirmDialog(computer.description)
        }
    }

    override fun actionConfirmed() {
        println("clicked!")
        //  val message = "${computer.name} ${computer.description} Bonus: ${computer.accuracy} ${computer.initiative}"
        es.empires[Allegiance.Player.ordinal]?.buyTech(computerBought)!!
        researchPoints.text = "Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}"
    }
}
