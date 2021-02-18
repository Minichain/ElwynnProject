package console;

import entities.Player;
import items.ItemType;
import main.Coordinates;
import main.GameTime;
import main.Log;
import main.Weather;
import scene.TileMap;

public class ConsoleInputProcessor {

    public static void processInput(String input) {
        if (input.startsWith("/")) {
            if (input.startsWith("/weather")) {
                parseWeatherCommand(input);
            } else if (input.startsWith("/time")) {
                parseTimeCommand(input);
            } else if (input.startsWith("/tp")) {
                parseTeleportCommand(input);
            } else if (input.startsWith("/reset")) {
                parseResetCommand(input);
            } else if (input.startsWith("/give")) {
                parseGiveCommand(input);
            } else {
                Log.e("Invalid command");
            }
        } else {
            Log.l(input);
        }
    }

    private static void parseGiveCommand(String command) {
        String[] inputSplit = command.split(" ");
        try {
            int itemType = Integer.parseInt(inputSplit[1]);
            int amount = 1;
            if (inputSplit.length > 2) {
                amount = Integer.parseInt(inputSplit[2]);
            }
            Player.getInstance().getInventory().storeItem(ItemType.values()[itemType].getItem(), amount);
        } catch (Exception e) {
            Log.e("Invalid command");
        }
    }

    private static void parseResetCommand(String command) {
        String[] inputSplit = command.split(" ");
        if (inputSplit[1].equals("tilemap")) {
            TileMap.setArrayOfTiles(null);
        } else {
            Log.e("Invalid reset command");
        }
    }

    private static void parseTeleportCommand(String command) {
        String[] inputSplit = command.split(" ");
        try {
            int x = Integer.parseInt(inputSplit[1]);
            int y = Integer.parseInt(inputSplit[2]);
            Player.getInstance().setWorldCoordinates(new Coordinates(x, y));
        } catch (Exception e) {
            Log.e("Invalid coordinates");
        }
    }

    private static void parseTimeCommand(String command) {
        String[] inputSplit = command.split(" ");
        try {
            float time = Float.parseFloat(inputSplit[1]);
            if (time >= 0) {
                GameTime.setGameTime(time);
            } else {
                Log.e("Invalid time");
            }
        } catch (Exception e) {
            Log.e("Invalid time");
        }
    }

    private static void parseWeatherCommand(String command) {
        String[] inputSplit = command.split(" ");
        if (inputSplit[1].equals("clear")) {
            Weather.setWeatherStatus(Weather.WeatherStatus.CLEAR);
        } else if (inputSplit[1].equals("rain") || inputSplit[1].equals("raining")) {
            Weather.setWeatherStatus(Weather.WeatherStatus.RAINING);
        } else {
            Log.e("Unrecognized command");
        }
    }
}
