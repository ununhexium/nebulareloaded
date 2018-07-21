package net.lab0.nebula.reloaded.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.UIManager


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

  try {
    // Set cross-platform Java L&F (also called "Metal")
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  }
  catch (e: Exception) {
    // handle exception... or not
  }

  val frame = JFrame("Tree Node Explorer")
  val treeBrowser = TreeBrowser()
  EventQueue.invokeLater {
    treeBrowser.finishSetup()
    frame.add(treeBrowser.mainPanel)
    frame.pack()
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
  }
}
