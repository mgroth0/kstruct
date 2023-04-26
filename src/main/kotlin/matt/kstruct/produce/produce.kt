package matt.kstruct.produce

import matt.collect.itr.list
import matt.kstruct.bj.AndroidModule
import matt.kstruct.bj.CodeModule
import matt.kstruct.bj.JsClientModule
import matt.kstruct.bj.JsLibModule
import matt.kstruct.bj.JvmOnlyModule
import matt.kstruct.bj.MultiPlatformModule
import matt.kstruct.target.Android
import matt.kstruct.target.Common
import matt.kstruct.target.Js
import matt.kstruct.target.JvmCommon
import matt.kstruct.target.JvmDesktop
import matt.kstruct.target.Native


fun CodeModule.producedTargets() {
    when (this) {
        is JsLibModule, is JsClientModule -> listOf(Js)
        is AndroidModule -> listOf(Android)
        is JvmOnlyModule -> listOf(JvmCommon)
        is MultiPlatformModule -> list {
            add(Common)
            if (this@producedTargets.js != null) {
                add(Js)
            }
            if (this@producedTargets.native != null) {
                add(Native)
            }
            if (this@producedTargets.jvm != null) {
                add(JvmCommon)
            }
            if (this@producedTargets.android != null) {
                add(Android)
                if (this@producedTargets.jvm != null) {
                    add(JvmDesktop)
                }
            }
        }
    }
}