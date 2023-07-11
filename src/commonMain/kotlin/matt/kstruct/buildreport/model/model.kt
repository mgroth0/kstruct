package matt.kstruct.buildreport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import matt.model.code.mod.GradleTaskPath
import matt.time.UnixTime

@Serializable
class TaskInfo(
    val path: GradleTaskPath,
    var dependencies: List<GradleTaskPath>,
    var result: TaskResult? = null
) {
    fun isInRootProject() = path.isTaskOfRootProject()
}

@Serializable
sealed interface TaskResult

@Serializable
@SerialName("Skipped")
class TaskSkip : TaskResult


@Serializable
@SerialName("Executed")
class TaskExecution(
    val start: UnixTime,
    val end: UnixTime,
    val succeeded: Boolean
) : TaskResult {
    val duration by lazy {
        end - start
    }

}