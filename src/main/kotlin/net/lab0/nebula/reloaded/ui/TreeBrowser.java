package net.lab0.nebula.reloaded.ui;

import net.lab0.nebula.reloaded.mandelbrot.ComputeEngine;
import net.lab0.nebula.reloaded.mandelbrot.ComputeEnginesKt;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
  private JPanel computePanel;
  private JComboBox<ComputeEngine> computeEngineComboBox;

  public TreeBrowser() {
    viewportReset.addActionListener(e -> mandelbrotPanel.resetViewport());
    computeEngineComboBox.addItemListener(e -> {
      ComputeEngine item = (ComputeEngine) e.getItem();
      mandelbrotPanel.setComputeEngine(item);
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
    populateComputeEngineList();
  }

  private void populateComputeEngineList() {
    ComputeEnginesKt.getComputeEngines().forEach(
        it -> computeEngineComboBox.addItem(it)
    );
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
