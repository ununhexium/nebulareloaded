package net.lab0.nebula.reloaded.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TreeBrowser {
  private JLabel realValue;
  private JLabel realLabel;
  private JLabel imgLabel;
  private JLabel imgValue;
  private JLabel xLabel;
  private JLabel xValue;
  private JLabel yLabel;
  private JLabel yValue;
  private JPanel controlPanel;
  private JPanel mainPanel;
  private MandelbrotPanel mandelbrotPanel;
  private JButton viewportReset;

  public TreeBrowser() {
    viewportReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mandelbrotPanel.resetViewport();
      }
    });
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  /**
   * To be called after the creation of this class to finish linking all the objects.
   */
  public void finishSetup() {
    linkMandelbrotPanel();
  }

  private void linkMandelbrotPanel() {
    /*
     * That's ugly...
     * Deal with it until this is converted to Java.
     */
    mandelbrotPanel.setRealValueLabel(realValue);
    mandelbrotPanel.setImgValueLabel(imgValue);
    mandelbrotPanel.setXValueLabel(xValue);
    mandelbrotPanel.setYValueLabel(yValue);

    MandelbrotActions mandelbrotActions = new MandelbrotActions(mandelbrotPanel);

    mandelbrotPanel.addMouseListener(mandelbrotActions);
    mandelbrotPanel.addMouseMotionListener(mandelbrotActions);
    mandelbrotPanel.addMouseWheelListener(mandelbrotActions);
    mandelbrotPanel.addKeyListener(mandelbrotActions);
    mandelbrotPanel.addComponentListener(mandelbrotActions);
  }
}
