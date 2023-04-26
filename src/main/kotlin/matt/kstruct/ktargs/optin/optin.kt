package matt.kstruct.ktargs.optin

/*https://kotlinlang.org/docs/opt-in-requirements.html#module-wide-opt-in*/
enum class KotlinOptIn(val qname: String) {
    Contracts("kotlin.contracts.ExperimentalContracts"),
    InternalSerialization("kotlinx.serialization.InternalSerializationApi"),
    ExperimentalSerialization("kotlinx.serialization.ExperimentalSerializationApi"),
    ExperimentalUnsignedTypes("kotlin.ExperimentalUnsignedTypes"),
    ExperimentalStdLibAPI("kotlin.ExperimentalStdlibApi"),
    DelicateCoroutinesApi("kotlinx.coroutines.DelicateCoroutinesApi"),
    ExperimentalLettuceCoroutinesApi("io.lettuce.core.ExperimentalLettuceCoroutinesApi");

    val arg = "-opt-in=$qname"
}



