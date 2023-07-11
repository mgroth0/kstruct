package matt.kstruct.bj.dep

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface BuildJsonDependency {
    val note: String?
}

@Serializable
sealed class BuildJsonIncludedDependency : BuildJsonDependency {
    abstract val cfg: String
    override val note: String? = null
    val checkIfUsed: Boolean = true
}

@SerialName("Project")
@Serializable
class BuildJsonProjectDependency(
    override val cfg: String,
    val path: String,
    val depCfg: String? = null
) : BuildJsonIncludedDependency() {
    override fun toString() = path
}

@SerialName("Lib")
@Serializable
class BuildJsonLibDependency(
    override val cfg: String,
    val key: String,
    val platform: Boolean = false,
    val classifier: String? = null
) : BuildJsonIncludedDependency() {
    override fun toString() = key
}

@SerialName("GradlePlugin")
@Serializable
class BuildJsonGradlePluginDependency(
    val key: String,
    override val note: String? = null
) : BuildJsonDependency {
    override fun toString() = key
}


@SerialName("LibBundle")
@Serializable
class BuildJsonLibBundleDependency(
    override val cfg: String,
    val key: String
) : BuildJsonIncludedDependency() {
    override fun toString() = key
}

@SerialName("GradleAPI")
@Serializable
class BuildJsonGradleAPIDependency(
    override val cfg: String,
) : BuildJsonIncludedDependency()

@SerialName("GradleKotlinDSL")
@Serializable
object BuildJsonGradleKotlinDSLDependency : BuildJsonDependency {
    override val note = null
}

//@SerialName("TempIntelliJGradlePlugin")
//@Serializable class TempBuildJsonIntelliJGradlePluginDependency(
//  override val cfg: String,
//): BuildJsonIncludedDependency()

@SerialName("AbsoluteFiles")
@Serializable
class BuildJsonAbsoluteFilesDependency(
    override val cfg: String, val files: List<String>
) : BuildJsonIncludedDependency()

@SerialName("RelativeToRegisteredJar")
@Serializable
class BuildJsonRelativeToRegisteredJarDependency(
    override val cfg: String, val files: List<String>
) : BuildJsonIncludedDependency()