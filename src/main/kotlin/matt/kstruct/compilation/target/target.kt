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
}

@Serializable
object ClassicJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

@Serializable
object ClassicJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override val compilation = true
    override fun nonTest() = this
}

@Serializable
object ClassicJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = ClassicJvmMain
}

@Serializable
object GradleMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

@Serializable
object GradleTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = GradleMain
}

@Serializable
object MppCommonMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = Common
    override fun nonTest() = this
}

@Serializable
object MppJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

@Serializable
object MppJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = JvmCommon
    override fun nonTest() = this
}

@Serializable
object MppJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = MppJvmTest
}

@Serializable
object MppJsMain : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override fun nonTest() = this
}

@Serializable
object MppJsMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override val compilation = true
    override fun nonTest() = this
}