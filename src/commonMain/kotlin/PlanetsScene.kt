import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class PlanetsScene(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState) : Scene() {

    private val direction = mutableListOf<Boolean>()

    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        println("ACTIVE STAR: ${ps.activePlayerStar}")

        val startx = 200
        var starty = 600

        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }

        for((i, planet) in gs.stars[ps.activePlayerStar]!!.planets.values.withIndex())
        {
            val fileName = when( planet.type) {
                PlanetType.TOXIC -> "planets/planet1.png"
                PlanetType.OCEAN -> "planets/planet2.png"
                PlanetType.TERRAN -> "planets/planet3.png"
                PlanetType.DESSERT -> "planets/planet4.png"
                PlanetType.VOLCANIC -> "planets/planet5.png"
                PlanetType.BARREN -> "planets/planet6.png"
                PlanetType.SUPERTERRAN -> "planets/planet7.png"
                PlanetType.TROPICAL -> "planets/planet10.png"
            }
            val planetImage = image(resourcesVfs[fileName].readBitmap()) {
                scale(0.5)
                position(startx, starty)
            }
            direction.add(i, false)
            planetImage.addUpdater { updatePlanet(planetImage,i) }
            planetImage.onClick { planetClicked(i, font)  }

            val planetTextColor = when(planet.ownerIndex)
            {
                Allegiance.Unoccupied -> Colors.WHITE
                Allegiance.Player -> Colors.CYAN
                Allegiance.Enemy -> Colors.RED
            }

            val planetTxt = "${planet.name} - ${planet.type} "
            text( planetTxt, 50.00, planetTextColor, font)
            {
                centerXOn(planetImage)
                alignTopToTopOf(planetImage, 12.0)
            }
            starty -= 200
          }

        val fileName = when( gs.stars[ps.activePlayerStar]!!.type) {
            StarType.YELLOW -> "stars/Star cK gK eg9.bmp"
            StarType.BLUE -> "stars/Star B supeg5.bmp"
            StarType.RED -> "stars/Star M supeg5.bmp"
        }
        val starImage = image(resourcesVfs[fileName].readBitmap()) {
            scale(0.5)
            position( width/2, 800.00)
        }
        text( gs.stars[ps.activePlayerStar]!!.name, 50.00, Colors.CYAN, font)
        {
            centerXOn(starImage)
            alignTopToTopOf(starImage, 12.0)
        }

        text("BACK", 50.00,Colors.GOLD, font)
        {
            onClick { sceneContainer.changeTo<StarsScene>() }
            position(300, 0)
            centerXOn(background)
        }

    }

    private suspend fun Container.planetClicked(index: Int, font: Font)
    {
        if(gs.stars[ps.activePlayerStar]!!.planets[index]!!.ownerIndex == Allegiance.Player)
        {
            ps.activePlayerPlanet = index; sceneContainer.changeTo<PlanetScene>()
        }
        //else
        //{
        //   text("NOT YOUR WORLD", 50.00, Colors.GOLD, font)
        //}

    }


    private fun updatePlanet(planet: Image, index: Int)
    {
        if(planet.x >= sceneWidth - planet.width) {
            direction[index] = true
        }
        if(planet.x <= 0 )
        {
            direction[index] = false
        }
        if(direction[index])
        {
            planet.x -= (index + 1)
        } else
        {
            planet.x += (index + 1)
        }
    }
}
