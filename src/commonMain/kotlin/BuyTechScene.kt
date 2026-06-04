import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class BuyTechScene(
    val es: EmpireState,
    val techTree: TechTree,
    val ps: PlayerState
) : BasicScene() {

    private lateinit var researchPoints: Text
    private var selectedTech: Tech? = null

    private lateinit var techListStack: Container
    private lateinit var detailsStack: Container

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        val tree = getCurrentTechTree()

        image(resourcesVfs[getRealmImagePath()].readBitmap()) {
            position(0.0, 0.0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val playerEmpire = es.empires[Allegiance.Player.ordinal] ?: error("Player empire was not found")
        val leftX = 60.0
        val rightX = 500.0
        val topY = 125.0
        val bottomY = sceneHeight - 160.0

        text(getRealmTitle(), 40.0, Colors.GOLD, font) {
            position(leftX, 30.0)
        }

        techListStack = container {
            position(leftX, topY)
        }

        detailsStack = uiVerticalStack {
            position(rightX, topY)
            padding = 12.0
        }

        buildTechList(tree, font)

        researchPoints = text("Research Points left: ${playerEmpire.researchPoints}", 25.0, Colors.CYAN, font) {
            position(leftX, bottomY - 70.0)
        }

        uiHorizontalStack {
            position(leftX, bottomY - 30.0)
            padding = 20.0

            uiButton("BUY SELECTED", width = 170.0, height = 45.0) {
                textColor = Colors.GOLD
                textFont = font
                onClick {
                    val tech = selectedTech
                    if (tech == null) {
                        showNoGo("Select a technology first.")
                    } else {
                        buyTech(tech)
                    }
                }
            }

            uiButton("BACK", width = 120.0, height = 45.0) {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<ChooseResearchRealm>() }
            }

            uiButton("MAP", width = 120.0, height = 45.0) {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>() }
            }
        }

        val firstAvailable = tree.firstOrNull { tech -> !playerEmpire.techTags.contains(tech.id) }
        if (firstAvailable != null) {
            selectTech(firstAvailable, font)
        } else {
            showDetailsMessage("No available technologies in this realm.", font)
        }
    }

    private fun getCurrentTechTree(): List<Tech> {
        return when (ps.techRealmChosen) {
            TechRealm.COMPUTERS -> techTree.computersTree
            TechRealm.WEAPONS -> techTree.weaponsTree
            TechRealm.DEFENSE -> techTree.defenseTree
            TechRealm.PROPULSION -> techTree.propulsionTree
        }
    }

    private fun getRealmTitle(): String {
        return when (ps.techRealmChosen) {
            TechRealm.COMPUTERS -> "Computer Research"
            TechRealm.WEAPONS -> "Weapons Research"
            TechRealm.DEFENSE -> "Defense Research"
            TechRealm.PROPULSION -> "Propulsion Research"
        }
    }

    private fun getRealmImagePath(): String {
        return when (ps.techRealmChosen) {
            TechRealm.COMPUTERS -> "tech/futureComputer.jpg"
            TechRealm.WEAPONS -> "tech/weapon.jpg"
            TechRealm.DEFENSE -> "tech/armor.jpg"
            TechRealm.PROPULSION -> "tech/propulsion.jpg"
        }
    }

    private fun buildTechList(tree: List<Tech>, font: Font) {
        val playerEmpire = es.empires[Allegiance.Player.ordinal] ?: error("Player empire was not found")

        techListStack.removeChildren()

        var rowY = 0.0

        for (tech in tree) {
            if (!playerEmpire.techTags.contains(tech.id)) {
                techListStack.uiButton(shortenTechName(tech.name)) {
                    position(0.0, rowY)
                    textColor = Colors.CYAN
                    textFont = font
                    onClick {
                        selectTech(tech, font)
                    }
                }

                rowY += 65.0
            }
        }
    }

    private fun shortenTechName(name: String): String {
        return if (name.length <= 15) {
            name
        } else {
            name.take(15) + "..."
        }
    }

    private fun selectTech(tech: Tech, font: Font) {
        selectedTech = tech
        detailsStack.removeChildren()

        for (line in getTechDetails(tech)) {
            for (wrappedLine in wrapText(line, 28)) {
                detailsStack.text(wrappedLine, 24.0, Colors.GOLD, font)
            }
        }
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""

        for (word in words) {
            val candidate = if (current.isBlank()) word else "$current $word"

            if (candidate.length > maxChars) {
                if (current.isNotBlank()) lines.add(current)
                current = word
            } else {
                current = candidate
            }
        }

        if (current.isNotBlank()) lines.add(current)

        return lines
    }

    private fun showDetailsMessage(message: String, font: Font) {
        detailsStack.removeChildren()
        detailsStack.text(message, 25.0, Colors.GOLD, font)
    }

    private fun getTechDetails(tech: Tech): List<String> {
        val lines = mutableListOf<String>()

        lines.add(tech.name)
        lines.add(tech.description)
        lines.add("Cost: ${tech.cost}")

        when (tech) {
            is ComputerTech -> {
                lines.add("Accuracy: ${tech.accuracy}")
                lines.add("Initiative: ${tech.initiative}")
            }

            is WeaponsTech -> {
                lines.add("Damage: ${tech.lowDamage} to ${tech.highDamage}")
            }

            is DefenseTech -> {
                lines.add("Absorbs: ${tech.damageSoak}")
                lines.add("Evasion: ${tech.evasion}")
            }

            is PropulsionTech -> {
                lines.add("Speed: ${tech.speed}")
            }
        }

        return lines
    }

    private suspend fun buyTech(tech: Tech) {
        val playerEmpire = es.empires[Allegiance.Player.ordinal]
            ?: error("Player empire was not found")

        if (!playerEmpire.canBuyTech(tech)) {
            showNoGo("Not enough Research to buy this!")
        } else {
            selectedTech = tech
            showConfirmDialog("Buy ${tech.name} for ${tech.cost} research?")
        }
    }

    override suspend fun actionConfirmed() {
        val tech = selectedTech ?: return

        val playerEmpire = es.empires[Allegiance.Player.ordinal]
            ?: error("Player empire was not found")

        playerEmpire.buyTech(tech)
        researchPoints.text = "Research Points left: ${playerEmpire.researchPoints}"

        // Simple refresh. Since this scene is not huge, reloading it is easiest and safest.
        sceneContainer.changeTo<BuyTechScene>()
    }
}
