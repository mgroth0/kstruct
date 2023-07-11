package matt.kstruct.include

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import matt.file.MFile
import matt.file.commons.BUILDSRC_FILE_NAME
import matt.file.commons.BUILD_GRADLE_KTS_NAME
import matt.file.commons.BUILD_JSON_NAME
import matt.json.prim.loadJson
import matt.model.code.mod.GradleKSubProjectPath

private const val LOAD_JSON = "load.json"


@Serializable
sealed interface LoadConfig

@Serializable
@SerialName("UnLoad")
class ToUnLoad(val unload: Set<String>) : LoadConfig

@Serializable
@SerialName("Load")
class ToLoad(val load: Set<String>) : LoadConfig

fun loadConfig(rootDir: MFile) =
    (rootDir[LOAD_JSON].takeIf { it.exists() }?.loadJson<LoadConfig>() ?: ToUnLoad(unload = setOf()))

fun discoverAllBuildJsonModules(
    rootFolder: MFile
) = sequence {
    val lConfig = loadConfig(rootFolder)
    val toUnload = (lConfig as? ToUnLoad)?.unload?.toMutableSet() ?: mutableSetOf()
    val toLoad = (lConfig as? ToLoad)?.load?.toMutableSet()

    /*    if (toUnload.duplicates().isNotEmpty()) {
            *//*I use this list in other places, so it needs to be correct*//*
        error("$LOAD_JSON has duplicates")
    }*/

    val mightHaveDirectSubprojects = mutableListOf(rootFolder.resolve("k"))
    while (mightHaveDirectSubprojects.isNotEmpty()) {
        mightHaveDirectSubprojects.toList().apply {
            mightHaveDirectSubprojects.clear()
        }.forEach { file ->
            file
                .listFiles()!!
                .filter {
                    val li = (it.list() ?: arrayOf())
                    (BUILD_GRADLE_KTS_NAME in li || BUILD_JSON_NAME in li)
                }
                .filter { it.name != BUILDSRC_FILE_NAME }
                .forEach {
                    val projPath = it.relativeTo(rootFolder).path.replace(
                        MFile.separator,
                        ":"
                    )
                    val realProjPath = ":$projPath"
                    if (toLoad != null) {
                        if (realProjPath in toLoad) {
                            yield(GradleKSubProjectPath(realProjPath))
                            toLoad.remove(realProjPath)
                        }
                    } else {
                        if (realProjPath in toUnload) {
                            toUnload.remove(realProjPath)
                        } else {
                            yield(GradleKSubProjectPath(realProjPath))
                        }
                    }

                    mightHaveDirectSubprojects.add(it)
                }
        }
    }
    toUnload.forEach {
        /*I use this list in other places, so it needs to be correct*/
        error("Cannot find subproject to unload with path $it")
    }
    toLoad?.forEach {
        /*I use this list in other places, so it needs to be correct*/
        error("Cannot find subproject to load with path $it")
    }
}