
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korinject.*
import com.soywiz.korma.geom.*
import kotlin.reflect.*

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val size = SizeInt(768, 1024) // Virtual Size
    //override val windowSize = SizeInt(768, 1024) // Window Size
    override val windowSize = SizeInt(2000, 1200) // Window Size
    override val title = "Crown of Stars"
    override val mainScene: KClass<out Scene> = MainMenu::class
    override val icon = "ui/CrownOfStars.jpg"

    override suspend fun AsyncInjector.configure() {
        mapInstance(GalaxyState())
        mapInstance(EmpireState())
        mapInstance(PlayerState())
        mapInstance(MusicPlayer())
        mapInstance(AICore(get(), get()))
        mapPrototype { MainMenu(get(), get(), get(), get()) }
        mapPrototype { PlanetsScene(get(), get(), get(), get()) }
        mapPrototype { PlanetScene(get(), get(), get(), get()) }
        mapPrototype { StarsScene(get(), get(), get(), get(), get()) }
        mapPrototype { BuyShipScene(get(), get(), get(), get()) }
        mapPrototype { DeployShipsScene(get(), get(), get(), get()) }
        mapPrototype { ColonyScene(get(), get(), get(), get()) }
        mapPrototype { terraformingScene(get(), get(), get(), get()) }
        mapPrototype { CreditsScene(get()) }
    }

}

