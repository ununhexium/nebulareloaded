package net.lab0.nebula.reloaded.ui;

import net.lab0.nebula.reloaded.compute.mandelbrot.Engines;
import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext;
import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeEngine;
import net.lab0.nebula.reloaded.image.color.ColorScheme;
import net.lab0.nebula.reloaded.image.color.ColorSchemes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.atomic.AtomicReference;

public class TreeBrowser {

  private static final Logger log = LoggerFactory.getLogger(TreeBrowser.class);


  private JLabel realValue;
  private JLabel imgValue;
  private JLabel xValue;
  private JLabel yValue;
  private JPanel controlPanel;
  private JPanel mainPanel;
  private MandelbrotPanel mandelbrotPanel;
  private JButton viewportReset;
  private JPanel computePanel;
  private JComboBox<MandelbrotComputeEngine> computeEngineComboBox;
  private JTabbedPane tabs;
  private JCheckBox drawFractal;
  private JSpinner iterationLimit;
  private JCheckBox drawNodes;
  private JButton moreNodesButton;
  private SurfaceIndicator surfaceIndicator;
  private JLabel iterationsValue;
  private NebulabrotPanel nebulabrotPanel;
  private JTabbedPane renderingsTabs;
  private JCheckBox syncCheckbox;
  private JCheckBox forceRenderingCheckbox;
  private JSplitPane innerSplit;
  private JComboBox<ColorScheme> mandelbrotColorSchemeComboBox;
  private JSpinner nebulaMinimumIteration;
  private JSpinner nebulaMaximumIteration;
  private JSpinner resolutionSelector;
  private JLabel resolutionLabel;

  // CUSTOM
  private MandelbrotComputeContext context;
  private AtomicReference<MandelbrotComputeContext> computeContextRef;
  private FractalPanelViewportSynchronizer synchronizer;

