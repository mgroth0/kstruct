package matt.kstruct.compilation

import matt.kstruct.bj.CodeModule
import matt.kstruct.bj.dep.BuildJsonDependency
import matt.kstruct.bj.dep.BuildJsonGradleKotlinDSLDependency
import matt.kstruct.bj.dep.BuildJsonGradlePluginDependency
import matt.kstruct.bj.dep.BuildJsonIncludedDependency
import matt.kstruct.bj.dep.BuildJsonProjectDependency
import matt.kstruct.cfg.gradleConfiguration
import matt.kstruct.compilation.target.ValidatedTargetConfig
import matt.kstruct.gradle.TypicalConfigs.api
import matt.model.code.mod.GradleKSubProjectPath


class MyClasspath(
    val mod: CodeModule,
    val targetConfig: ValidatedTargetConfig,
) {
    private val target = targetConfig.target
    private val test = targetConfig.test
    private val compilation = targetConfig.compilation

    private fun resolveDirectDependencies(
        includeImplementations: Boolean
    ): List<BuildJsonDependency> {
        var deps = mod.dependencies


        if (!test) {
            deps = deps.filter {
                when (it) {
                    is BuildJsonGradlePluginDependency    -> true
                    is BuildJsonGradleKotlinDSLDependency -> true
                    is BuildJsonIncludedDependency        -> it.gradleConfiguration.isNotTest
                }
            }
        }

        if (!includeImplementations) {
            deps = deps.filter {
                when (it) {
                    is BuildJsonGradlePluginDependency    -> true
                    is BuildJsonGradleKotlinDSLDependency -> true
                    is BuildJsonIncludedDependency        -> it.gradleConfiguration.typicalConfig == api
                }
            }
        }


        deps = deps.filter {
            when (it) {
                is BuildJsonGradlePluginDependency    -> true
                is BuildJsonGradleKotlinDSLDependency -> true
                is BuildJsonIncludedDependency        -> {
                    target.includes(it.gradleConfiguration.targetIn(mod))
                }
            }
        }


        return deps

    }

    fun resolveRecursiveDependencies(
        buildJsonProvider: (GradleKSubProjectPath) -> CodeModule
    ): List<BuildJsonDependency> = resolveRecursiveDependencies(
        includeDirectImplementations = true,
        consumers = listOf(),
        buildJsonProvider = buildJsonProvider
    )

    private fun resolveRecursiveDependencies(
        includeDirectImplementations: Boolean,
        consumers: List<GradleKSubProjectPath>,
        buildJsonProvider: (GradleKSubProjectPath) -> CodeModule
    ): List<BuildJsonDependency> {

        val directDependencies = resolveDirectDependencies(
            includeImplementations = includeDirectImplementations
        )

        val recursiveDeps = directDependencies.filterIsInstance<BuildJsonProjectDependency>().flatMap {
            val gradleKSubPath = GradleKSubProjectPath(it.path)
            if (gradleKSubPath in consumers) {
                error("circular dependency: $it")
            }
            val depCodeMod = buildJsonProvider(gradleKSubPath)
            MyClasspath(
                mod = depCodeMod,
                targetConfig = targetConfig.nonTest()
            ).resolveRecursiveDependencies(
                includeDirectImplementations = !compilation,
                consumers = consumers + gradleKSubPath,
                buildJsonProvider = buildJsonProvider
            )
        }

        return directDependencies + recursiveDeps

    }

}