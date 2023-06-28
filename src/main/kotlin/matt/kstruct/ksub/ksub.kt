package matt.kstruct.ksub

import matt.file.MFile
import matt.file.commons.DEFAULT_GIT_FILE_NAME
import matt.file.commons.IdeProject
import matt.file.commons.IdeProject.all
import matt.file.construct.mFile
import matt.json.YesIUseJson
import matt.json.prim.loadJson
import matt.kstruct.bj.BuildJsonModuleImpl
import matt.kstruct.gradle.GradleTask
import matt.kstruct.mod.Mod
import matt.kstruct.mod.fstruct.buildJson
import matt.kstruct.mod.kGroupName
import matt.model.code.mod.GradleProjectPath
import matt.model.code.mod.GradleTaskPath
import matt.model.code.mod.ModType
import matt.model.code.mod.RelativeToKMod
import matt.model.code.mod.gradlePath
import matt.model.code.mod.isRoot

val RelativeToKMod.relFolder
    get() = mFile(
        gradlePath.split(":").joinToString(MFile.separator).removePrefix(MFile.separator)
    )


val RelativeToKMod.projectName get() = gradlePath.split(":").last()


interface LocatedProject : ModType {
    val folder: MFile
}

interface LocatedMod : Mod, LocatedProject {
    override val folder: MFile
    override val buildJsonModule get() = loadBuildJson()
}


fun LocatedMod.loadBuildJson(): BuildJsonModuleImpl {
    YesIUseJson
    return buildJson.loadJson()
}


val LocatedProject.isGitProject: Boolean
    get() {
        val theFiles = folder.list() ?: error("got null when listing files in $folder")
        return DEFAULT_GIT_FILE_NAME in theFiles
    }


fun RelativeToKMod.within(root: IdeProject) = LocatedKSubProject(this, root)
val RelativeToKMod.withinAll get() = within(all)


class LocatedKSubProject(val sub: RelativeToKMod, val root: IdeProject) : LocatedMod {
    override val folder by lazy {
        (root.folder + sub.relFolder)
    }

    override val relToKNames get() = sub.relToKNames

    override fun toString() = "LocatedKSubProject[sub=$sub,root=$root]"

    override val groupName get() = kGroupName
}


fun RelativeToKMod.pathForTask(task: GradleTask) = pathForTaskNamed(task.name)
fun RelativeToKMod.pathForTaskNamed(taskName: String) = GradleTaskPath("${gradlePath.removeSuffix(":")}:${taskName}")

fun GradleProjectPath.withinRoot(root: IdeProject) = LocatedProjectImpl(this, root)


class LocatedProjectImpl(val sub: GradleProjectPath, val root: IdeProject) : GradleProjectPath by sub, LocatedProject {
    override val folder: MFile
        get() = sub.folderIn(root.folder)
}

fun GradleProjectPath.folderIn(rootFolder: MFile): MFile {
    val r = if (isRoot) rootFolder
    else rootFolder.resolve(path.removePrefix(":").replace(":", MFile.separator))
    return r
}
