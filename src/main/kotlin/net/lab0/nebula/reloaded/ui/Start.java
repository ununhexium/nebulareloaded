package net.lab0.nebula.reloaded.ui;

import com.bulenkov.darcula.DarculaLaf;
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

    try {
      // Hack for Linux platforms to prevent a future NPE in the LaF setup
      UIManager.getFont("Label.font");
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(new DarculaLaf());
    }
    catch (Exception e) {
      // handle exception... or not
    }

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
