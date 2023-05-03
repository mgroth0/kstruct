package matt.kstruct.compilation.target

import kotlinx.serialization.Serializable
import matt.kstruct.target.Android
import matt.kstruct.target.Common
import matt.kstruct.target.CompilationTarget
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon
import matt.kstruct.target.JvmDesktop
import matt.kstruct.target.Native

private enum class ClassPathUsage {
    Compile,
    Runtime,
}

@Serializable
sealed interface ValidatedTargetConfig {
    val test: Boolean
    val compilation: Boolean get() = false /*Meaning don't include recursive implementation dependencies*/
    val target: CompilationTarget
    fun nonTest(): ValidatedTargetConfig

    /*because common targets for a specific project actually mean different things depending on what is present*/
    /*https://youtrack.jetbrains.com/issue/KT-33578*/
    fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig
}

@Serializable
object ClassicJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopMain
            Native     -> TODO()
        }
    }
}

@Serializable
object ClassicJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override val compilation = true
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopMainCompilation
            Native     -> TODO()
        }
    }
}

@Serializable
object ClassicJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = ClassicJvmMain
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> TODO()
            Native     -> TODO()
        }
    }
}


@Serializable
object JvmDesktopMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmDesktop
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> this
            Native     -> TODO()
        }
    }
}

@Serializable
object JvmDesktopMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = JvmDesktop
    override val compilation = true
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> this
            Native     -> TODO()
        }
    }
}


@Serializable
object JvmDesktopTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmDesktop
    override fun nonTest() = JvmDesktopMain
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> TODO()
            JvmDesktop -> this
            Native     -> TODO()
        }
    }
}

@Serializable
object GradleMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopMain
            Native     -> TODO()
        }
    }
}

@Serializable
object GradleTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = GradleMain
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> TODO()
            Native     -> TODO()
        }
    }
}

@Serializable
object MppCommonMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = Common
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> MppJvmMainCompilation
            JvmDesktop -> JvmDesktopMainCompilation
            Native     -> TODO()
        }
    }
}

@Serializable
object MppJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopMain
            Native     -> TODO()
        }
    }
}

@Serializable
object MppJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = JvmCommon
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopMainCompilation
            Native     -> TODO()
        }
    }
}

@Serializable
object MppJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = MppJvmTest
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> TODO()
            Android    -> TODO()
            JvmCommon  -> this
            JvmDesktop -> JvmDesktopTest
            Native     -> TODO()
        }
    }
}

@Serializable
object MppJsMain : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> this
            Android    -> TODO()
            JvmCommon  -> TODO()
            JvmDesktop -> TODO()
            Native     -> TODO()
        }
    }
}

@Serializable
object MppJsMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override val compilation = true
    override fun nonTest() = this
    override fun forMostSpecificTarget(target: CompilationTarget): ValidatedTargetConfig {
        return when (target) {
            Common     -> this
            Js         -> this
            Android    -> TODO()
            JvmCommon  -> TODO()
            JvmDesktop -> TODO()
            Native     -> TODO()
        }
    }
}

