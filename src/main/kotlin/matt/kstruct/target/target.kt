package matt.kstruct.target

import kotlinx.serialization.Serializable
import matt.kstruct.gradle.GradleConfigIdea

@Serializable
sealed interface CompilationTarget : GradleConfigIdea {
    fun includes(other: CompilationTarget): Boolean
}

@Serializable
sealed interface ExportsToJs : CompilationTarget
@Serializable
sealed interface ExportsToJvmDesktop : CompilationTarget
@Serializable
sealed interface ExportsToJvmAndroid : CompilationTarget
@Serializable
sealed interface ExportsToJvmCommon : ExportsToJvmDesktop, ExportsToJvmAndroid
@Serializable
sealed interface ExportsToNative : CompilationTarget
@Serializable
sealed interface ExportsToCommon : ExportsToNative, ExportsToJvmCommon, ExportsToJs

@Serializable
object JvmDesktop : ExportsToJvmDesktop {
    override fun includes(other: CompilationTarget) = other is ExportsToJvmDesktop
}

@Serializable
object Js : ExportsToJs {
    override fun includes(other: CompilationTarget) = other is ExportsToJs
}

@Serializable
object Native : ExportsToNative {
    override fun includes(other: CompilationTarget) = other is ExportsToNative
}

@Serializable
object Common : ExportsToJvmCommon {
    override fun includes(other: CompilationTarget) = other is ExportsToCommon
}

@Serializable
object JvmCommon : ExportsToJvmCommon {
    override fun includes(other: CompilationTarget) = other is ExportsToJvmCommon
}

@Serializable
object Android : ExportsToJvmAndroid {
    override fun includes(other: CompilationTarget) = other is ExportsToJvmAndroid
}
