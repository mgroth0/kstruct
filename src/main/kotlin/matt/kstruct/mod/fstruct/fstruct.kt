package matt.kstruct.mod.fstruct

import matt.file.MFile
import matt.file.commons.BUILD_GRADLE_GROOVY_NAME
import matt.file.commons.BUILD_GRADLE_KTS_NAME
import matt.file.commons.BUILD_JSON_NAME
import matt.file.commons.GIT_IGNORE_FILE_NAME
import matt.kstruct.ksub.LocatedMod
import matt.model.code.idea.ModIdea


val LocatedMod.buildGradleKts: MFile get() = folder + BUILD_GRADLE_KTS_NAME
val LocatedMod.buildGradle: MFile get() = folder + BUILD_GRADLE_GROOVY_NAME
val LocatedMod.buildJson: MFile get() = folder + BUILD_JSON_NAME


val LocatedMod.gitIgnoreFile: MFile get() = folder + GIT_IGNORE_FILE_NAME


interface ModFileStruct : ModIdea