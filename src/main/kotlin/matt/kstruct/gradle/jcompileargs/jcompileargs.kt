package matt.kstruct.gradle.jcompileargs

object JavaCompilerArgs {
    val args by lazy {
        arrayOf(
            "-Xlint:unchecked"
        )
    }
}