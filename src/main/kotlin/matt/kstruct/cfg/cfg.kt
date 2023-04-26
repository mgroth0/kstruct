package matt.kstruct.cfg

import matt.kstruct.bj.AndroidModule
import matt.kstruct.bj.CodeModule
import matt.kstruct.bj.JsOnlyNotMultiplatformModule
import matt.kstruct.bj.MultiPlatformModule
import matt.kstruct.bj.dep.BuildJsonIncludedDependency
import matt.kstruct.gradle.ATypicalConfigs
import matt.kstruct.gradle.GradleConfig
import matt.kstruct.gradle.GradleConfigIdea
import matt.kstruct.gradle.TypicalConfigs
import matt.kstruct.target.Android
import matt.kstruct.target.Common
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon
import matt.kstruct.target.JvmDesktop
import matt.kstruct.target.Native
import matt.prim.str.lower

val BuildJsonIncludedDependency.gradleConfiguration get() = GradleConfiguration(cfg)

@JvmInline
value class GradleConfiguration(val name: String) : GradleConfigIdea {
    private val lowName get() = name.lower()

    val typicalConfig
        get() = (TypicalConfigs.values().toList<GradleConfig>() + ATypicalConfigs.values()
            .toList<GradleConfig>()).firstOrNull {
            lowName.endsWith(it.name.lower())
        } ?: error("could not find right typicalConfig for $name")


    private val beforeTypConfig get() = lowName.substringBeforeLast(typicalConfig.name.lower())


    val isTest get() = beforeTypConfig.endsWith("test")
    val isMain get() = !isTest
    val isNotTest get() = isMain

    private val beforeMainOrTest
        get() =
            if (isTest) beforeTypConfig.substringBeforeLast("test") else beforeTypConfig.substringBeforeLast("main")

    init {
        /*validate typicalConfig does not throw an error*/
        typicalConfig

        if (!isTest && beforeTypConfig.isNotBlank()) {
            require(beforeTypConfig.endsWith("main")) {
                "thought $beforeTypConfig would end with \"main\" in $name"
            }
        }
    }


    fun targetIn(module: CodeModule) = when (beforeMainOrTest) {
        "" -> when (module) {
            is JsOnlyNotMultiplatformModule -> Js
            is AndroidModule -> Android
            is MultiPlatformModule -> Android
            else -> JvmCommon
        }

        "js" -> Js
        "native" -> Native
        "common" -> Common
        "android" -> Android
        "jvm" -> when {
            ((module as? MultiPlatformModule)?.android != null) -> JvmDesktop
            else -> JvmCommon
        }

        else -> error("what is target of \"$beforeMainOrTest\"?")
    }

}


