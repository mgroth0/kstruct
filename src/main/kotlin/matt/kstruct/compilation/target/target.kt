package matt.kstruct.compilation.target

import matt.kstruct.target.Common
import matt.kstruct.target.CompilationTarget
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon

private enum class ClassPathUsage {
    Compile,
    Runtime,
}

sealed interface ValidatedTargetConfig {
    val test: Boolean
    val compilation: Boolean get() = false /*Meaning don't include recursive implementation dependencies*/
    val target: CompilationTarget
    fun nonTest(): ValidatedTargetConfig
}

object ClassicJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

object ClassicJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override val compilation = true
    override fun nonTest() = this
}

object ClassicJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = ClassicJvmMain
}


object GradleMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

object GradleTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = GradleMain
}

object MppCommonMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = Common
    override fun nonTest() = this
}

object MppJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

object MppJvmMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val compilation = true
    override val target = JvmCommon
    override fun nonTest() = this
}

object MppJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = MppJvmTest
}

object MppJsMain : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override fun nonTest() = this
}

object MppJsMainCompilation : ValidatedTargetConfig {
    override val test = false
    override val target = Js
    override val compilation = true
    override fun nonTest() = this
}