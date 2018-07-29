package net.lab0.nebula.reloaded.opencl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clGetDeviceInfo;
import static org.lwjgl.opencl.CL10.clGetMemObjectInfo;
import static org.lwjgl.opencl.CL10.clGetPlatformInfo;
import static org.lwjgl.opencl.CL10.clGetProgramBuildInfo;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memASCII;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public final class InfoUtil {

  private InfoUtil() {
  }

  public static String getPlatformInfoStringASCII(long cl_platform_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetPlatformInfo(cl_platform_id, param_name, (ByteBuffer) null, pp));
      int bytes = (int) pp.get(0);

      ByteBuffer buffer = stack.malloc(bytes);
      checkCLError(clGetPlatformInfo(cl_platform_id, param_name, buffer, null));

      return memASCII(buffer, bytes - 1);
    }
  }

  public static String getPlatformInfoStringUTF8(long cl_platform_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetPlatformInfo(cl_platform_id, param_name, (ByteBuffer) null, pp));
      int bytes = (int) pp.get(0);

      ByteBuffer buffer = stack.malloc(bytes);
      checkCLError(clGetPlatformInfo(cl_platform_id, param_name, buffer, null));

      return memUTF8(buffer, bytes - 1);
    }
  }

  public static int getDeviceInfoInt(long cl_device_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      IntBuffer pl = stack.mallocInt(1);
      checkCLError(clGetDeviceInfo(cl_device_id, param_name, pl, null));
      return pl.get(0);
    }
  }

  public static long getDeviceInfoLong(long cl_device_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      LongBuffer pl = stack.mallocLong(1);
      checkCLError(clGetDeviceInfo(cl_device_id, param_name, pl, null));
      return pl.get(0);
    }
  }

  public static long getDeviceInfoPointer(long cl_device_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetDeviceInfo(cl_device_id, param_name, pp, null));
      return pp.get(0);
    }
  }

  public static String getDeviceInfoStringUTF8(long cl_device_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetDeviceInfo(cl_device_id, param_name, (ByteBuffer) null, pp));
      int bytes = (int) pp.get(0);

      ByteBuffer buffer = stack.malloc(bytes);
      checkCLError(clGetDeviceInfo(cl_device_id, param_name, buffer, null));

      return memUTF8(buffer, bytes - 1);
    }
  }

  public static long getMemObjectInfoPointer(long cl_mem, int param_name) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetMemObjectInfo(cl_mem, param_name, pp, null));
      return pp.get(0);
    }
  }

  public static long getMemObjectInfoInt(long cl_mem, int param_name) {
    try (MemoryStack stack = stackPush()) {
      IntBuffer pi = stack.mallocInt(1);
      checkCLError(clGetMemObjectInfo(cl_mem, param_name, pi, null));
      return pi.get(0);
    }
  }

  public static int getProgramBuildInfoInt(long cl_program_id, long cl_device_id, int param_name) {
    try (MemoryStack stack = stackPush()) {
      IntBuffer pl = stack.mallocInt(1);
      checkCLError(clGetProgramBuildInfo(cl_program_id, cl_device_id, param_name, pl, null));
      return pl.get(0);
    }
  }

  public static String getProgramBuildInfoStringASCII(
      long cl_program_id,
      long cl_device_id,
      int param_name
  ) {
    try (MemoryStack stack = stackPush()) {
      PointerBuffer pp = stack.mallocPointer(1);
      checkCLError(clGetProgramBuildInfo(
          cl_program_id,
          cl_device_id,
          param_name,
          (ByteBuffer) null,
          pp
      ));
      int bytes = (int) pp.get(0);

      ByteBuffer buffer = stack.malloc(bytes);
      checkCLError(clGetProgramBuildInfo(cl_program_id, cl_device_id, param_name, buffer, null));

      return memASCII(buffer, bytes - 1);
    }
  }

  public static void checkCLError(IntBuffer errcode) {
    checkCLError(errcode.get(errcode.position()));
  }

  public static void checkCLError(int errcode) {
    if (errcode != CL_SUCCESS) {
      throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
    }
  }

}
