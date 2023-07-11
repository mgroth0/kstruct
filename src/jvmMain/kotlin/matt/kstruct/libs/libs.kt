package matt.kstruct.libs

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import matt.file.MFile
import matt.file.commons.COMMON_PROJ_FOLDER
import matt.file.commons.DEFAULT_GITHUB_BRANCH_NAME
import matt.file.commons.TEMP_DIR
import matt.http.commons.GH_USERNAME
import matt.http.gh.rawGithubURL
import matt.json.custom.jsonObjectOrNull
import matt.json.custom.stringOrNull
import matt.log.warn.warn
import matt.model.code.version.GradleVersion
import matt.model.code.version.JavaVersion
import matt.model.code.version.PythonVersion
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import java.net.URI
import java.net.URL


abstract class TomlVersions {
    abstract fun tomlVersion(name: String): String
    abstract fun tomlVersionOrNull(name: String): String?
    abstract val librariesTableAsJson: JsonObject
    abstract fun libKeyToDepNotation(key: String): String?


    val java by lazy {
        JavaVersion(tomlVersion("java"))
    }
    val python by lazy {
        PythonVersion(tomlVersion("python"))
    }
}


class TomlVersionsImpl(private val libsVersionsText: String) : TomlVersions() {

    override fun tomlVersion(name: String) = versionsTable.getString(name)!!
    override fun tomlVersionOrNull(name: String): String? = versionsTable.getString(name)


    private val toml: TomlParseResult = run {
        Toml.parse(libsVersionsText)
    }

    private val versionsTable by lazy { toml.getTable("versions")!! }
    val pluginsTable by lazy { toml.getTable("plugins")!! }
    val librariesTable by lazy { toml.getTable("libraries")!! }
    val bundlesTable by lazy { toml.getTable("bundles")!! }
    val pythonTable by lazy { toml.getTable("metadata.python")!! }
    override val librariesTableAsJson by lazy {
        Json.decodeFromString<JsonObject>(
            librariesTable.toJson()
        )
    }
    val bundlesTableAsJson by lazy {
        Json.decodeFromString<JsonObject>(
            bundlesTable.toJson()
        )
    }
    val pluginsTableAsJson by lazy {
        Json.decodeFromString<JsonObject>(
            pluginsTable.toJson()
        )
    }
    val pythonTableAsJson by lazy {
        Json.decodeFromString<JsonObject>(
            pythonTable.toJson()
        )
    }



    override fun libKeyToDepNotation(key: String): String? {
        val obj = librariesTableAsJson[key]?.jsonObjectOrNull ?: return null
        val module = obj["module"]?.stringOrNull ?: run {

            val g = obj["group"]?.stringOrNull ?: return null
            val n = obj["name"]?.stringOrNull ?: return null

            "$g:$n"

        }

        val v = obj["version"]


        val version = v?.stringOrNull ?: run {
            v?.jsonObjectOrNull?.get("ref")?.stringOrNull?.let {
                tomlVersionOrNull(it)
            }
        }

        return if (version == null) {
            module /*for jetpack compose which is controlled by BOM library*/
        } else "$module:$version"
    }


    val javaJPackNonWindows by lazy {
        JavaVersion(tomlVersion("java-j-pack-non-windows"))
    }
    val javaJPackWindows by lazy {
        JavaVersion(tomlVersion("java-j-pack-windows"))
    }


    val gradle by lazy {
        GradleVersion(tomlVersion("gradle"))
    }


    fun serializedToToml() = toml.toToml()

    fun writeIntoFolder(folder: MFile) {
        folder[LIBS_VERSIONS_TOML].text = serializedToToml()
    }

    fun pythonReq(key: String) = pythonTable.get(key) as String

}


const val LIBS_VERSIONS_TOML = "libs.versions.toml"


interface TomlVersionsConstantTextProvider {
    val libsVersionsText: String
    val physicalFile: MFile

}

fun TomlVersionsConstantTextProvider.toTomlVersions() = TomlVersionsImpl(libsVersionsText)


class LazyTomlVersionsConstantTextProvider(
    tryLookingInsideHereFirstAndWarnIfNotPresent: MFile? = null,
) : TomlVersionsConstantTextProvider {
    companion object {

        val COMMON_LIBS_VERSIONS_FILE by lazy { COMMON_PROJ_FOLDER + LIBS_VERSIONS_TOML }

        private val LIBS_VERSIONS_ONLINE_URI by lazy {
            URI(
                rawGithubURL(
                    user = GH_USERNAME,
                    repo = COMMON_PROJ_FOLDER.name,
                    branch = DEFAULT_GITHUB_BRANCH_NAME,
                    path = LIBS_VERSIONS_TOML
                )
            )
        }
        private val LIBS_VERSIONS_ONLINE_URL: URL by lazy { LIBS_VERSIONS_ONLINE_URI.toURL() }


    }

    private val tryFirstOrWarnToml by lazy {
        tryLookingInsideHereFirstAndWarnIfNotPresent?.resolve(LIBS_VERSIONS_TOML)
    }


    private val registeredCommonExists by lazy {
        COMMON_LIBS_VERSIONS_FILE.exists()
    }

    override val libsVersionsText by lazy {
        val expected: String? = tryFirstOrWarnToml?.abspath
        var got: String? = null
        val fromTryFirst = tryFirstOrWarnToml?.let {
            if (it.exists()) {
                got = it.abspath
                it.text
            } else {
                null
            }
        }

        val t = if (fromTryFirst != null) {
            fromTryFirst
        } else {
            if (registeredCommonExists) {
                got = COMMON_LIBS_VERSIONS_FILE.abspath
                COMMON_LIBS_VERSIONS_FILE.text
            } else {
                got = LIBS_VERSIONS_ONLINE_URL.toString()
                LIBS_VERSIONS_ONLINE_URL.readText()
            }
        }

        if (expected != null) {
            if (expected != got) {
                warn("Expected to get versions from $expected, but that did not exist. Got them from $got instead.")
            }
        }
        t
    }


    override val physicalFile by lazy {
        matt.file.ext.createTempFile(directory = TEMP_DIR.mkdir("libVersionsToml"), suffix = ".toml").apply {
            writeText(libsVersionsText)
        }
    }
}



