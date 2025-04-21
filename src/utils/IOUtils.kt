package utils;

import java.io.*
import java.nio.*
import java.nio.channels.*
import java.nio.charset.Charset
import java.nio.file.*
import org.lwjgl.BufferUtils.*

/**
 * Reads the specified resource and returns the raw data as a ByteBuffer.
 *
 * @param resource   the resource to read
 * @param bufferSize the initial buffer size
 *
 * @return the resource data
 *
 * @throws IOException if an IO error occurs
 */
fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
  val path = Paths.get(resource)
  if (Files.isReadable(path)) {
    val fc = Files.newByteChannel(path)
    val buffer = createByteBuffer(fc.size().toInt() + 1)
    while (fc.read(buffer) != -1) {
      //Nothing
    }
    buffer.flip()
    return buffer
  } else {
    val buffer = createByteBuffer(bufferSize);
    object {}.javaClass.classLoader.getResourceAsStream(resource)?.let { inputStream ->
      val rbc = Channels.newChannel(inputStream)
      while (true) {
        val bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
        }
      }
      buffer.flip()
    }
    return buffer
  }
}

fun readFile(path: String, encoding: Charset): String {
  val encoded = Files.readAllBytes(Paths.get(path))
  return String(encoded, encoding)
}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
  val newBuffer = createByteBuffer(newCapacity)
  buffer.flip()
  newBuffer.put(buffer)
  return newBuffer
}
