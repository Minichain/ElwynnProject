package main;

import entities.*;
import enums.NonPlayerCharacterAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import scene.Camera;
import scene.Scene;
import scene.Tile;
import scene.TileMap;
import utils.IOUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class WorldLoader {
    private static WorldLoader instance = null;
    private static final String worldFilePath = "res/world/";

    private WorldLoader() {
        Scene.getInstance().setSceneName("scene01");
    }

    public static WorldLoader getInstance() {
        if (instance == null) {
            instance = new WorldLoader();
        }
        return instance;
    }

    /** SAVE WORLD **/

    public void saveWorld() {
        try {
            SaveSceneThread thread = new SaveSceneThread(Scene.getInstance().getSceneName());
            thread.start();
        } catch (Exception e) {
            Log.e("World could not be saved");
        }
    }

    private static class SaveSceneThread extends Thread {
        private String sceneToSave;

        public SaveSceneThread(String sceneToSave) {
            this.sceneToSave = sceneToSave;
        }

        public void run() {
            try {
                String worldFileContentOld = IOUtils.readFile("res/world/world.txt", StandardCharsets.UTF_8);
//                Log.l(worldFileContentOld);

                String startSceneString = "start_scene:name=\"" + sceneToSave + "\"";
                int startSceneIndex = worldFileContentOld.indexOf(startSceneString);
                String endSceneString = "end_scene:name=\"" + sceneToSave + "\"";
                int endSceneIndex = worldFileContentOld.indexOf(endSceneString) + endSceneString.length();

                StringBuilder worldFileContentNew = new StringBuilder();
                worldFileContentNew.append(worldFileContentOld, 0, startSceneIndex);
                worldFileContentNew.append(worldFileContentOld, endSceneIndex, worldFileContentOld.length());

                worldFileContentNew.append("start_scene:name=\"").append(sceneToSave).append("\"").append("\n");
                worldFileContentNew.append("default_spawn_coordinates: ").append("x=\"800.0\",y=\"800.0\"").append("\n");
                worldFileContentNew.append("tiles:").append("width=\"").append(Integer.toString(TileMap.getNumOfHorizontalTiles())).append("\"")
                        .append(",height=\"").append(Integer.toString(TileMap.getNumOfVerticalTiles())).append("\"").append("\n");

                /** TILES **/
                Tile tile;
                for (int j = 0; j < TileMap.getNumOfVerticalTiles(); j++) {
                    for (int i = 0; i < TileMap.getNumOfHorizontalTiles(); i++) {
                        tile = TileMap.getArrayOfTiles()[i][j];
                        worldFileContentNew.append(Integer.toString(tile.getLayerValue(0))).append(",").append(tile.isCollidable() ? "1" : "0").append(";");
                    }
                }
                worldFileContentNew.append("\n");

                /** ENTITIES **/
                for (StaticGraphicEntity entity : Scene.getInstance().getListOfStaticEntities()) {
                    boolean isWarpEntity = entity.getEntityCode().equals(LargeWarp.ENTITY_CODE) || entity.getEntityCode().equals(SmallWarp.ENTITY_CODE);
                    if (isWarpEntity) worldFileContentNew.append("entity_warp:code=\"");
                    else worldFileContentNew.append("entity:code=\"");
                    worldFileContentNew.append(entity.getEntityCode());
                    worldFileContentNew.append("\",type=\"");
                    worldFileContentNew.append(Integer.toString(entity.getType()));
                    worldFileContentNew.append("\",x=\"");
                    worldFileContentNew.append(Double.toString(entity.getWorldCoordinates().x));
                    worldFileContentNew.append("\",y=\"");
                    worldFileContentNew.append(Double.toString(entity.getWorldCoordinates().y));
                    if (isWarpEntity) {
                        worldFileContentNew.append("\",warp_to=\"");
                        worldFileContentNew.append(((Warp) entity).getWarpToScene());
                        worldFileContentNew.append("\",warp_to_x=\"");
                        worldFileContentNew.append(Double.toString(((Warp) entity).getWarpToCoordinates().x));
                        worldFileContentNew.append("\",warp_to_y=\"");
                        worldFileContentNew.append(Double.toString(((Warp) entity).getWarpToCoordinates().y));
                    }
                    if (isWarpEntity) worldFileContentNew.append("\"\n");
                    else worldFileContentNew.append("\"\n");
                }

                /** NPCs **/
                for (NonPlayerCharacter npc : Scene.getInstance().getListOfNonPlayerCharacters()) {
                    worldFileContentNew.append("entity_npc:code=\"");
                    worldFileContentNew.append(npc.getEntityCode());
                    worldFileContentNew.append("\",type=\"");
                    worldFileContentNew.append(Integer.toString(npc.getType()));
                    worldFileContentNew.append("\",x=\"");
                    worldFileContentNew.append(Double.toString(npc.getWorldCoordinates().x));
                    worldFileContentNew.append("\",y=\"");
                    worldFileContentNew.append(Double.toString(npc.getWorldCoordinates().y));
                    worldFileContentNew.append("\",buys=\"");
                    worldFileContentNew.append(Boolean.toString(npc.getAvailableActions().contains(NonPlayerCharacterAction.BUY)));
                    worldFileContentNew.append("\",sells=\"");
                    worldFileContentNew.append(Boolean.toString(npc.getAvailableActions().contains(NonPlayerCharacterAction.SELL)));
                    worldFileContentNew.append("\",talk_text_string_name=\"");
                    worldFileContentNew.append(npc.getTalkTextStringName());
                    worldFileContentNew.append("\",talk_text_string_args=\"");
                    worldFileContentNew.append(npc.getTalkTextStringArgs());
                    worldFileContentNew.append("\"\n");
                }

                worldFileContentNew.append("end_scene:name=\"").append(sceneToSave).append("\"");

                FileWriter fileWriter = new FileWriter("res/world/world.txt");
                fileWriter.write(worldFileContentNew.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.l("World saved successfully");
        }
    }

    /** LOAD WORLD **/

    public void loadWorld() {
        try {
            String sceneToLoad = Scene.getInstance().getSceneName();
            String worldFileContent = IOUtils.readFile("res/world/world.txt", StandardCharsets.UTF_8);

            String startSceneString = "start_scene:name=\"" + sceneToLoad + "\"";
            int startSceneIndex = worldFileContent.indexOf(startSceneString);
            String endSceneString = "end_scene:name=\"" + sceneToLoad + "\"";
            int endSceneIndex = worldFileContent.indexOf(endSceneString) + endSceneString.length();


            String sceneContent = worldFileContent.substring(startSceneIndex, endSceneIndex);
//            Log.l(sceneContent);
            loadScene(sceneContent);

            Log.l("World loaded successfully");
        } catch (Exception e) {
            Log.e("Error loading World. Reason: " + e);
        }
    }

    private void loadScene(String sceneContent) {
        String contentLines[] = sceneContent.split("\\r?\\n");
        for (int i = 0; i < contentLines.length; i++) {
            Log.l(contentLines[i]);
            if (contentLines[i].contains("default_spawn_coordinates:")) {
                loadSpawnCoords(contentLines[i]);
            } else if (contentLines[i].contains("tiles:")) {
                loadTiles(contentLines[i], contentLines[i + 1]);
                i++;
            } else if (contentLines[i].contains("entity:")) {
                loadEntity(contentLines[i]);
            } else if (contentLines[i].contains("entity_warp:")) {
                loadEntityWarp(contentLines[i]);
            } else if (contentLines[i].contains("entity_npc:")) {
                loadNPC(contentLines[i]);
            }
        }
    }

    private void loadNPC(String content) {
        String[] contentSplitted = content.split(":")[1].split(",");
        String talkTextStringName, talkTextStringArgs;
        boolean buys, sells;
        double x, y;
        String entityCode;
        int entityType;
        entityCode = contentSplitted[0].split("\"")[1];
        entityType = Integer.parseInt(contentSplitted[1].split("\"")[1]);
        x = parseCoordinate(contentSplitted[2]);
        y = parseCoordinate(contentSplitted[3]);
        buys = Boolean.parseBoolean(contentSplitted[4].split("\"")[1]);
        sells = Boolean.parseBoolean(contentSplitted[5].split("\"")[1]);
        talkTextStringName = contentSplitted[6].split("\"")[1];
        talkTextStringArgs = contentSplitted[7].split("\"")[1];

        if (entityCode.equals(NonPlayerCharacter.ENTITY_CODE)) {
            NonPlayerCharacter npc = new NonPlayerCharacter((int) x, (int) y, sells, buys, entityType);
            npc.setTalkTextStringName(talkTextStringName, talkTextStringArgs);
        }
    }

    private void loadEntityWarp(String content) {
        String[] contentSplitted = content.split(":")[1].split(",");
        double x, y;
        String entityCode;
        int entityType;
        entityCode = contentSplitted[0].split("\"")[1];
        entityType = Integer.parseInt(contentSplitted[1].split("\"")[1]);
        x = parseCoordinate(contentSplitted[2]);
        y = parseCoordinate(contentSplitted[3]);
        String warpTo = contentSplitted[4].split("\"")[1];
        Coordinates warpToCoordinates = new Coordinates(parseCoordinate(contentSplitted[5]), parseCoordinate(contentSplitted[6]));

        if (entityCode.equals(LargeWarp.ENTITY_CODE)) {
            new LargeWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        } else if (entityCode.equals(SmallWarp.ENTITY_CODE)) {
            new SmallWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        }
    }

    private void loadEntity(String content) {
        String[] contentSplitted = content.split(":")[1].split(",");
        double x, y;
        String entityCode;
        int entityType;
        entityCode = contentSplitted[0].split("\"")[1];
        entityType = Integer.parseInt(contentSplitted[1].split("\"")[1]);
        x = parseCoordinate(contentSplitted[2]);
        y = parseCoordinate(contentSplitted[3]);

        if (entityCode.equals(Tree.ENTITY_CODE)) {
            new Tree((int) x, (int) y, entityType);
        } else if (entityCode.equals(Light.ENTITY_CODE)) {
            new Light((int) x, (int) y);
        } else if (entityCode.equals(Torch.ENTITY_CODE)) {
            new Torch((int) x, (int) y);
        } else if (entityCode.equals(Fence.ENTITY_CODE)) {
            new Fence((int) x, (int) y, entityType);
        } else if (entityCode.equals(Building.ENTITY_CODE)) {
            new Building((int) x, (int) y, entityType);
        } else if (entityCode.equals(UtilityPole.ENTITY_CODE)) {
            new UtilityPole((int) x, (int) y);
        }
    }

    private void loadTiles(String tilesInfo, String tilesContent) {
        Log.l(tilesInfo);
        Log.l(tilesContent);
        String[] tilesInfoSplitted = tilesInfo.split(":")[1].split(",");
        TileMap.setNumOfHorizontalTiles(Integer.parseInt(tilesInfoSplitted[0].split("\"")[1]));
        TileMap.setNumOfVerticalTiles(Integer.parseInt(tilesInfoSplitted[1].split("\"")[1]));
        String[] tilesString = tilesContent.split(";");
        Tile[][] tiles = new Tile[TileMap.getNumOfHorizontalTiles()][TileMap.getNumOfVerticalTiles()];
        Tile tile;
        int index = 0;
        byte tileType;
        boolean collidable;
        for (int j = 0; j < TileMap.getNumOfVerticalTiles(); j++) {
            for (int i = 0; i < TileMap.getNumOfHorizontalTiles(); i++) {
                String[] tileString = tilesString[index].split(",");
                tileType = Byte.parseByte(tileString[0]);
                collidable = tileString[1].equals("1");
                tile = new Tile();
                tile.setLayerValue(0, tileType);
                tile.setCollidable(collidable);
                tiles[i][j] = tile;
                index++;
            }
        }

        TileMap.setArrayOfTiles(tiles);
    }

    private void loadSpawnCoords(String content) {
        String[] contentSplitted = (content.split(":"))[1].split(",");
        double x = parseCoordinate(contentSplitted[0]);
        double y = parseCoordinate(contentSplitted[1]);
//        Log.l(x + ", " + y);
        Camera.getInstance().init(new Coordinates(x, y));
        Player.getInstance().init(new Coordinates(x, y));
    }

    /**
     * Example
     * @returns double if the input is "x="double""
     **/
    private double parseCoordinate(String coordinate) {
        return Double.parseDouble(coordinate.split("\"")[1]);
    }
}