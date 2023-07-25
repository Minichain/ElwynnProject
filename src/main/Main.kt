package main

import org.lwjgl.glfw.GLFW
import java.io.File
import kotlin.system.exitProcess

fun main() {
  //Load natives files
  System.setProperty("org.lwjgl.librarypath", File("natives/windows/x64").absolutePath)
  initializeGLFW()
  Log.l("OS Name " + System.getProperty("os.name"))
  Log.l("OS Version " + System.getProperty("os.version"))
  Game.startGame()
  runGameLoopUntilStopped()
  exit()
}

private fun initializeGLFW() {
  if (!GLFW.glfwInit()) {
    System.err.println("GLFW Failed to initialize!")
    exitProcess(0)
  }
}

private fun runGameLoopUntilStopped() {
  var timeElapsedNanos: Long = 0
  var lastUpdateTime = System.nanoTime()
  var maxTimeBetweenFrames: Long
  while (gameIsRunning()) {
    maxTimeBetweenFrames = (1000000000 / Parameters.getFramesPerSecond()).toLong()
    FramesPerSecond.update(1000000000f / timeElapsedNanos)
    updateAndRender(timeElapsedNanos / 1000000)
    //Wait time until processing next frame. FPS locked.
    timeElapsedNanos = System.nanoTime() - lastUpdateTime
    while (timeElapsedNanos < maxTimeBetweenFrames) {
      timeElapsedNanos = System.nanoTime() - lastUpdateTime
    }
    lastUpdateTime = System.nanoTime()
  }
}

private fun gameIsRunning() = !GLFW.glfwWindowShouldClose(Window.getWindow()) && GameStatus.getStatus() != GameStatus.Status.STOPPED

private fun updateAndRender(timeElapsedMillis: Long) {
  Game.update(timeElapsedMillis)
  Game.render()
  GLFW.glfwSwapBuffers(Window.getWindow())
  GLFW.glfwPollEvents()
}

private fun exit() {
  Game.stopGame()
  GLFW.glfwTerminate()
  exitProcess(0)
}