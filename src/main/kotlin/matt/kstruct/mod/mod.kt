package matt.kstruct.mod

import matt.kstruct.bj.BuildJsonModule
import matt.model.code.mod.AbsoluteKMod
import matt.model.code.mod.RelativeToKMod
import matt.prim.str.lower

interface Mod : AbsoluteKMod {
    val buildJsonModule: BuildJsonModule
}

val RelativeToKMod.kGroupName
    get() = (arrayOf("matt") + relToKNames.dropLast(1)).joinToString(".") { it.lower() }