package ca.rightsomegoodgames.mayacharm.resources

import com.intellij.openapi.application.PathManager
import java.nio.file.Paths

class PythonStrings {
    val OPEN_LOG: String = "import maya.cmds as cmds; cmds.cmdFileOutput(o=r\"{0}\")"
    val CLOSE_LOG: String = "import maya.cmds as cmds; cmds.cmdFileOutput(closeAll=True)"
    val EXECFILE: String = "python (\"execfile (\\\"{0}\\\")\");"

    val SETTRACE = "import pydevd; pydevd.settrace(\"%1\$s\", port=%2\$s, suspend=%3\$s, stdoutToServer=%4\$s, stderrToServer=%4\$s)"
    val STOPTRACE: String = "import pydevd; pydevd.stoptrace()"

    val PYSTDERR: String = "# Error: "
    val PYSTDWRN: String = "# Warning: "

    private val _cmdportSetupScript: String
    private val _pydevSetupScript: String

    val cmdportSetupScript: String
        get() = this._cmdportSetupScript

    val pydevSetupScript: String
        get() = this._pydevSetupScript

    init {
        _cmdportSetupScript = readStringFromResource("python/command_port_setup.py")
        val pyDevPath = Paths.get(PathManager.getHomePath(), "debug-eggs", "pycharm-debug.egg").toString()
        _pydevSetupScript = readStringFromResource("python/pydev_setup.py").format(pyDevPath)
    }

    companion object {
        @JvmField
        val INSTANCE: PythonStrings = PythonStrings()
        fun readStringFromResource(resource: String): String {
            return PythonStrings::class.java.classLoader.getResource(resource).readText()
        }
    }
}
