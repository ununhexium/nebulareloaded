package net.lab0.nebula.reloaded.image

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RasterizationContextTest {
    @Test
    fun `can convert in square viewport and square image`() {
        val context = RasterizationContext(
            PlanViewport(
                -1 to 1,
                -1 to 1
            ),
            1000,
            1000
        )

        assertThat(context.ratio).isEqualTo(1.0)
        assertThat(context.pixelSide).isEqualTo(2.0 / 1000)

        assertThat(context.xCenter).isEqualTo(1000 / 2)
        assertThat(context.yCenter).isEqualTo(1000 / 2)

        val convert1 = context.convert(PlanCoordinates(1, 1))
        assertThat(convert1.x).isEqualTo(1000)
        assertThat(convert1.y).isEqualTo(0)

        val convert2 = context.convert(PlanCoordinates(-1, -1))
        assertThat(convert2.x).isEqualTo(0)
        assertThat(convert2.y).isEqualTo(1000)

        val convert3 = context.convert(PlanCoordinates(0, 0))
        assertThat(convert3.x).isEqualTo(1000 / 2)
        assertThat(convert3.y).isEqualTo(1000 / 2)


        val unconvert1 = context.convert(ImageCoordinates(0, 0))
        assertThat(unconvert1.real).isEqualTo(-1.0)
        assertThat(unconvert1.img).isEqualTo(1.0)

        val unconvert2 = context.convert(ImageCoordinates(1000, 1000))
        assertThat(unconvert2.real).isEqualTo(1.0)
        assertThat(unconvert2.img).isEqualTo(-1.0)

        val unconvert3 = context.convert(ImageCoordinates(500, 500))
        assertThat(unconvert3.real).isEqualTo(0.0)
        assertThat(unconvert3.real).isEqualTo(0.0)
    }

    @Test
    fun `can convert in viewport and 2-1 ratio image`() {
        val context = RasterizationContext(
            PlanViewport(
                -1 to 1,
                -1 to 1
            ),
            1000,
            500
        )

        assertThat(context.ratio).isEqualTo(2.0)
        assertThat(context.pixelSide).isEqualTo(2.0 / 500)

        assertThat(context.xCenter).isEqualTo(1000 / 2)
        assertThat(context.yCenter).isEqualTo(500 / 2)

        val convert1 = context.convert(PlanCoordinates(1, 1))
        assertThat(convert1.x).isEqualTo(750)
        assertThat(convert1.y).isEqualTo(0)

        val convert2 = context.convert(PlanCoordinates(-1, -1))
        assertThat(convert2.x).isEqualTo(250)
        assertThat(convert2.y).isEqualTo(500)

        val convert3 = context.convert(PlanCoordinates(0, 0))
        assertThat(convert3.x).isEqualTo(1000 / 2)
        assertThat(convert3.y).isEqualTo(500 / 2)


        val unconvert1 = context.convert(ImageCoordinates(0, 0))
        assertThat(unconvert1.real).isEqualTo(-2.0)
        assertThat(unconvert1.img).isEqualTo(1.0)

        val unconvert2 = context.convert(ImageCoordinates(1000, 500))
        assertThat(unconvert2.real).isEqualTo(2.0)
        assertThat(unconvert2.img).isEqualTo(-1.0)

        val unconvert3 = context.convert(ImageCoordinates(500, 250))
        assertThat(unconvert3.real).isEqualTo(0.0)
        assertThat(unconvert3.real).isEqualTo(0.0)
    }

    @Test
    fun `can convert in viewport and 1-2 ratio image`() {
        val context = RasterizationContext(
            PlanViewport(
                -1 to 1,
                -1 to 1
            ),
            500,
            1000
        )

        assertThat(context.ratio).isEqualTo(0.5)
        assertThat(context.pixelSide).isEqualTo(2.0 / 500)

        assertThat(context.xCenter).isEqualTo(500 / 2)
        assertThat(context.yCenter).isEqualTo(1000 / 2)

        val convert1 = context.convert(PlanCoordinates(1, 1))
        assertThat(convert1.x).isEqualTo(500)
        assertThat(convert1.y).isEqualTo(250)

        val convert2 = context.convert(PlanCoordinates(-1, -1))
        assertThat(convert2.x).isEqualTo(0)
        assertThat(convert2.y).isEqualTo(750)

        val convert3 = context.convert(PlanCoordinates(0, 0))
        assertThat(convert3.x).isEqualTo(500 / 2)
        assertThat(convert3.y).isEqualTo(1000 / 2)


        val unconvert1 = context.convert(ImageCoordinates(0, 0))
        assertThat(unconvert1.real).isEqualTo(-1.0)
        assertThat(unconvert1.img).isEqualTo(2.0)

        val unconvert2 = context.convert(ImageCoordinates(500, 1000))
        assertThat(unconvert2.real).isEqualTo(1.0)
        assertThat(unconvert2.img).isEqualTo(-2.0)

        val unconvert3 = context.convert(ImageCoordinates(250, 1000))
        assertThat(unconvert3.real).isEqualTo(0.0)
        assertThat(unconvert3.real).isEqualTo(0.0)
    }

}
