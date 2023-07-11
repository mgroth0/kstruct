package matt.kstruct.ktargs

import matt.collect.itr.mapToArray
import matt.kstruct.ktargs.optin.KotlinOptIn
import matt.lang.If
import matt.lang.anno.SeeURL
import matt.lang.opt

const val DEFAULT_EXTENDED_COMPILER_CHECKS = false

data class KotlinCompilerArgs(
    val optIns: Set<KotlinOptIn> = setOf(),
    val nativeBundleID: String? = null, /*prevents a warning*/
    @SeeURL("https://youtrack.jetbrains.com/issue/KT-46654/What-are-the-performance-implications-of-Xextended-compiler-checks")
    /*might need this to solve the current bug I'm working on*/
    private val extendedCompilerChecks: Boolean = DEFAULT_EXTENDED_COMPILER_CHECKS
) {
    val args by lazy {
        arrayOf(
            *optIns.mapToArray { it.arg },
            *opt(nativeBundleID) { "-Xbundle-id=$nativeBundleID" },
            "-Xsuppress-version-warnings",
            "-Xskip-prerelease-check",
            "-Xcontext-receivers",
            *If(extendedCompilerChecks).then("-Xextended-compiler-checks")
        )
    }
}