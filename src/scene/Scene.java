package scene;

import audio.OpenALManager;
import entities.*;
import main.*;
import particles.ParticleManager;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Scene {
    private static Scene instance = null;

    /** ENTITIES **/
    private static ArrayList<GraphicEntity> listOfEntities;
    private static ArrayList<StaticGraphicEntity> listOfStaticEntities;
    private static ArrayList<Enemy> listOfEnemies;
    private static ArrayList<NonPlayerCharacter> listOfNonPlayerCharacters;
    private static int enemySpawnPeriod; // In Milliseconds
    private static long lastEnemySpawnTime;

    private static int renderDistance;
    private static int updateDistance;

    private static Coordinates initialCoordinates;

    public static ArrayList<CircleAttack> listOfCircleAttacks;

    public static ArrayList<LightSource> listOfLightSources;
    public static ArrayList<LightSource> listOfVisibleLightSources;
    private double visibleLightSourceDistanceFactor = 30.0;

    private Scene() {

    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }

    public void init() {
        Log.l("Initiating Scene");
        initialCoordinates = new Coordinates(2481, 1747);
        listOfEntities = new ArrayList<>();
        listOfStaticEntities = new ArrayList<>();
        listOfEnemies = new ArrayList<>();
        listOfCircleAttacks = new ArrayList<>();
        listOfLightSources = new ArrayList<>();
        listOfVisibleLightSources = new ArrayList<>();
        listOfNonPlayerCharacters = new ArrayList<>();

        TileMap.loadMap();
        Player.getInstance().init();
        Camera.getInstance().init();
        Scene.getInstance().getListOfEntities().add(Player.getInstance());
        GameTime.setGameTime(0);

        //TODO: NPC testing
        GenericNPC01 npc;
        ArrayList<String> textList;
        npc = new GenericNPC01(2455, 1725);
        npc.setTalkText("Press SPACE when moving to roll.");
        npc = new GenericNPC01(2485, 1720);
        npc.setTalkText("Press LEFT or RIGHT mouse buttons to attack.");
        npc = new GenericNPC01(2500, 1725);
        npc.setTalkText("Use Q and E to change the musical mode.");
        npc = new GenericNPC01(2460, 1810);
        textList = new ArrayList<>();
        textList.add("There is a village south from here.");
        textList.add("Just follow the path...");
        textList.add("...you won't get lost.");
        npc.setTalkText(textList);
        npc = new GenericNPC01(2456, 2442);
        npc.setTalkText("Welcome, outsider. This village doesn't even have a name.");
        npc = new GenericNPC01(2622, 2471);
        npc.setTalkText("It won't stop raining...");
        npc = new GenericNPC01(2427, 2583);
        textList = new ArrayList<>();
        textList.add("Uh...?");
        textList.add("An outsider!");
        textList.add("Uh...");
        textList.add("Do you come from afar?");
        npc.setTalkText(textList);
    }

    public void reset() {
        Log.l("Resetting Scene");
        resetEntities();
        listOfCircleAttacks.clear();
        init();
    }

    public void update(long timeElapsed) {
        renderDistance = (int) (Parameters.getRenderDistance() * (Parameters.getResolutionWidth() / 2.0) / Camera.getZoom());
        updateDistance = (int) Parameters.getUpdateDistance();

        OpenALManager.playMusicDependingOnMusicalMode(Player.getInstance().getMusicalMode());

        updateAndSortEntities(timeElapsed);

        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }

        if (Parameters.isSpawnEnemies()) {
            updateEnemiesSpawn();
        }

        for (int i = 0; i < listOfCircleAttacks.size(); i++) {
            listOfCircleAttacks.get(i).update(timeElapsed, true);
            if (listOfCircleAttacks.get(i).isDead()) {
                listOfCircleAttacks.remove(listOfCircleAttacks.get(i));
            }
        }

        ParticleManager.getInstance().updateParticles(timeElapsed);
    }

    private void updateEnemiesSpawn() {
        long currentTime = System.currentTimeMillis();
        enemySpawnPeriod = (int) ((5000 / Parameters.getSpawnRate()) * Math.pow(listOfEnemies.size(), 2.0));
        if ((currentTime - lastEnemySpawnTime) > enemySpawnPeriod) {
            int distance = (int) (MathUtils.random(1000, 1170));
            double angle = MathUtils.random(0, 2 * Math.PI);
            int x = (int) ((Math.cos(angle) * distance) + Player.getInstance().getWorldCoordinates().x);
            int y = (int) ((Math.sin(angle) * distance) + Player.getInstance().getWorldCoordinates().y);
            Coordinates tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
            int i = (int) tileCoordinates.x;
            int j = (int) tileCoordinates.y;
            if (0 < i && i < TileMap.getArrayOfTiles().length
                    && 0 < j && j < TileMap.getArrayOfTiles()[0].length
                    && !TileMap.getArrayOfTiles()[i][j].isCollidable()) {
                Log.l("Spawn enemy at x: " + x + ", y: " + y);
                new Enemy(x, y);
                lastEnemySpawnTime = currentTime;
            }
        }
    }

    private void updateAndSortEntities(long timeElapsed) {
        /** UPDATE ENTITIES **/
        if (!listOfEntities.isEmpty()) {
            listOfEnemies.clear();
            for (int i = 0; i < listOfEntities.size(); i++) {
                GraphicEntity currentEntity = listOfEntities.get(i);

                double entityDistance = MathUtils.module(Camera.getInstance().getCoordinates(), currentEntity.getCenterOfMassWorldCoordinates());

                //update "update" boolean
                if (entityDistance < updateDistance) {
                    if (GameStatus.getStatus() == GameStatus.Status.RUNNING && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                        currentEntity.update(timeElapsed);
                    }
                }
                currentEntity.updateCoordinates();

                //update "render" boolean
                if (entityDistance < renderDistance) {
                    currentEntity.render = true;
                } else {
                    currentEntity.render = false;
                }

                if (currentEntity instanceof Enemy && ((Enemy) currentEntity).getStatus() != Enemy.Status.DEAD) {
                    listOfEnemies.add((Enemy) currentEntity);
                }
            }
        }

        /** SORT ENTITIES BY DEPTH (BUBBLE ALGORITHM) **/
        //TODO Replace Bubble Algorithm by Insertion Sort Algorithm or Quick Sort Algorithm to improve performance.
        int n = listOfEntities.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                GraphicEntity entity1 = listOfEntities.get(j + 1);
                GraphicEntity graphicEntity2 = listOfEntities.get(j);
                if (entity1.getWorldCoordinates().y < graphicEntity2.getWorldCoordinates().y) {
                    listOfEntities.set(j + 1, graphicEntity2);
                    listOfEntities.set(j, entity1);
                }
            }
        }

        /** LIGHT SOURCES **/
        listOfVisibleLightSources.clear();
        if (!listOfLightSources.isEmpty()) {
            for (LightSource lightSource : listOfLightSources) {
                lightSource.update(timeElapsed);
                if (MathUtils.module(lightSource.getWorldCoordinates(), Camera.getInstance().getCoordinates()) / lightSource.getIntensity() < visibleLightSourceDistanceFactor) {
                    listOfVisibleLightSources.add(lightSource);
                }
            }
        }
    }

    public List<GraphicEntity> getListOfEntities() {
        return listOfEntities;
    }

    public List<StaticGraphicEntity> getListOfStaticEntities() {
        return listOfStaticEntities;
    }

    public ArrayList<LightSource> getListOfLightSources() {
        return listOfLightSources;
    }

    public ArrayList<LightSource> getListOfVisibleLightSources() {
        return listOfVisibleLightSources;
    }

    public ArrayList<NonPlayerCharacter> getListOfNonPlayerCharacters() {
        return listOfNonPlayerCharacters;
    }

    public static Coordinates getInitialCoordinates() {
        return initialCoordinates;
    }

    private void resetEntities() {
        for (int i = 0; i < listOfEntities.size(); i++) {
            Entity entity = listOfEntities.get(i);
            if (entity instanceof DynamicGraphicEntity) {
                DynamicGraphicEntity dynamicGraphicEntity = (DynamicGraphicEntity) listOfEntities.get(i);
                for (LightSource lightSource : dynamicGraphicEntity.getLightSources()) {
                    getListOfLightSources().remove(lightSource);
                }
                listOfEntities.remove(i);
                i--;
            }
        }
    }

    public void render() {
        /** COMPUTE WHICH ARE THE TILES WE ARE GOING TO RENDER **/
        int oneAxisDistance = (int) (renderDistance * Math.sin(Math.PI / 2));

        Coordinates topLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        Coordinates topLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topLeftWorldCoordinates.x, topLeftWorldCoordinates.y);

        Coordinates topRightWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x + oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        Coordinates topRightTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topRightWorldCoordinates.x, topRightWorldCoordinates.y);

        Coordinates bottomLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y + oneAxisDistance);
        Coordinates bottomLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(bottomLeftWorldCoordinates.x, bottomLeftWorldCoordinates.y);

        OpenGLManager.useShader(1);

        /** FIRST LAYER OF TILES IS DRAWN FIRST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 0);

        /** SECOND LAYER IS DRAWN AT THE SAME TIME AS ENTITIES, BY ORDER OF DEPTH **/
