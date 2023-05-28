package matt.kstruct.mod.fstruct

import matt.file.Folder
import matt.file.commons.BUILD_GRADLE_GROOVY_NAME
import matt.file.commons.BUILD_GRADLE_KTS_NAME
import matt.file.commons.BUILD_JSON_NAME
import matt.file.commons.GIT_IGNORE_FILE_NAME
import matt.kstruct.ksub.LocatedMod
import matt.kstruct.ksub.LocatedProject
import matt.model.code.idea.ModIdea
import matt.model.code.mod.RelativeToKMod
import matt.model.code.mod.jarBaseName

val LocatedProject.buildFolder get() = Folder(folder + "build")
val LocatedProject.buildLibsFolder get() = buildFolder["libs"]
val LocatedMod.srcFolder get() = Folder(folder + "src")
val LocatedMod.buildGradleKts get() = folder + BUILD_GRADLE_KTS_NAME
val LocatedMod.buildGradle get() = folder + BUILD_GRADLE_GROOVY_NAME
val LocatedMod.buildJson get() = folder + BUILD_JSON_NAME


val LocatedMod.gitIgnoreFile get() = folder + GIT_IGNORE_FILE_NAME


interface ModFileStruct : ModIdea


const val SHADOW_JAR_SUFFIX = "-0-all.jar"
val RelativeToKMod.nonMppShadowJarName get() = "${jarBaseName}$SHADOW_JAR_SUFFIX"
val RelativeToKMod.mppShadowJarName get() = "${jarBaseName}-jvm$SHADOW_JAR_SUFFIX"
val LocatedMod.nonMppShadowJar get() = buildLibsFolder[nonMppShadowJarName]
val LocatedMod.mppShadowJar get() = buildLibsFolder[mppShadowJarName]