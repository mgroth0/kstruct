package matt.kstruct.mod.srcset

import matt.kstruct.bj.BuildJsonModule
import matt.kstruct.bj.MultiPlatformModule
import matt.kstruct.mod.srcset.SourceSets.commonMain
import matt.kstruct.mod.srcset.SourceSets.main
import matt.lang.platform.SourceTypeInter
import matt.model.code.idea.ModIdea


enum class SourceSets(val isTest: Boolean = false) : ModIdea, SourceTypeInter {
    main,
    test(isTest = true),
    resources,
    commonMain,
    jvmMain,
    jsMain,
    nativeMain,
    commonTest(isTest = true),
    jvmTest(isTest = true),
    jsTest(isTest = true),
    nativeTest(isTest = true),
    androidMain,
    androidTest(isTest = true),
    commonJvmAndroidMain,
    commonJvmAndroidTest(isTest = true);


}


val BuildJsonModule.mainSourceSet get() = if (this is MultiPlatformModule) commonMain.name else main.name