//        renderSecondLayerOfTilesAndEntities(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);

        /** THIRD AND LAST LAYER OF TILES IS DRAWN LAST **/
//        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 2);

        renderEntities();

        OpenGLManager.releaseCurrentShader();

        /** COLLIDABLE TILES **/
        if ((GameMode.getGameMode() == GameMode.Mode.CREATIVE || Parameters.isDebugMode())) {
            renderCollidableTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);
        }

        /** ENTITIES HITBOX **/
        if (Parameters.isDebugMode()) {
            for (GraphicEntity graphicEntity : listOfEntities) {
                if (graphicEntity instanceof StaticGraphicEntity && graphicEntity.render) {
                    ((StaticGraphicEntity) graphicEntity).drawHitBox((int) graphicEntity.getCameraCoordinates().x, (int) graphicEntity.getCameraCoordinates().y);
                }
            }
        }
    }

    private void renderEntities() {
        for (GraphicEntity entity: listOfEntities) {
            if (entity.render) {
                entity.drawSprite((int) entity.getCameraCoordinates().x, (int) entity.getCameraCoordinates().y);
            }
        }
    }

    private void renderSecondLayerOfTilesAndEntities(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates) {
//        GraphicEntity entity = null;
//        int entityIterator = 0;
//        int firstTileRowToDraw = (int) topLeftTileCoordinates.y;
//        int lastTileRowToDraw = (int) bottomLeftTileCoordinates.y;
//        int tileRowIterator = firstTileRowToDraw;
//
//        while (tileRowIterator < lastTileRowToDraw) {
//            if (entityIterator < listOfEntitiesToUpdate.size()) {
//                entity = listOfEntitiesToUpdate.get(entityIterator);
//            }
//            if (entity != null
//                    && entity.getWorldCoordinates().y > Coordinates.tileCoordinatesToWorldCoordinates(0, tileRowIterator).y) {
//                entity.drawSprite((int) entity.getCameraCoordinates().x, (int) entity.getCameraCoordinates().y);
//                entity = null;
//                entityIterator++;
//            } else {
//                glActiveTexture(GL_TEXTURE0);
//                TileMap.bindTileSetTexture();
//
//                glEnable(GL_BLEND);
//                glEnable(GL_TEXTURE_2D);
//                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//                OpenGLManager.glBegin(GL_QUADS);
//
//                for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
//                    int k = 1;
//                    if (0 < i && i < TileMap.getArrayOfTiles().length
//                            && 0 < tileRowIterator && tileRowIterator < TileMap.getArrayOfTiles()[0].length
//                            && TileMap.getArrayOfTiles()[i][tileRowIterator].getLayerValue(k) != 0) {
//                        double scale = Camera.getZoom();
//                        int x = i * TileMap.TILE_WIDTH;
//                        int y = tileRowIterator * TileMap.TILE_HEIGHT;
//                        TileMap.drawTile(i, tileRowIterator, k, x, y, scale);
//                    }
//                }
//
//                glDisable(GL_BLEND);
//                glDisable(GL_TEXTURE_2D);
//
//                glEnd();
//
//                tileRowIterator++;
//            }
//        }
    }

    private void renderLayerOfTiles(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates, int layerToRender) {
//        Log.l("Render layer " + layerToRender + " of Tiles.");

        glActiveTexture(GL_TEXTURE0);
        TileMap.bindTileSetTexture();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OpenGLManager.glBegin(GL_QUADS);

        for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
            for (int j = (int) topLeftTileCoordinates.y; j < bottomLeftTileCoordinates.y; j++) {
                if (0 < i && i < TileMap.getArrayOfTiles().length
                        && 0 < j && j < TileMap.getArrayOfTiles()[0].length
                        && TileMap.getArrayOfTiles()[i][j].getLayerValue(layerToRender) != 0) {
                    double scale = Camera.getZoom();
                    int x = i * TileMap.TILE_WIDTH;
                    int y = j * TileMap.TILE_HEIGHT;
                    TileMap.drawTile(i, j, layerToRender, x, y, scale);
                }
            }
        }

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glEnd();
    }

    private void renderCollidableTiles(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates) {
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
            for (int j = (int) topLeftTileCoordinates.y; j < bottomLeftTileCoordinates.y; j++) {
                if (0 < i && i < TileMap.getArrayOfTiles().length
                        && 0 < j && j < TileMap.getArrayOfTiles()[0].length) {
                    if (TileMap.getArrayOfTiles()[i][j].isCollidable()) {
                        double scale = Camera.getZoom();
                        int x = i * TileMap.TILE_WIDTH;
                        int y = j * TileMap.TILE_HEIGHT;
                        Coordinates cameraCoordinates = new Coordinates(x, y);
                        cameraCoordinates = cameraCoordinates.toCameraCoordinates();
                        int width = (int) (TileMap.TILE_WIDTH * scale);
                        int height = (int) (TileMap.TILE_HEIGHT * scale);
                        OpenGLManager.drawRectangle((int) cameraCoordinates.x, (int) cameraCoordinates.y, width, height, 0.5, 1f, 0f, 0f);
                    }
                }
            }
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public boolean checkCollisionWithEntities(Coordinates coordinatesToCheck) {
        for (StaticGraphicEntity staticGraphicEntity : getListOfStaticEntities()) {
            if (staticGraphicEntity.getCollision().isColliding(coordinatesToCheck)) {
                return true;
            }
        }
        return false;
    }
}
