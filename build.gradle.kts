
import com.soywiz.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "com.honorthrawn.CrownofStars"
    name = "Crown of Stars"
    version = "0.0.9"
   // icon = file("C:\\DEV\\MYSRC\\CrownOfStars\\src\\commonMain\\resources\\ui\\crown.png")
// To enable all targets at once
   // targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    //targetJs()
    //targetDesktop()
    targetIos()
    targetAndroidIndirect() // targetAndroidDirect()
    serializationJson()
    //targetAndroidDirect()
    bundle("https://github.com/korlibs/korlibs-bundle-source-extensions.git::text-terminal::8a6d8af9c5cbd7be3acccfac145d0671f4477588")
}
