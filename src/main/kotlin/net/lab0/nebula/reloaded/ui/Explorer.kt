package net.lab0.nebula.reloaded.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFrame


class Explorer {
    companion object {
        internal val log: Logger by lazy {
            LoggerFactory
                .getLogger(this::class.java.name)
        }
    }
}

fun main(args: Array<String>) {
    Explorer.log.error("Error test")
    Explorer.log.warn("Warning test")
    Explorer.log.info("Info test")
    Explorer.log.debug("Debug test")
    Explorer.log.trace("Trace test")

    val frame = JFrame("Tree Node Explorer")
    val treeBrowser = TreeBrowser()
    treeBrowser.finishSetup()
    frame.add(treeBrowser.mainPanel)
    frame.pack()
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}
