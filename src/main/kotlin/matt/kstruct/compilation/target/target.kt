package matt.kstruct.compilation.target

import matt.kstruct.target.CompilationTarget
import matt.kstruct.target.JvmCommon

private enum class ClassPathUsage {
    Compile,
    Runtime,
}

sealed interface ValidatedTargetConfig {
    val test: Boolean
    val target: CompilationTarget
    fun nonTest(): ValidatedTargetConfig
}

object ClassicJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
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

object MppJvmMain : ValidatedTargetConfig {
    override val test = false
    override val target = JvmCommon
    override fun nonTest() = this
}

object MppJvmTest : ValidatedTargetConfig {
    override val test = true
    override val target = JvmCommon
    override fun nonTest() = MppJvmTest
}