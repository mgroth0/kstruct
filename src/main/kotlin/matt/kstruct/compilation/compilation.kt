package matt.kstruct.compilation

import matt.kstruct.bj.AndroidModule
import matt.kstruct.bj.CodeModule
import matt.kstruct.bj.JvmOnlyModule
import matt.kstruct.bj.MultiPlatformModule
import matt.kstruct.bj.dep.BuildJsonDependency
import matt.kstruct.bj.dep.BuildJsonGradleKotlinDSLDependency
import matt.kstruct.bj.dep.BuildJsonGradlePluginDependency
import matt.kstruct.bj.dep.BuildJsonIncludedDependency
import matt.kstruct.bj.dep.BuildJsonProjectDependency
import matt.kstruct.bj.targetsItCanConsume
import matt.kstruct.cfg.gradleConfiguration
import matt.kstruct.compilation.target.ValidatedTargetConfig
import matt.kstruct.gradle.TypicalConfigs.api
import matt.kstruct.target.Android
import matt.kstruct.target.ExportsToJs
import matt.kstruct.target.ExportsToJvmAndroid
import matt.kstruct.target.ExportsToJvmCommon
import matt.kstruct.target.ExportsToJvmDesktop
import matt.kstruct.target.ExportsToNative
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon
import matt.kstruct.target.JvmDesktop
import matt.kstruct.target.Native
import matt.model.code.mod.GradleKSubProjectPath

fun ValidatedTargetConfig.deCommonizeIn(mod: CodeModule) = when (mod) {

    /*JS? Native?*/

    is JvmOnlyModule       -> when (mod) {
        is AndroidModule -> forMostSpecificTarget(Android)
        else             -> forMostSpecificTarget(JvmCommon)
    }

    is MultiPlatformModule -> {
        val consumes = mod.targetsItCanConsume()
        when {
            consumes.all { it is ExportsToJs }         -> forMostSpecificTarget(Js)
            consumes.all { it is ExportsToNative }     -> forMostSpecificTarget(Native)
            consumes.all { it is ExportsToJvmAndroid } -> forMostSpecificTarget(Android)
            consumes.all { it is ExportsToJvmDesktop } -> forMostSpecificTarget(JvmDesktop)
            consumes.all { it is ExportsToJvmCommon }  -> forMostSpecificTarget(JvmCommon)
            else                                       -> this
        }
    }

    else                   -> this
}

class MyClasspath(
    val mod: CodeModule,
    private val consumerTargetConfig: ValidatedTargetConfig,
) {

    /*because common targets for a specific project actually mean different things depending on what is present*/
    /*https://youtrack.jetbrains.com/issue/KT-33578*/
    private val targetConfig = consumerTargetConfig/*.commonizeIn(mod)*/

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
    ): List<DependencyWithBreadcrumbs> = resolveRecursiveDependencies(
        includeDirectImplementations = true,
        consumers = listOf(),
        buildJsonProvider = buildJsonProvider
    )

    private fun resolveRecursiveDependencies(
        includeDirectImplementations: Boolean,
        consumers: List<GradleKSubProjectPath>,
        buildJsonProvider: (GradleKSubProjectPath) -> CodeModule
    ): List<DependencyWithBreadcrumbs> {

        val directDependencies = resolveDirectDependencies(
            includeImplementations = includeDirectImplementations
        ).map {
            DependencyWithBreadcrumbs(
                breadcrumbs = consumers.toList(),
                dep = it
            )
        }

        val recursiveDeps = directDependencies.filter { it.dep is BuildJsonProjectDependency }.flatMap {
            val gradleKSubPath = GradleKSubProjectPath((it.dep as BuildJsonProjectDependency).path)
            if (gradleKSubPath in consumers) {
                error("circular dependency: $it")
            }
            val depCodeMod = buildJsonProvider(gradleKSubPath)
            MyClasspath(
                mod = depCodeMod,
                /*because common targets for a specific project actually mean different things depending on what is present*/
                /*https://youtrack.jetbrains.com/issue/KT-33578*/
                consumerTargetConfig = consumerTargetConfig.nonTest().deCommonizeIn(depCodeMod)
            ).resolveRecursiveDependencies(
                includeDirectImplementations = !compilation,
                consumers = consumers + gradleKSubPath,
                buildJsonProvider = buildJsonProvider
            )
        }
        return directDependencies + recursiveDeps
    }
}

class DependencyWithBreadcrumbs(
    val breadcrumbs: List<GradleKSubProjectPath>,
    val dep: BuildJsonDependency,
) {
    override fun toString(): String {
        return if (dep is BuildJsonProjectDependency) {
            breadcrumbs.joinToString(separator = " -> ") { it.path } + " -> ${dep.path}"
        } else {
            require(breadcrumbs.isEmpty())
            dep.toString()
        }

    }
}
