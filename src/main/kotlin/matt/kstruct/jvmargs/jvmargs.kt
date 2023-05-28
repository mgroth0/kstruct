package matt.kstruct.jvmargs

import matt.file.thismachine.thisMachine
import matt.kstruct.jvmargs.KJvmArgsSets.FOR_ALL_OTHER_MACHINES_MATT_USES
import matt.kstruct.jvmargs.KJvmArgsSets.FOR_NEW_MAC
import matt.model.code.jvm.JvmArgs
import matt.model.code.sys.NEW_MAC
import matt.model.data.byte.gibibytes

object KJvmArgsSets {

    val FOR_APP_RELEASES = JvmArgs(
        xmx = 6.gibibytes,
        enableAssertionsAndCoroutinesDebugMode = false,
    )

    val FOR_OM_EXEC = FOR_APP_RELEASES.copy(
        prism = false,
        enableAssertionsAndCoroutinesDebugMode = false,
        unlockDiagnosticVmOptions = false,
        showHiddenFrames = false,
    )

    val FOR_ALL_OTHER_MACHINES_MATT_USES = JvmArgs(6.gibibytes)

    val FOR_NEW_MAC = JvmArgs(30.gibibytes)


    val FOR_GRADLE =
        JvmArgs(
            xms = 16.gibibytes,
            xmx = 16.gibibytes,
            prism = false,
            enableAssertionsAndCoroutinesDebugMode = false,
            unlockDiagnosticVmOptions = false,
            showHiddenFrames = false,
            useParallelGC = true /*I've seen in multiple places including an official one that this could speed up android builds... but they all say to double check with a profiler*/
        )
    val FOR_KOTLIN_DAEMON = FOR_GRADLE.copy(useParallelGC = false)

}

fun jvmArgsForThisMattRuntime() = when (thisMachine) {
    NEW_MAC -> FOR_NEW_MAC
    else    -> FOR_ALL_OTHER_MACHINES_MATT_USES
}