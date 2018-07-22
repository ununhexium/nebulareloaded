package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.PlanViewport
import java.util.EventListener

interface ViewportListener:EventListener {
  fun viewportChanged(source:Any, viewport: PlanViewport)
}