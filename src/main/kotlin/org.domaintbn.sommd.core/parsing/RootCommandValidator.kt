package org.domaintbn.sommd.core.parsing

class RootCommandValidator{
    companion object {
        fun isValidRootCommand(str: String): Boolean {
            CommandStrings.values().forEach {
                if (str.startsWith(it.txt)) {
                    return true
                }
            }
            return false
        }
    }
}