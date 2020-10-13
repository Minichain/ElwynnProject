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
    private static ArrayList<GraphicEntity> listOfEntitiesToUpdate;
    private static ArrayList<Enemy> listOfEnemies;
    private static ArrayList<MusicalNoteGraphicEntity> listOfMusicalNoteGraphicEntities;
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
        listOfEntitiesToUpdate = new ArrayList<>();
        listOfEnemies = new ArrayList<>();
        listOfCircleAttacks = new ArrayList<>();
        listOfLightSources = new ArrayList<>();
        listOfVisibleLightSources = new ArrayList<>();
        listOfMusicalNoteGraphicEntities = new ArrayList<>();
        listOfNonPlayerCharacters = new ArrayList<>();

        TileMap.loadMap();

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

    public void update(long timeElapsed) {
        renderDistance = (int) (1.2 * (Parameters.getResolutionWidth() / 2.0) / Camera.getZoom());
        updateDistance = (int) (1.5 * (Parameters.getResolutionWidth() / 2.0) / Camera.getZoom());

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
            int distance = (int) ((Math.random() * 170) + 1000);
            double angle = Math.random() * 2 * Math.PI;
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
        if (!listOfEntities.isEmpty() && listOfEntitiesToUpdate != null) {
            listOfEntitiesToUpdate.clear();
            listOfEnemies.clear();
            for (int i = 0; i < listOfEntities.size(); i++) {
                GraphicEntity currentEntity = listOfEntities.get(i);
                if (GameStatus.getStatus() == GameStatus.Status.RUNNING && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                    currentEntity.update(timeElapsed);
                }
                currentEntity.updateCoordinates();

                if (MathUtils.module(Camera.getInstance().getCoordinates(), currentEntity.getCenterOfMassWorldCoordinates()) < updateDistance) {
                    listOfEntitiesToUpdate.add(currentEntity);
                }
                if (currentEntity instanceof Enemy && ((Enemy) currentEntity).getStatus() != Enemy.Status.DEAD) {
                    listOfEnemies.add((Enemy) currentEntity);
                }
            }
        }

        /** SORT ENTITIES BY DEPTH (BUBBLE ALGORITHM) **/
        //TODO Replace Bubble Algorithm by Insertion Sort Algorithm or Quick Sort Algorithm to improve performance.
        int n = listOfEntitiesToUpdate.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                GraphicEntity entity1 = listOfEntitiesToUpdate.get(j + 1);
                GraphicEntity graphicEntity2 = listOfEntitiesToUpdate.get(j);
                if (entity1.getWorldCoordinates().y < graphicEntity2.getWorldCoordinates().y) {
                    listOfEntitiesToUpdate.set(j + 1, graphicEntity2);
                    listOfEntitiesToUpdate.set(j, entity1);
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

        /** MUSICAL NOTES **/
        if (!listOfMusicalNoteGraphicEntities.isEmpty()) {
            MusicalNoteGraphicEntity musicalNoteGraphicEntity;
            for (int i = 0; i < listOfMusicalNoteGraphicEntities.size(); i++) {
                musicalNoteGraphicEntity = listOfMusicalNoteGraphicEntities.get(i);
                if (musicalNoteGraphicEntity.isDead()) {
                    for (LightSource lightSource : musicalNoteGraphicEntity.getLightSources()) {
                        listOfLightSources.remove(lightSource);
                    }
                    listOfMusicalNoteGraphicEntities.remove(musicalNoteGraphicEntity);
                } else {
                    musicalNoteGraphicEntity.update(timeElapsed);
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

    public List<GraphicEntity> getListOfEntitiesToUpdate() {
        return listOfEntitiesToUpdate;
    }

    public ArrayList<LightSource> getListOfLightSources() {
        return listOfLightSources;
    }

    public ArrayList<LightSource> getListOfVisibleLightSources() {
        return listOfVisibleLightSources;
    }

    public ArrayList<MusicalNoteGraphicEntity> getListOfMusicalNoteGraphicEntities() {
        return listOfMusicalNoteGraphicEntities;
    }

    public ArrayList<NonPlayerCharacter> getListOfNonPlayerCharacters() {
        return listOfNonPlayerCharacters;
    }

    public static Coordinates getInitialCoordinates() {
        return initialCoordinates;
    }

    public void reset() {
        Log.l("Reseting Scene");
        resetEntities();
        Player.getInstance().reset();
        Camera.getInstance().reset();
        GameTime.setGameTime(0);

        init();
        getListOfEntities().add(Player.getInstance());
        listOfCircleAttacks.clear();
    }

    private void resetEntities() {
        for (int i = 0; i < getListOfEntities().size(); i++) {
            Entity entity = getListOfEntities().get(i);
            if (entity instanceof DynamicGraphicEntity) {
                DynamicGraphicEntity dynamicGraphicEntity = (DynamicGraphicEntity) getListOfEntities().get(i);
                for (LightSource lightSource : dynamicGraphicEntity.getLightSources()) {
                    getListOfLightSources().remove(lightSource);
                }
                getListOfEntities().remove(i);
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
        renderSecondLayerOfTilesAndEntities(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);

        /** THIRD AND LAST LAYER OF TILES IS DRAWN LAST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 2);

        /** MUSICAL NOTES **/
        if (!listOfMusicalNoteGraphicEntities.isEmpty()) {
            for (MusicalNoteGraphicEntity musicalNoteGraphicEntity : listOfMusicalNoteGraphicEntities) {
                if (musicalNoteGraphicEntity != null) {
                    Coordinates cameraCoordinates = musicalNoteGraphicEntity.getWorldCoordinates().toCameraCoordinates();
                    musicalNoteGraphicEntity.drawSprite((int) cameraCoordinates.x, (int) cameraCoordinates.y);
                }
            }
        }

        OpenGLManager.releaseCurrentShader();

        /** COLLIDABLE TILES **/
        if ((GameMode.getGameMode() == GameMode.Mode.CREATIVE || Parameters.isDebugMode())) {
            renderCollidableTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);
        }

        /** ENTITIES HITBOX **/
        if (Parameters.isDebugMode()) {
            for (GraphicEntity graphicEntity : listOfEntitiesToUpdate) {
                if (graphicEntity instanceof StaticGraphicEntity) {
                    ((StaticGraphicEntity) graphicEntity).drawHitBox((int) graphicEntity.getCameraCoordinates().x, (int) graphicEntity.getCameraCoordinates().y);
                }
            }
        }
    }

    private void renderSecondLayerOfTilesAndEntities(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates) {
//        Log.l("Render second layer of Tiles and Entities.");

        GraphicEntity entity = null;
        int entityIterator = 0;
        int firstTileRowToDraw = (int) topLeftTileCoordinates.y;
        int lastTileRowToDraw = (int) bottomLeftTileCoordinates.y;
        int tileRowIterator = firstTileRowToDraw;

        while (tileRowIterator < lastTileRowToDraw) {
            if (entityIterator < listOfEntitiesToUpdate.size()) {
                entity = listOfEntitiesToUpdate.get(entityIterator);
            }
            if (entity != null && entity.getWorldCoordinates().y < Coordinates.tileCoordinatesToWorldCoordinates(0, tileRowIterator).y) {
                entity.drawSprite((int) entity.getCameraCoordinates().x, (int) entity.getCameraCoordinates().y);
                entity = null;
                entityIterator++;
            } else {
                glActiveTexture(GL_TEXTURE0);
                TileMap.bindTileSetTexture();

                glEnable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

                OpenGLManager.glBegin(GL_QUADS);

                for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
                    int k = 1;
                    if (0 < i && i < TileMap.getArrayOfTiles().length
                            && 0 < tileRowIterator && tileRowIterator < TileMap.getArrayOfTiles()[0].length
                            && TileMap.getArrayOfTiles()[i][tileRowIterator].getLayerValue(k) != 0) {
                        double scale = Camera.getZoom();
                        int x = i * TileMap.TILE_WIDTH;
                        int y = tileRowIterator * TileMap.TILE_HEIGHT;
                        TileMap.drawTile(i, tileRowIterator, k, x, y, scale);
                    }
                }

                glDisable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);

                glEnd();

                tileRowIterator++;
            }
        }
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
