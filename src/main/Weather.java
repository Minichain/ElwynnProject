package main;

import audio.OpenALManager;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;

import java.awt.*;

public class Weather {
    private static Weather instance = null;
    private static float timeUntilNextCheck = 1f * 3600f * 1000f / GameTime.getGameTimeRealTimeFactor();  //milliseconds
    private static float timeElapsedUntilNextCheck;  //milliseconds
    private static WeatherStatus weatherStatus;
    private static float rainingIntensity;

    public enum WeatherStatus {
        CLEAR, RAINING
    }

    private Weather() {
        weatherStatus = WeatherStatus.RAINING;
        timeElapsedUntilNextCheck = 0;
        rainingIntensity = 0f;
    }

    public static Weather getInstance() {
        if (instance == null) {
            instance = new Weather();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            OpenALManager.stopPlayingSound(OpenALManager.SOUND_RAIN_01);
            return;
        }

        timeElapsedUntilNextCheck += (float) timeElapsed;
        if (timeElapsedUntilNextCheck >= timeUntilNextCheck) {
            if (Math.random() >= 0.5) {
                setWeatherStatus(WeatherStatus.CLEAR);
            } else {
                setWeatherStatus(WeatherStatus.RAINING);
            }
            timeElapsedUntilNextCheck = 0;
        }

        if (isRaining()) {
            rainingIntensity += timeElapsed / 20000f;
            if (rainingIntensity > 1f) rainingIntensity = 1f;
        } else {
            rainingIntensity -= timeElapsed / 20000f;
            if (rainingIntensity < 0f) rainingIntensity = 0f;
        }

        if (rainingIntensity > 0f && !Scene.getInstance().isIndoors()) {
            OpenALManager.playSound(OpenALManager.SOUND_RAIN_01);
            OpenALManager.setSoundGain(OpenALManager.SOUND_RAIN_01, rainingIntensity * Parameters.getAmbienceSoundLevel());
            int newParticlesToGenerate = (int) (timeElapsed * rainingIntensity);
            for (int i = 0; i < newParticlesToGenerate; i++) {
                double width = 750.0;
                double height = 800.0;
                double x = Camera.getInstance().getCoordinates().x - width / 2 + Math.random() * width;
                double y = Camera.getInstance().getCoordinates().y - height / 2 + Math.random() * (height * 0.25);
                double[] velocityVector = new double[] {- 0.25 - Math.random() * 0.2, 2.5 + Math.random() * 0.4};
                float particleSize = (float) (Math.random() * 2.0);
                Particle[] newParticles = new Particle[5];
                for (int j = 0; j < 5; j++) {
                    newParticles[j] = new Particle(
                            new Coordinates(x - j * velocityVector[0] * particleSize / 3.0, y - j * velocityVector[1] * particleSize / 3.0),
                            velocityVector, 0.25, particleSize, new Color(0.25f, 0.25f, 0.5f), 750.0, false);
                }
                ParticleManager.getInstance().addParticles(newParticles);
            }
        } else {
            OpenALManager.stopPlayingSound(OpenALManager.SOUND_RAIN_01);
        }
    }

    public static WeatherStatus getWeatherStatus() {
        return weatherStatus;
    }

    public static void setWeatherStatus(WeatherStatus weatherStatus) {
        Log.l("Set Weather to " + weatherStatus);
        Weather.weatherStatus = weatherStatus;
    }

    public static boolean isRaining() {
        return getWeatherStatus() == WeatherStatus.RAINING;
    }

    public static float getRainingIntensity() {
        return rainingIntensity;
    }
}
