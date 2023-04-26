package matt.kstruct.include

import matt.collect.itr.duplicates
import matt.file.MFile
import matt.file.commons.BUILDSRC_FILE_NAME
import matt.file.commons.BUILD_GRADLE_KTS_NAME
import matt.file.commons.BUILD_JSON_NAME
import matt.json.prim.loadJson
import matt.model.code.mod.GradleKSubProjectPath

private const val UNLOAD_JSON = "unload.json"
fun unloadedPaths(rootDir: MFile) = (rootDir[UNLOAD_JSON].takeIf { it.exists() }?.loadJson<List<String>>() ?: listOf())

fun discoverAllBuildJsonModules(
    rootFolder: MFile
) = sequence {
    val toUnload = unloadedPaths(rootFolder).toMutableSet()

    if (toUnload.duplicates().isNotEmpty()) {
        /*I use this list in other places, so it needs to be correct*/
        error("$UNLOAD_JSON has duplicates")
    }

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
                    if (realProjPath in toUnload) {
                        toUnload.remove(realProjPath)
                    } else {
                        yield(GradleKSubProjectPath(realProjPath))
                    }
                    mightHaveDirectSubprojects.add(it)
                }
        }
    }
    toUnload.forEach {
        /*I use this list in other places, so it needs to be correct*/
        error("Cannot find subproject to unload with path $it")
    }
}