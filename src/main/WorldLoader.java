package main;

import entities.*;
import enums.NonPlayerCharacterAction;
import scene.Camera;
import scene.Scene;
import scene.Tile;
import scene.TileMap;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static utils.IOUtilsKt.readFile;

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
            long start = System.currentTimeMillis();
            try {
                String worldFileContentOld = readFile("res/world/world.txt", StandardCharsets.UTF_8);
//                Log.l(worldFileContentOld);

                String startSceneString = "start_scene:name=\"" + sceneToSave + "\"";
                int startSceneIndex = worldFileContentOld.indexOf(startSceneString);
                String endSceneString = "end_scene:name=\"" + sceneToSave + "\"";
                int endSceneIndex = worldFileContentOld.indexOf(endSceneString) + endSceneString.length();

                StringBuilder worldFileContentNew = new StringBuilder();
                if (startSceneIndex > 1) worldFileContentNew.append(worldFileContentOld, 0, startSceneIndex - 1);
                worldFileContentNew.append(worldFileContentOld, endSceneIndex, worldFileContentOld.length() - 1);

                worldFileContentNew.append("\nstart_scene:name=\"").append(sceneToSave).append("\"").append("\n");
                worldFileContentNew.append("default_spawn_coordinates:").append("x=\"800.0\",y=\"800.0\"").append("\n");
                worldFileContentNew.append("scene_attributes:").append("is_indoors=\"").append(Scene.getInstance().isIndoors())
                        .append("\",is_safe_area=\"").append(Scene.getInstance().isSafeArea()).append("\"").append("\n");
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

                worldFileContentNew.append("end_scene:name=\"").append(sceneToSave).append("\"\n");

                FileWriter fileWriter = new FileWriter("res/world/world.txt");
                fileWriter.write(worldFileContentNew.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.l("World saved successfully in " + (System.currentTimeMillis() - start) + " ms.");
        }
    }

    /** LOAD WORLD **/

    public void loadWorld() {
        long start = System.currentTimeMillis();
        try {
            String sceneToLoad = Scene.getInstance().getSceneName();
            String worldFileContent = readFile("res/world/world.txt", StandardCharsets.UTF_8);

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
        Log.l("Scene loaded in " + (System.currentTimeMillis() - start) + " ms.");
    }

    private void loadScene(String sceneContent) {
        String contentLines[] = sceneContent.split("\\r?\\n");
        for (int i = 0; i < contentLines.length; i++) {
//            Log.l(contentLines[i]);
            if (contentLines[i].contains("default_spawn_coordinates:")) {
                loadSpawnCoords(contentLines[i]);
            } else if (contentLines[i].contains("scene_attributes:")) {
                Scene.getInstance().setIndoors(Boolean.parseBoolean(parseAttribute(contentLines[i], "is_indoors")));
                Scene.getInstance().setSafeArea(Boolean.parseBoolean(parseAttribute(contentLines[i], "is_safe_area")));
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
        String talkTextStringName, talkTextStringArgs;
        boolean buys, sells;
        double x, y;
        String entityCode;
        int entityType;
        entityCode = parseAttribute(content, "code");
        entityType = Integer.parseInt(parseAttribute(content, "type"));
        x = Double.parseDouble(parseAttribute(content, "x"));
        y = Double.parseDouble(parseAttribute(content, "y"));
        buys = Boolean.parseBoolean(parseAttribute(content, "buys"));
        sells = Boolean.parseBoolean(parseAttribute(content, "sells"));
        talkTextStringName = parseAttribute(content, "talk_text_string_name");
        talkTextStringArgs = parseAttribute(content, "talk_text_string_args");

        if (entityCode.equals(NonPlayerCharacter.ENTITY_CODE)) {
            NonPlayerCharacter npc = new NonPlayerCharacter((int) x, (int) y, sells, buys, entityType);
            npc.setTalkTextStringName(talkTextStringName, talkTextStringArgs);
        }
    }

    private void loadEntityWarp(String content) {
        double x, y;
        String entityCode;
        int entityType;
        entityCode = parseAttribute(content, "code");
        entityType = Integer.parseInt(parseAttribute(content, "type"));
        x = Double.parseDouble(parseAttribute(content, "x"));
        y = Double.parseDouble(parseAttribute(content, "y"));
        String warpTo = parseAttribute(content, "warp_to");
        Coordinates warpToCoordinates = new Coordinates(Double.parseDouble(parseAttribute(content, "warp_to_x")),
                Double.parseDouble(parseAttribute(content, "warp_to_y")));

        if (entityCode.equals(LargeWarp.ENTITY_CODE)) {
            new LargeWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        } else if (entityCode.equals(SmallWarp.ENTITY_CODE)) {
            new SmallWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        }
    }

    private void loadEntity(String content) {
        double x, y;
        String entityCode;
        int entityType;
        entityCode = parseAttribute(content, "code");
        entityType = Integer.parseInt(parseAttribute(content, "type"));
        x = Double.parseDouble(parseAttribute(content, "x"));
        y = Double.parseDouble(parseAttribute(content, "y"));

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
        double x = Double.parseDouble(parseAttribute(content, "x"));
        double y = Double.parseDouble(parseAttribute(content, "y"));
//        Log.l(x + ", " + y);
        Camera.getInstance().setCoordinates(new Coordinates(x, y));
        Player.getInstance().setWorldCoordinates(new Coordinates(x, y));
    }

    /**
     * Example
     * @param content code="torch",type="0",x="24.0",y="40.0" or entity:code="torch",type="0",x="24.0",y="40.0"
     * @param attributeToParse code
     * @return torch
     **/
    private String parseAttribute(String content, String attributeToParse) {
        if (content.contains(":")) content = content.split(":")[1];
        content = content.split(attributeToParse + "=\"")[1];
        return content.substring(0, content.indexOf("\""));
    }
}