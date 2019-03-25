package net.lab0.nebula.reloaded.compute.mandelbrot

import com.google.common.io.Files
import com.google.common.io.Resources
import jcuda.Pointer
import jcuda.Sizeof
import jcuda.driver.CUcontext
import jcuda.driver.CUdevice
import jcuda.driver.CUdeviceptr
import jcuda.driver.CUfunction
import jcuda.driver.CUmodule
import jcuda.driver.JCudaDriver
import java.io.IOException

class CudaMandelbrotComputeEngine : MandelbrotComputeEngine {

  private val context by lazy {
    JCudaDriver.setExceptionsEnabled(true)

    // Initialize the driver and create a context for the first device.
    JCudaDriver.cuInit(0)
    val device = CUdevice()
    JCudaDriver.cuDeviceGet(device, 0)
    val context = CUcontext()
    JCudaDriver.cuCtxCreate(context, 0, device)

    context
  }

  private val ptxFileName by lazy {
    // calling the NVCC
    preparePtxFile("Mandelbrot.cu")
  }

  @Deprecated("Come on... this is executed on a GPU...")
  override fun iterationsAt(real: Double, img: Double, iterationLimit: Long): Long {
    return iterationsAt(doubleArrayOf(real), doubleArrayOf(img), iterationLimit).first()
  }

  override fun iterationsAt(reals: DoubleArray, imags: DoubleArray, iterationLimit: Long): LongArray {
    val items = reals.size

    // Load the ptx file.
    val module = CUmodule()
    JCudaDriver.cuModuleLoad(module, ptxFileName)

    // Obtain a function pointer to the "mandelbrot" function.
    val function = CUfunction()
    JCudaDriver.cuModuleGetFunction(function, module, "mandelbrot")

    // Allocate device memory
    val deviceReals = reals.copyToDevice()
    val deviceImags = imags.copyToDevice()

    val deviceIterations = CUdeviceptr()
    JCudaDriver.cuMemAlloc(deviceIterations, items.toLong() * Sizeof.LONG)

    // Set up the kernel parameters: A pointer to an array
    // of pointers which point to the actual values.
    val kernelParameters = Pointer.to(
        Pointer.to(deviceReals),
        Pointer.to(deviceImags),
        Pointer.to(longArrayOf(iterationLimit)),
        Pointer.to(deviceIterations)
    )

    val start = System.currentTimeMillis()
    // Call the kernel function.
    val blockSizeX = 512
    val gridSizeX = Math.ceil(items.toDouble() / blockSizeX).toInt()
    JCudaDriver.cuLaunchKernel(
        function,
        gridSizeX, 1, 1, // Grid dimension
        blockSizeX, 1, 1, // Block dimension
        0, null // Kernel- and extra parameters
        , // Shared memory size and stream
        kernelParameters, null
    )
    JCudaDriver.cuCtxSynchronize()
    println("Elapsed kernel time: " + (System.currentTimeMillis() - start) + "ms")

    // Copy the device output to the host.
    val hostIterations = LongArray(items)
    JCudaDriver.cuMemcpyDtoH(
        Pointer.to(hostIterations),
        deviceIterations,
        (items * Sizeof.LONG).toLong()
    )

    // Clean up.
    JCudaDriver.cuMemFree(deviceReals)
    JCudaDriver.cuMemFree(deviceImags)
    JCudaDriver.cuMemFree(deviceIterations)

    return hostIterations
  }

  private fun preparePtxFile(cuFileName: String): String {
    var endIndex = cuFileName.lastIndexOf('.')
    if (endIndex == -1) {
      endIndex = cuFileName.length - 1
    }
    val tmpDir = Files.createTempDir()
    val ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx"
    val ptxFile = tmpDir.toPath().resolve(ptxFileName).toFile()
    if (ptxFile.exists()) {
      return ptxFileName
    }


    val content = Resources.getResource(cuFileName).readText()
    val cudaFile = tmpDir.toPath().resolve(cuFileName).toFile()
    cudaFile.writeText(content)

    if (!cudaFile.exists()) {
      throw IOException("Input file not found: $cuFileName")
    }
    val modelString = "-m" + System.getProperty("sun.arch.data.model")
    val command = "/usr/local/cuda/bin/nvcc " + modelString + " -ptx " +
        cudaFile.getPath() + " -o " + ptxFileName

    println("Executing\n$command")
    val process = Runtime.getRuntime().exec(command)


    val errorMessage = String(process.errorStream.readBytes())
    val outputMessage = String(process.inputStream.readBytes())

    val exitValue: Int
    try {
      exitValue = process.waitFor()
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      throw IOException(
          "Interrupted while waiting for nvcc output", e
      )
    }


    if (exitValue != 0) {
      println("nvcc process exitValue $exitValue")
      println("errorMessage:\n$errorMessage")
      println("outputMessage:\n$outputMessage")
      throw IOException(
          "Could not create .ptx file: $errorMessage"
      )
    }

    println("Finished creating PTX file")
    return ptxFileName
  }


}

private fun DoubleArray.copyToDevice(): CUdeviceptr {
  val devicePointer = CUdeviceptr()
  JCudaDriver.cuMemAlloc(devicePointer, this.cuSize())
  JCudaDriver.cuMemcpyHtoD(devicePointer, this.cuPtr(), this.cuSize())
  return devicePointer
}

private fun DoubleArray.cuSize() = Sizeof.DOUBLE.toLong() * this.size

private fun DoubleArray.cuPtr() = Pointer.to(this)
