package matt.kstruct.jvmargs

import matt.file.thismachine.thisMachine
import matt.kstruct.jvmargs.KJvmArgsSets.FOR_ALL_OTHER_MACHINES_MATT_USES
import matt.kstruct.jvmargs.KJvmArgsSets.FOR_NEW_MAC
import matt.model.code.jvm.JvmArgs
import matt.model.code.sys.NEW_MAC
import matt.model.data.byte.gigabytes

object KJvmArgsSets {

    val FOR_APP_RELEASES = JvmArgs(
        xmx = 6.gigabytes,
        enableAssertionsAndCoroutinesDebugMode = false,
    )

    val FOR_ALL_OTHER_MACHINES_MATT_USES = JvmArgs(6.gigabytes)

    val FOR_NEW_MAC = JvmArgs(30.gigabytes)

}

fun jvmArgsForThisMattRuntime() = when (thisMachine) {
    NEW_MAC -> FOR_NEW_MAC
    else -> FOR_ALL_OTHER_MACHINES_MATT_USES
}