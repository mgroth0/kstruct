package matt.kstruct.compilation.target

import kotlinx.serialization.Serializable
import matt.kstruct.target.Common
import matt.kstruct.target.CompilationTarget
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon

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
//    fun forTarget(target: CompilationTarget): ValidatedTargetConfig
}

@Serializable
object ClassicJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        return when (target) {
//            Common     -> TODO()
//            Js         -> TODO()
//            Android    -> TODO()
//            JvmCommon  -> this
//            JvmDesktop -> TODO()
//            Native     -> TODO()
//        }
//    }
}

@Serializable
object ClassicJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override val compilation = true
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object ClassicJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = ClassicJvmMain
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object GradleMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object GradleTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = GradleMain
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object MppCommonMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = Common
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        return when (target) {
//            Common     -> this
//            Js         -> TODO()
//            Android    -> TODO()
//            JvmCommon  -> MppJvmMainCompilation
//            JvmDesktop -> TODO()
//            Native     -> TODO()
//        }
//    }
}

@Serializable
object MppJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object MppJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = JvmCommon
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object MppJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = MppJvmTest
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object MppJsMain : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

@Serializable
object MppJsMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override val compilation = true
    override fun nonTest() = this
//    override fun forTarget(target: CompilationTarget): ValidatedTargetConfig {
//        TODO("Not yet implemented")
//    }
}