  public TreeBrowser() {
    viewportReset.addActionListener(
        e -> {
          mandelbrotPanel.resetViewport();
          nebulabrotPanel.resetViewport();
        }
    );
    computeEngineComboBox.addItemListener(e -> {
      MandelbrotComputeEngine item = (MandelbrotComputeEngine) e.getItem();
      mandelbrotPanel.setComputeEngine(item);
    });
    drawFractal.addActionListener(
        e -> mandelbrotPanel.setShowFractal(drawFractal.isSelected())
    );
    iterationLimit.addChangeListener(
        e -> mandelbrotPanel.setIterationLimit(((Number) iterationLimit.getValue()).longValue())
    );
    drawNodes.addActionListener(
        e -> mandelbrotPanel.setShowTree(drawNodes.isSelected())
    );
    moreNodesButton.addActionListener(
        e -> computeContextRef.get().computeTreeOnce(
            () -> {
              triggerAreasRefresh();
              mandelbrotPanel.asyncUpdateRendering();
              return null;
            })
    );

    finishSetup();
    syncCheckbox.addActionListener(
        e -> synchronizer.setSynchronize(syncCheckbox.isSelected())
    );
    forceRenderingCheckbox.addActionListener(
        e -> synchronizer.setForceRendering(forceRenderingCheckbox.isSelected())
    );
    innerSplit.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        innerSplit.setDividerLocation(0.9);
      }
    });
    mandelbrotColorSchemeComboBox.addActionListener(
        e -> {
          mandelbrotPanel.setColorScheme(mandelbrotColorSchemeComboBox.getItemAt(
              mandelbrotColorSchemeComboBox.getSelectedIndex()));
          mandelbrotPanel.asyncUpdateRendering();
        }
    );
    nebulaMinimumIteration.addChangeListener(
        e -> {
          nebulabrotPanel.setMinIterations((Integer) nebulaMinimumIteration.getValue());
          nebulabrotPanel.asyncUpdateRendering();
        }
    );
    nebulaMaximumIteration.addChangeListener(
        e -> {
          nebulabrotPanel.setMaxIterations((Integer) nebulaMaximumIteration.getValue());
          nebulabrotPanel.asyncUpdateRendering();
        }
    );
    resolutionSelector.addChangeListener(
        e -> {
          updateResolutionIndex((Integer) resolutionSelector.getValue());
          nebulabrotPanel.asyncUpdateRendering();
        }
    );
  }

  private void triggerAreasRefresh() {
    new SwingWorker<Void, Void>() {
      private InEdgeOutUndef surfaces;

      @Override
      protected Void doInBackground()
      throws Exception {
        surfaces = computeContextRef.get().getInEdgeOutSurfaces();
        return null;
      }

      @Override
      protected void done() {
        log.debug("New surfaces are " + surfaces);
        /*
         * using undef as edge surface as these were the previous edge surfaces.
         */
        surfaceIndicator.setEdge(surfaces.getUndef());
        surfaceIndicator.setInside(surfaces.getInside());
        surfaceIndicator.setOutside(surfaces.getOutside());
        surfaceIndicator.repaint();
      }
    }.execute();
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  /**
   * To be called after the creation of this class to finish linking all the objects.
   */
  public void finishSetup() {
    linkFractalPanels();
    populate();
    setTabsNames();
    initIterations();
    setDisplayOptions();
    refreshDisplays();
  }

  private void populate() {
    populateComputeEngineList();
    populateMandelbrotColorScheme();
  }

  private void populateMandelbrotColorScheme() {
    ColorSchemes.INSTANCE.forEach(
        it -> mandelbrotColorSchemeComboBox.addItem(it)
    );
  }

  private void refreshDisplays() {
    mandelbrotPanel.asyncUpdateRendering();
    nebulabrotPanel.asyncUpdateRendering();
  }

  private void setDisplayOptions() {
    drawFractal.setSelected(true);
    mandelbrotPanel.setShowTree(drawNodes.isSelected());
  }

  private void initIterations() {
    iterationLimit.setValue(128);
    mandelbrotPanel.setIterationLimit((Integer) iterationLimit.getValue());

    nebulaMinimumIteration.setValue(100);
    nebulabrotPanel.setMinIterations((Integer) nebulaMinimumIteration.getValue());

    nebulaMaximumIteration.setValue(4096);
    nebulabrotPanel.setMaxIterations((Integer) nebulaMaximumIteration.getValue());

    resolutionSelector.setValue(10);
    updateResolutionIndex(10);
  }

  private void updateResolutionIndex(int resolutionIndex) {
    nebulabrotPanel.setResolutionIndex(resolutionIndex);
    resolutionLabel.setText(String.valueOf(1.0 / Math.pow(2, resolutionIndex)));
  }

  private void setTabsNames() {
    tabs.setTitleAt(0, "Control");
    tabs.setTitleAt(1, "Compute");
    renderingsTabs.setTitleAt(0, "Mandelbrot");
    renderingsTabs.setTitleAt(1, "Nebulabrot");
  }

  private void populateComputeEngineList() {
    Engines.INSTANCE.getComputeEngines().forEach(
        it -> computeEngineComboBox.addItem(it)
    );
  }

  private void linkFractalPanels() {
    FractalActions mandelbrotActions = new FractalActions(mandelbrotPanel);
    FractalActions nebulaActions = new FractalActions(nebulabrotPanel);

    linkLabels(nebulaActions);
    linkLabels(mandelbrotActions);

    linkActions(nebulaActions, nebulabrotPanel);
    linkActions(mandelbrotActions, mandelbrotPanel);

    synchronizer = new FractalPanelViewportSynchronizer(
        mandelbrotPanel, nebulabrotPanel
    );
    synchronizer.setSynchronize(syncCheckbox.isSelected());
    synchronizer.setSynchronize(forceRenderingCheckbox.isSelected());

    mandelbrotPanel.addViewportListener(synchronizer);
    nebulabrotPanel.addViewportListener(synchronizer);
  }

  private void linkActions(
      FractalActions fractalActions,
      FractalPanel fractalPanel
  ) {
    fractalPanel.addMouseListener(fractalActions);
    fractalPanel.addMouseMotionListener(fractalActions);
    fractalPanel.addMouseWheelListener(fractalActions);
    fractalPanel.addKeyListener(fractalActions);
    fractalPanel.addComponentListener(fractalActions);
  }

  private void linkLabels(FractalActions fractalActions) {
    fractalActions.setRealValueLabel(realValue);
    fractalActions.setImgValueLabel(imgValue);
    fractalActions.setIterationsValueLabel(iterationsValue);

    fractalActions.setXValueLabel(xValue);
    fractalActions.setYValueLabel(yValue);
  }

  private void createUIComponents() {
    context = new MandelbrotComputeContext();
    computeContextRef = new AtomicReference<>(context);
    mandelbrotPanel = new MandelbrotPanel(computeContextRef);
    nebulabrotPanel = new NebulabrotPanel(computeContextRef);
  }
}
