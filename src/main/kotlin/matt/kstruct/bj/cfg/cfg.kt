package matt.kstruct.bj.cfg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable enum class Native {
  LIB, APP
}


@Serializable class JvmConfig {
  val exec: JvmExecConfig? = null
}


@Serializable sealed interface JvmExecConfig {
  val useGradleStandardIn: Boolean
}

@SerialName("Typical")
@Serializable class TypicalJvmExecConfig: JvmExecConfig {
  override val useGradleStandardIn = false
}


const val STAGING_SUFFIX = "-staging"

@SerialName("Heroku")
@Serializable class HerokuJvmExecConfig: JvmExecConfig {
  val herokuAppName: String? = null
  override val useGradleStandardIn get() = true /*so I can use the terminal during local server testing to kill the server or other stuff*/
  val herokuStagingAppName get() = herokuAppName?.let { it + STAGING_SUFFIX }
}

@Serializable enum class JS {
  LIB, CLIENT
}
