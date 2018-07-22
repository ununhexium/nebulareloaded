package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.PlanViewport

class FractalPanelViewportSynchronizer(
    private vararg val panels: FractalPanel
) :
    ViewportListener {

  var synchronize: Boolean = false
  var forceRendering: Boolean = false

  override fun viewportChanged(source: Any, viewport: PlanViewport) {
    if (synchronize) {
      panels.filter { it != source }.forEach {
        it.viewport = viewport
        if(forceRendering) it.asyncUpdateRendering()
      }
    }
  }
}
