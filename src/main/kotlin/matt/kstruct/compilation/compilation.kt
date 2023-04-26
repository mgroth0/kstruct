package matt.kstruct.compilation

import matt.kstruct.bj.CodeModule
import matt.kstruct.bj.dep.BuildJsonIncludedDependency
import matt.kstruct.bj.dep.BuildJsonProjectDependency
import matt.kstruct.cfg.gradleConfiguration
import matt.kstruct.compilation.target.ValidatedTargetConfig
import matt.model.code.mod.GradleKSubProjectPath


class MyClasspath(
    val mod: CodeModule,
    val targetConfig: ValidatedTargetConfig,
) {
    private val target = targetConfig.target
    private val test = targetConfig.test
    private fun resolveDirectDependencies(): List<BuildJsonIncludedDependency> {
        var deps = mod.dependencies.filterIsInstance<BuildJsonIncludedDependency>()

        if (!test) {
            deps = deps.filter { it.gradleConfiguration.isNotTest }
        }

        deps = deps.filter { target.includes(it.gradleConfiguration.targetIn(mod)) }

        return deps

    }

    fun resolveRecursiveDependencies(
        buildJsonProvider: (GradleKSubProjectPath) -> CodeModule
    ): List<BuildJsonIncludedDependency> {

        val directDependencies = resolveDirectDependencies()

        val recursiveDeps = directDependencies.filterIsInstance<BuildJsonProjectDependency>().flatMap {
            val depCodeMod = buildJsonProvider(GradleKSubProjectPath(it.path))
            MyClasspath(
                mod = depCodeMod,
                targetConfig = targetConfig.nonTest()
            ).resolveRecursiveDependencies(buildJsonProvider)
        }

        return directDependencies + recursiveDeps

    }


    fun resolveRecursiveProjectDeps(
        buildJsonProvider: (GradleKSubProjectPath) -> CodeModule
    ) = resolveRecursiveDependencies(
        buildJsonProvider = buildJsonProvider
    ).filterIsInstance<BuildJsonProjectDependency>()

}