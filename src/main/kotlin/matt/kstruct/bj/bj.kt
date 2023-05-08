package matt.kstruct.bj

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import matt.collect.itr.list
import matt.kstruct.bj.cfg.JS
import matt.kstruct.bj.cfg.JvmConfig
import matt.kstruct.bj.cfg.JvmExecConfig
import matt.kstruct.bj.cfg.Native
import matt.kstruct.bj.dep.BuildJsonDependency
import matt.kstruct.target.Android
import matt.kstruct.target.Common
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon
import matt.kstruct.target.JvmDesktop
import matt.lang.go
import matt.model.code.mod.KMod
import matt.model.code.mod.RelativeToKMod
import kotlin.reflect.KClass

@Serializable
sealed interface BuildJsonModule : KMod {

    val note: String?
    fun copy(
        dependencies: List<BuildJsonDependency>? = null, note: String? = null, cls: KClass<out BuildJsonModule>? = null
    ): BuildJsonModule


    val publishes: Boolean
    var publicBytecode: Boolean
}


@Serializable
sealed interface JsOnlyNotMultiplatformModule : BuildJsonModule {
    override val publishes get() = false
    val client: Boolean
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializable
sealed class BuildJsonModuleImpl : BuildJsonModule {

    companion object {
        val guessAllModTypes: List<KClass<out BuildJsonModule>> by lazy {

            listOf<KClass<out BuildJsonModule>>(
                BasicJvmOnlyMod::class,
                MultiPlatformModule::class,
                GradleModule::class,
                JsClientModule::class,
                JsLibModule::class,
                AbstractModule::class,
                KCPluginModule::class
            )

        }
    }

    override var note: String? = null
        internal set

    override var publicBytecode = false

    override fun copy(
        dependencies: List<BuildJsonDependency>?, note: String?, cls: KClass<out BuildJsonModule>?
    ): BuildJsonModuleImpl = run {
        val jsonObj = Json.encodeToJsonElement(this).jsonObject


        val ser = (cls ?: this::class).serializer()
        val descriptor = ser.descriptor

        val copiedJsonObj = buildJsonObject {
            jsonObj.forEach {
                put(it.key, it.value)
            }
            put(Json.configuration.classDiscriminator, descriptor.serialName)
        }

        val r = Json.decodeFromJsonElement<BuildJsonModuleImpl>(copiedJsonObj)

        if (note != null) {
            r.note = note
        }

        if (dependencies != null) {
            (r as CodeModule).dependencies = dependencies
        }

        r.publicBytecode = publicBytecode


        if (this is CodeModule) {
            (r as CodeModule).python = python
        }

        r

    }

}


@Serializable
sealed class CodeModule : BuildJsonModuleImpl() {
    var dependencies = listOf<BuildJsonDependency>()
        internal set
    abstract val shouldAnalyzeDeps: Boolean

    var python: PythonConfig? = null
        internal set


}


@Serializable
class PythonConfig(
    val name: String? = null,
    val description: String? = null,
    val dependencies: List<PyDepRef> = listOf(),
    val urls: Map<String, String> = mapOf()
)


///*For the libs.toml*/
//@Serializable
//class PythonDependency(
//    val name: String,
//    val version: String,
//)

@Serializable
sealed interface PyDepRef {
    val testOnly: Boolean
}

@Serializable
@SerialName("Project")
class PythonProjectDepRef(
    val name: String,
    override val testOnly: Boolean = false
) : PyDepRef

@Serializable
@SerialName("Lib")
class PythonLibDepRef(
    val key: String,
    override val testOnly: Boolean = false
) : PyDepRef

/*
@Serializable
@SerialName("Project")
class PythonProjectDep : PythonDependency


@Serializable
@SerialName("Lib")
class PythonLibDep : PythonDependency
*/


@Serializable
sealed interface MaybeJvmExecutable {
    val jvmExec: JvmExecConfig?
}


@Serializable
sealed class JvmOnlyModule : CodeModule(), MaybeJvmExecutable {
    abstract val usedAsDep: Boolean
    abstract val publishToMC: Boolean
}

sealed interface ComposableThing {
    val compose: Boolean
}

sealed interface ComposableModule : ComposableThing, BuildJsonModule

@Serializable
@SerialName("BasicJvmOnlyMod")
class BasicJvmOnlyMod : JvmOnlyModule(), ComposableModule {
    override val publishes get() = usedAsDep
    override val publishToMC = false
    override val usedAsDep get() = jvmExec == null
    override val shouldAnalyzeDeps get() = !compose /*todo: temporarily turning off analyze deps for compose modules because I'm trying to do too many things at once... turn this back on some day (used to be "true")*/
    override var jvmExec: JvmExecConfig? = null
        internal set

