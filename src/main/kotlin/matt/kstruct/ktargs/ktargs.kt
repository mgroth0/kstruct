package matt.kstruct.ktargs

import matt.collect.itr.mapToArray
import matt.kstruct.ktargs.optin.KotlinOptIn
import matt.lang.opt

data class KotlinCompilerArgs(
    val optIns: Set<KotlinOptIn> = setOf(),
    val nativeBundleID: String? = null, /*prevents a warning*/
) {
    val args by lazy {
        arrayOf(
            *optIns.mapToArray { it.arg },
            *opt(nativeBundleID) { "-Xbundle-id=$nativeBundleID" },
            "-Xsuppress-version-warnings",
            "-Xskip-prerelease-check",
            "-Xcontext-receivers",
        )
    }
}