package net.lab0.nebula.reloaded.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.EventQueue;

public class Start {

  private static final Logger log = LoggerFactory.getLogger(Start.class);


  public static void main(String ... args){
    log.error("Error test");
    log.warn("Warning test");
    log.info("Info test");
    log.debug("Debug test");
    log.trace("Trace test");

    JFrame frame = new JFrame("Tree Node Explorer");
    TreeBrowser treeBrowser = new TreeBrowser();
    EventQueue.invokeLater(
        () -> {
          frame.add(treeBrowser.getMainPanel());
          frame.pack();
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setVisible(true);
        }
    );
  }
}
