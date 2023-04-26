package matt.kstruct.gradle

import matt.collect.itr.mapToArray
import matt.kstruct.mod.srcset.SourceSets
import matt.kstruct.mod.srcset.SourceSets.commonMain
import matt.kstruct.mod.srcset.SourceSets.jsMain
import matt.kstruct.mod.srcset.SourceSets.jvmMain
import matt.kstruct.mod.srcset.SourceSets.main
import matt.kstruct.mod.srcset.SourceSets.nativeMain
import matt.kstruct.mod.srcset.SourceSets.resources
import matt.kstruct.mod.srcset.SourceSets.test
import matt.model.code.idea.ModIdea
import matt.prim.str.cap

interface GradleConfigIdea : ModIdea





sealed interface GradleConfig : GradleConfigIdea {
    val name: String
    fun nameFor(sourceSet: SourceSets) = when (sourceSet) {
        main -> name
        test -> TODO()
        resources -> TODO()
        commonMain -> commonMain.name + name.cap()
        jvmMain -> jvmMain.name + name.cap()
        jsMain -> jsMain.name + name.cap()
        nativeMain -> nativeMain.name + name.cap()
        else -> TODO()
    }


}


enum class TypicalConfigs : GradleConfig {
    api, implementation
}

enum class ATypicalConfigs : GradleConfig {
    compileOnly,
    runtimeOnly
}


val TYPICAL_CONFIGS = TypicalConfigs.values().mapToArray { it.name }


val JVM_ONLY_CONFIGS = (TypicalConfigs.values().toList<GradleConfig>() + ATypicalConfigs.values()
    .toList<GradleConfig>()).mapToArray { it.name }

val COMMON_CONFIGS = TypicalConfigs.values().mapToArray { it.nameFor(commonMain) }
val JVM_CONFIGS = TypicalConfigs.values().mapToArray { it.nameFor(jvmMain) }
val JS_CONFIGS = TypicalConfigs.values().mapToArray { it.nameFor(jsMain) }
val NATIVE_CONFIGS = TypicalConfigs.values().mapToArray { it.nameFor(nativeMain) }


val KOTLIN_MULTIPLATFORM_CONFIGS = arrayOf(
    *COMMON_CONFIGS, *JVM_CONFIGS, *JS_CONFIGS, *NATIVE_CONFIGS
)

const val VERBOSE_LOGGING_PROP_NAME = "verboseLogging"

enum class GradleTask {
    shadowJar, run, kbuild
}