    override var compose: Boolean = false

    val publicApp: Boolean = false

    fun copy(
        dependencies: List<BuildJsonDependency>? = null,
        note: String? = null,
        jvmExec: JvmExecConfig,
        cls: KClass<out BuildJsonModule>? = null
    ) = copy(dependencies, note, cls).also {
        (it as BasicJvmOnlyMod).jvmExec = jvmExec
        it.compose = compose
    }
}

@Serializable
@SerialName("IdeaPluginMod")
class IdeaPluginMod : JvmOnlyModule() {
    override val publishes get() = true
    override val usedAsDep get() = true
    override val shouldAnalyzeDeps get() = true
    override val jvmExec get() = null
    override val publishToMC get() = false
}


@Serializable
@SerialName("GradleModule")
class GradleModule : JvmOnlyModule() {
    override val jvmExec get() = null
    override val publishes get() = false
    override val usedAsDep get() = true
    override val shouldAnalyzeDeps get() = false
    override val publishToMC = false
}

@Serializable
@SerialName("KCPluginModule")
class KCPluginModule : JvmOnlyModule() {
    override val jvmExec get() = null
    override val publishes get() = false
    override val usedAsDep get() = false
    override val shouldAnalyzeDeps get() = false
    override val publishToMC get() = false
}

@Serializable
@SerialName("AndroidModule")
sealed class AndroidModule : JvmOnlyModule(), ComposableModule {
    override val publishToMC get() = false
}

@Serializable
@SerialName("AndroidLibModule")
class AndroidLibModule : AndroidModule() {
    override val usedAsDep: Boolean = true
    override val shouldAnalyzeDeps = false /*lets take things one step at a time...*/
    override val publishes =
        false /*just making this false for now to simplify/speed up things since I don't think I ever use my local maven artificats. But feel free to consider make this true if I ever want to*/
    override val jvmExec = null
    override val compose = true
}

@Serializable
@SerialName("AndroidAppModule")
class AndroidAppModule : AndroidModule() {
    override val usedAsDep: Boolean = false
    override val shouldAnalyzeDeps = false /*lets take things one step at a time...*/
    override val publishes = false
    override val jvmExec =
        null /*null for now, but may make this non-null at some point? i don't know exactly how I will "run" android apps yet*/
    override val compose = true
}


@Serializable
@SerialName("AbstractModule")
class AbstractModule : BuildJsonModuleImpl(), BuildJsonModule {

    override val publishes get() = false

}


@Serializable
@SerialName("JsLibModule")
class JsLibModule : CodeModule(), JsOnlyNotMultiplatformModule {
    override val client get() = false
    override val shouldAnalyzeDeps get() = false
}

@Serializable
@SerialName("JsClientModule")
class JsClientModule : CodeModule(), JsOnlyNotMultiplatformModule {
    override val client get() = true
    override val shouldAnalyzeDeps get() = false
}

@Serializable
@SerialName("MultiPlatformModule")
class MultiPlatformModule : CodeModule(), MaybeJvmExecutable, ComposableModule {
    override val publishes get() = (jvm != null && jvm.exec == null) || native == Native.LIB || js == JS.LIB
    val jvm: JvmConfig? = null
    val js: JS? = null
    val native: Native? = null
    val android: AndroidConfig? = null
    override val jvmExec get() = jvm?.exec
    override val shouldAnalyzeDeps get() = jvm != null && (android == null /*one thing at a time...*/)
    override val compose: Boolean = false
}

fun MultiPlatformModule.targetsItCanConsume() = list {
    add(Common)
    if (jvm != null) {
        add(JvmCommon)
        add(JvmDesktop)
    }
    if (js != null) {
        add(Js)
    }
    android?.go {
        add(JvmCommon)
        add(Android)
    }
    if (native != null) {
        add(matt.kstruct.target.Native)
    }
}.toSet()

@Serializable
sealed interface AndroidConfig

@Serializable
@SerialName("Lib")
class AndroidLibConfig : AndroidConfig

@Serializable
@SerialName("App")
class AndroidAppConfig : AndroidConfig

fun interface BuildJsonProvider {
    fun of(kSub: RelativeToKMod): BuildJsonModule
}