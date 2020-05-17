package main;

import audio.OpenALManager;
import entities.Player;
import enums.Resolution;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;

public class Weather {
    private static Weather instance = null;
    private static WeatherStatus weatherStatus;
    private static float timeUntilNextCheck = 1f * 3600f * 1000f / GameTime.getGameTimeRealTimeFactor();  //milliseconds
    private static float timeElapsedUntilNextCheck;  //milliseconds

    public enum WeatherStatus {
        CLEAR, RAINING
    }

    private Weather() {
        weatherStatus = WeatherStatus.CLEAR;
        timeElapsedUntilNextCheck = 0;
    }

    public static Weather getInstance() {
        if (instance == null) {
            instance = new Weather();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
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
            OpenALManager.playSound(OpenALManager.SOUND_RAIN_01);
            int newParticlesToGenerate = (int) timeElapsed;
            for (int i = 0; i < newParticlesToGenerate; i++) {
                double width = 1400.0 * Window.getWidth() / Resolution.RESOLUTION_1920_1080.getResolution()[0];
                double height = 600.0 * Window.getHeight() / Resolution.RESOLUTION_1920_1080.getResolution()[1];
                double x = Player.getInstance().getWorldCoordinates().x - (width / Camera.getZoom()) + Math.random() * (width * 2.0/ Camera.getZoom());
                double y = Player.getInstance().getWorldCoordinates().y - (height / Camera.getZoom() + Math.random() * (height * 0.2 / Camera.getZoom()));
                double[] velocityVector = new double[] {- 0.25 - Math.random() * 0.2, 2.5 + Math.random() * 0.4};
                float particleSize = (float) (Math.random() * 2.0);
                Particle newParticle = new Particle(new Coordinates(x, y), velocityVector, particleSize, 0.25f, 0.25f, 0.5f, 4000.0);
                ParticleManager.getInstance().addParticle(newParticle);
            }
        } else {
            OpenALManager.stopPlayingSound(OpenALManager.SOUND_RAIN_01);
        }
    }

    public static WeatherStatus getWeatherStatus() {
        return weatherStatus;
    }

    public static void setWeatherStatus(WeatherStatus weatherStatus) {
        Weather.weatherStatus = weatherStatus;
    }

    public static boolean isRaining() {
        return getWeatherStatus() == WeatherStatus.RAINING;
    }
}
