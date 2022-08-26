import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korma.geom.*
import com.soywiz.korinject.*
import kotlin.reflect.*

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val size = SizeInt(768, 1024) // Virtual Size
    //override val windowSize = SizeInt(768, 1024) // Window Size
    override val windowSize = SizeInt(2000, 1200) // Window Size
    override val title = "Crown of Stars"

    override val mainScene: KClass<out Scene> = MainMenu::class

    override suspend fun AsyncInjector.configure() {
        mapInstance(GalaxyState())
        mapInstance(EmpireState())
        mapInstance(PlayerState())
        mapPrototype { MainMenu(get(),get()) }
        mapPrototype { PlanetsScene(get(),get(),get()) }
        mapPrototype { PlanetScene(get(),get(),get()) }
        mapPrototype { StarsScene(get(),get(),get()) }
    }
}

