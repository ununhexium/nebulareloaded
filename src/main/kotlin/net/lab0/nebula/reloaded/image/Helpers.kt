package net.lab0.nebula.reloaded.image

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO


fun save(image: BufferedImage, to: String = "saved", type: String) {
  val outputfile = File("$to.$type")
  val parent = outputfile.toPath().parent
  if (!Files.isDirectory(parent)) {
    parent.toFile().mkdirs()
  }
  ImageIO.write(image, type, outputfile)
}

fun Color.withAlpha(alpha: Number) =
    Color(
        this.colorSpace,
        this.getColorComponents(null),
        alpha.toFloat()
    )
