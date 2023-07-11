package matt.kstruct.proj

import matt.file.MFile
import matt.file.commons.IdeProject
import matt.lang.err

fun MFile.projectNameRelativeToRoot(root: IdeProject): String {
  return when {

	parentFile in root.subRootFolders + root.folder -> name

	root.subRootFolders.any { this in it }          -> {
	  val subRoot = root.subRootFolders.first { this in it }
	  relativeTo(subRoot).cpath.replace(MFile.separator, "-")
	}

	else                                            -> err("how to set name of ${this}?")
  }
}

