package net.lab0.nebula.reloaded.ui

import javax.swing.JFrame

class Explorer {
}

fun main(args: Array<String>) {
    val frame = JFrame("Tree Node Explorer")
    frame.add(TreeBrowser().mainPanel)
    frame.pack()
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}
