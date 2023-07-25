package main

import audio.OpenALManager
import listeners.InputListenerManager
import scene.Camera
import scene.Scene
import ui.UserInterface

object Game {
  fun startGame() {
    Strings.updateStrings(Strings.englishStringPath)
    Parameters.init()
    Window.init()
    Log.l("Project Version: " + Parameters.getProjectVersion())
    Scene.getInstance().init()
    GameStatus.setStatus(GameStatus.Status.RUNNING)
    Log.l("------------ GAME INITIATED! ------------")
  }

  fun update(timeElapsed: Long) {
    val startTime = System.nanoTime()
    val timeElapsedDividedBySpeedFactor = timeElapsed / GameTime.getTimeSpeedFactor()
    GameStatus.setRuntime(GameStatus.getRuntime() + timeElapsedDividedBySpeedFactor)
    InputListenerManager.updateMouseWorldCoordinates()
    InputListenerManager.updateControllerInputs()
    Camera.getInstance().update(timeElapsedDividedBySpeedFactor)
    Scene.getInstance().update(timeElapsedDividedBySpeedFactor)
    Weather.getInstance().update(timeElapsedDividedBySpeedFactor)
    GameTime.getInstance().update(timeElapsedDividedBySpeedFactor)
    UserInterface.getInstance().update(timeElapsedDividedBySpeedFactor)
    FramesPerSecond.updateUpdatingTimeNanoseconds(System.nanoTime() - startTime)
  }

  fun render() {
    val startTime = System.nanoTime()
    OpenGLManager.prepareFrame()
    OpenGLManager.updateShadersUniforms()
    Scene.getInstance().render()
    SpecialEffects.render()
    UserInterface.getInstance().render()
    FramesPerSecond.updateRenderingTimeNanoseconds(System.nanoTime() - startTime)
  }

  fun stopGame() {
    OpenALManager.destroy()
    InputListenerManager.release()
    Log.closeLogFile()
  }
}
