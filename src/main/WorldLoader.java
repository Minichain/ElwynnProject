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
import java.io.File;

public class WorldLoader {
    private static WorldLoader instance = null;
    private static final String worldFilePath = "res/world/";

    private WorldLoader() {
        Scene.getInstance().setSceneName("scene02");
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
            SaveWorldThread thread = new SaveWorldThread();
            thread.start();
        } catch (Exception e) {
            Log.e("World could not be saved");
        }
    }

    private static class SaveWorldThread extends Thread {

        public SaveWorldThread() {

        }

        public void run() {
            StringBuilder worldString = new StringBuilder();
            worldString.append("<scene name=\"").append(Scene.getInstance().getSceneName()).append("\">\n" + "<spawn_coordinates x=\"800.0\" y=\"800.0\"></spawn_coordinates>\n" + "<tiles width=\"")
                    .append(TileMap.getNumOfHorizontalTiles()).append("\" height=\"").append(TileMap.getNumOfVerticalTiles()).append("\">");

            /** TILES **/
            Tile tile;
            for (int j = 0; j < TileMap.getNumOfVerticalTiles(); j++) {
                for (int i = 0; i < TileMap.getNumOfHorizontalTiles(); i++) {
                    tile = TileMap.getArrayOfTiles()[i][j];
                    worldString.append(tile.getLayerValue(0)).append(",").append(tile.isCollidable() ? "1" : "0").append(";");
                }
            }
            worldString.append("</tiles>\n");

            /** ENTITIES **/
            for (StaticGraphicEntity entity : Scene.getInstance().getListOfStaticEntities()) {
                boolean isWarpEntity = entity.getEntityCode().equals(LargeWarp.ENTITY_CODE) || entity.getEntityCode().equals(SmallWarp.ENTITY_CODE);
                if (isWarpEntity) worldString.append("<entity_warp code=\"");
                else worldString.append("<entity code=\"");
                worldString.append(entity.getEntityCode());
                worldString.append("\" type=\"");
                worldString.append(entity.getType());
                worldString.append("\" x=\"");
                worldString.append(entity.getWorldCoordinates().x);
                worldString.append("\" y=\"");
                worldString.append(entity.getWorldCoordinates().y);
                if (isWarpEntity) {
                    worldString.append("\" warp_to=\"");
                    worldString.append(((Warp) entity).getWarpToScene());
                    worldString.append("\" warp_to_x=\"");
                    worldString.append(((Warp) entity).getWarpToCoordinates().x);
                    worldString.append("\" warp_to_y=\"");
                    worldString.append(((Warp) entity).getWarpToCoordinates().y);
                }
                if (isWarpEntity) worldString.append("\"></entity_warp>\n");
                else worldString.append("\"></entity>\n");
            }

            /** NPCs **/
            for (NonPlayerCharacter npc : Scene.getInstance().getListOfNonPlayerCharacters()) {
                worldString.append("<entity_npc code=\"");
                worldString.append(npc.getEntityCode());
                worldString.append("\" type=\"");
                worldString.append(npc.getType());
                worldString.append("\" x=\"");
                worldString.append(npc.getWorldCoordinates().x);
                worldString.append("\" y=\"");
                worldString.append(npc.getWorldCoordinates().y);
                worldString.append("\" buys=\"");
                worldString.append(npc.getAvailableActions().contains(NonPlayerCharacterAction.BUY));
                worldString.append("\" sells=\"");
                worldString.append(npc.getAvailableActions().contains(NonPlayerCharacterAction.SELL));
                worldString.append("\" talk_text_string_name=\"");
                worldString.append(npc.getTalkTextStringName());
                worldString.append("\" talk_text_string_args=\"");
                worldString.append(npc.getTalkTextStringArgs());
                worldString.append("\"></entity_npc>\n");
            }

            worldString.append("</scene>\n");

            try {
                IOUtils.stringToXmlFile(worldString.toString(), worldFilePath + Scene.getInstance().getSceneName() + ".xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.l("World saved successfully");
        }
    }

    /** LOAD WORLD **/

    public void loadWorld() {
        try {
            File xmlFile = new File(worldFilePath + Scene.getInstance().getSceneName() + ".xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            NodeList nodeList;

            Log.l("Loading scene: " + Scene.getInstance().getSceneName());

            nodeList = document.getElementsByTagName("scene");
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (((Element) nodeList.item(i)).getAttribute("name").equals(Scene.getInstance().getSceneName())) {
                    loadScene(nodeList.item(i));
                }
            }

            Log.l("World loaded successfully");
        } catch (Exception e) {
            Log.e("Error loading World. Reason: " + e);
        }
    }

    private void loadScene(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("spawn_coordinates")) {
                loadSpawnCoords((Element) nodeList.item(i));
            } else if (nodeList.item(i).getNodeName().equals("tiles")) {
                loadTiles((Element) nodeList.item(i));
            } else if (nodeList.item(i).getNodeName().equals("entity")) {
                loadEntity((Element) nodeList.item(i));
            } else if (nodeList.item(i).getNodeName().equals("entity_warp")) {
                loadEntityWarp((Element) nodeList.item(i));
            } else if (nodeList.item(i).getNodeName().equals("entity_npc")) {
                loadNPC((Element) nodeList.item(i));
            }
        }
    }

    private void loadNPC(Element element) {
        String talkTextStringName, talkTextStringArgs;
        boolean buys, sells;
        double x, y;
        String entityCode;
        int entityType;
        entityCode = element.getAttribute("code");
        entityType = Integer.parseInt(element.getAttribute("type"));
        x = Double.parseDouble(element.getAttribute("x"));
        y = Double.parseDouble(element.getAttribute("y"));
        talkTextStringName = element.getAttribute("talk_text_string_name");
        talkTextStringArgs = element.getAttribute("talk_text_string_args");
        buys = Boolean.parseBoolean(element.getAttribute("buys"));
        sells = Boolean.parseBoolean(element.getAttribute("sells"));

        if (entityCode.equals(NonPlayerCharacter.ENTITY_CODE)) {
            NonPlayerCharacter npc = new NonPlayerCharacter((int) x, (int) y, sells, buys, entityType);
            npc.setTalkTextStringName(talkTextStringName, talkTextStringArgs);
        }
    }

    private void loadEntityWarp(Element element) {
        double x, y;
        String entityCode;
        int entityType;
        entityCode = element.getAttribute("code");
        entityType = Integer.parseInt(element.getAttribute("type"));
        x = Double.parseDouble(element.getAttribute("x"));
        y = Double.parseDouble(element.getAttribute("y"));
        String warpTo = element.getAttribute("warp_to");
        Coordinates warpToCoordinates = new Coordinates(Double.parseDouble(element.getAttribute("warp_to_x")),
                Double.parseDouble(element.getAttribute("warp_to_y")));

        if (entityCode.equals(LargeWarp.ENTITY_CODE)) {
            new LargeWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        } else if (entityCode.equals(SmallWarp.ENTITY_CODE)) {
            new SmallWarp((int) x, (int) y, entityType, warpTo, warpToCoordinates);
        }
    }

    private void loadEntity(Element element) {
        double x, y;
        String entityCode;
        int entityType;
        entityCode = element.getAttribute("code");
        entityType = Integer.parseInt(element.getAttribute("type"));
        x = Double.parseDouble(element.getAttribute("x"));
        y = Double.parseDouble(element.getAttribute("y"));

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

    private void loadTiles(Element element) {
        TileMap.setNumOfHorizontalTiles(Integer.parseInt(element.getAttribute("width")));
        TileMap.setNumOfVerticalTiles(Integer.parseInt(element.getAttribute("height")));
        String[] tilesString = element.getTextContent().split(";");
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

    private void loadSpawnCoords(Element element) {
        double x = Double.parseDouble(element.getAttribute("x"));
        double y = Double.parseDouble(element.getAttribute("y"));
//        Log.l(x + ", " + y);
        Camera.getInstance().init(new Coordinates(x, y));
        Player.getInstance().init(new Coordinates(x, y));
    }
}