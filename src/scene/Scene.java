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

    private String sceneName = null;
    private boolean indoors;
    private boolean safeArea;

    /** ENTITIES **/
    private static ArrayList<GraphicEntity> listOfGraphicEntities;
    private static ArrayList<StaticGraphicEntity> listOfStaticGraphicEntities;
    private static ArrayList<Enemy> listOfEnemies;
    private static ArrayList<NonPlayerCharacter> listOfNonPlayerCharacters;
    private static int enemySpawnPeriod; // In Milliseconds
    private static long lastEnemySpawnTime;

    private static int renderDistance;
    private static int updateDistance;

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
        listOfGraphicEntities = new ArrayList<>();
        listOfStaticGraphicEntities = new ArrayList<>();
        listOfEnemies = new ArrayList<>();
        listOfCircleAttacks = new ArrayList<>();
        listOfLightSources = new ArrayList<>();
        listOfVisibleLightSources = new ArrayList<>();
        listOfNonPlayerCharacters = new ArrayList<>();

        WorldLoader.getInstance().loadWorld();
        Scene.getInstance().getListOfGraphicEntities().add(Player.getInstance());
        GameTime.setGameTime(0);
    }

    public void reset() {
        Log.l("Resetting Scene");
        resetEntities();
        listOfCircleAttacks.clear();
        init();
    }

    public void update(long timeElapsed) {
        renderDistance = (int) Parameters.getRenderDistance();
        updateDistance = (int) Parameters.getUpdateDistance();

        OpenALManager.playMusicDependingOnMusicalMode(Player.getInstance().getMusicalMode());

        updateAndSortEntities(timeElapsed);
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) return;
        if (Parameters.isSpawnEnemies() && !isSafeArea()) updateEnemiesSpawn();

        for (int i = 0; i < listOfCircleAttacks.size(); i++) {
            listOfCircleAttacks.get(i).update(timeElapsed, true);
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
            int[] tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
            int i = tileCoordinates[0], j = tileCoordinates[1];
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
        if (!listOfGraphicEntities.isEmpty()) {
            listOfEnemies.clear();
            removeDeadEntities();
            for (int i = 0; i < listOfGraphicEntities.size(); i++) {
                GraphicEntity graphicEntity = listOfGraphicEntities.get(i);
                graphicEntity.updateCoordinates();
                double entityDistance = MathUtils.module(Camera.getInstance().getCoordinates(), graphicEntity.getCenterOfMassWorldCoordinates());
                if (entityDistance < updateDistance) {
                    if (GameStatus.getStatus() == GameStatus.Status.RUNNING && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                        graphicEntity.update(timeElapsed);
                    }
                }

                //Set "render" flag
                graphicEntity.setRender(entityDistance < renderDistance);

                if (graphicEntity instanceof Enemy && ((Enemy) graphicEntity).getStatus() != Enemy.Status.DEAD) {
                    listOfEnemies.add((Enemy) graphicEntity);
                }
            }
        }

        /** SORT ENTITIES BY DEPTH (BUBBLE ALGORITHM) **/
        //TODO Replace Bubble Algorithm by Insertion Sort Algorithm or Quick Sort Algorithm to improve performance.
        int n = listOfGraphicEntities.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                GraphicEntity entity1 = listOfGraphicEntities.get(j + 1);
                GraphicEntity entity2 = listOfGraphicEntities.get(j);
                if (entity1.getWorldCoordinates().y < entity2.getWorldCoordinates().y) {
                    listOfGraphicEntities.set(j + 1, entity2);
                    listOfGraphicEntities.set(j, entity1);
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

    private void removeDeadEntities() {
        ArrayList<GraphicEntity> tempList = new ArrayList<>();
        for (GraphicEntity graphicEntity : listOfGraphicEntities) {
            if (!graphicEntity.isDead()) {
                tempList.add(graphicEntity);
            } else {
                for (LightSource lightSource : graphicEntity.getLightSources()) {
                    Scene.getInstance().getListOfLightSources().remove(lightSource);
                }
            }
        }
        listOfGraphicEntities = tempList;
    }

    public List<GraphicEntity> getListOfGraphicEntities() {
        return listOfGraphicEntities;
    }

    public List<StaticGraphicEntity> getListOfStaticEntities() {
        return listOfStaticGraphicEntities;
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

    public ArrayList<CircleAttack> getListOfCircleAttacks() {
        return listOfCircleAttacks;
    }

    private void resetEntities() {
        for (int i = 0; i < listOfGraphicEntities.size(); i++) {
            Entity entity = listOfGraphicEntities.get(i);
            if (entity instanceof DynamicGraphicEntity) {
                DynamicGraphicEntity dynamicGraphicEntity = (DynamicGraphicEntity) listOfGraphicEntities.get(i);
                for (LightSource lightSource : dynamicGraphicEntity.getLightSources()) {
                    getListOfLightSources().remove(lightSource);
                }
                listOfGraphicEntities.remove(i);
                i--;
            }
        }
    }

    public void render() {
        /** COMPUTE WHICH ARE THE TILES WE ARE GOING TO RENDER **/
        int oneAxisDistance = (int) (renderDistance * Math.sin(Math.PI / 2));

        Coordinates topLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        int[] topLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topLeftWorldCoordinates.x, topLeftWorldCoordinates.y);

        Coordinates topRightWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x + oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        int[] topRightTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topRightWorldCoordinates.x, topRightWorldCoordinates.y);

        Coordinates bottomLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y + oneAxisDistance);
        int[] bottomLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(bottomLeftWorldCoordinates.x, bottomLeftWorldCoordinates.y);

        OpenGLManager.useShader(1);

        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 0);

        renderEntities();

        OpenGLManager.releaseCurrentShader();

        /** COLLIDABLE TILES **/
        if ((GameMode.getGameMode() == GameMode.Mode.CREATIVE || Parameters.isDebugMode())) {
            renderCollidableTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);
        }

        /** ENTITIES HITBOX **/
        if (Parameters.isDebugMode()) {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            OpenGLManager.glBegin(GL_LINES);
            for (GraphicEntity graphicEntity : listOfGraphicEntities) {
                graphicEntity.drawHitBox();
            }
            glEnd();
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
        }
    }

    private void renderEntities() {
        for (GraphicEntity entity: listOfGraphicEntities) {
            if (entity.isRender()) {
                entity.drawSprite((int) entity.getCameraCoordinates().x, (int) entity.getCameraCoordinates().y);
            }
        }
    }

    private void renderLayerOfTiles(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates, int layerToRender) {
//        Log.l("Render layer " + layerToRender + " of Tiles.");

        glActiveTexture(GL_TEXTURE0);
        TileMap.bindTileSetTexture();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OpenGLManager.glBegin(GL_QUADS);

        for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
            for (int j = topLeftTileCoordinates[1]; j < bottomLeftTileCoordinates[1]; j++) {
                if (0 <= i && i < TileMap.getArrayOfTiles().length
                        && 0 <= j && j < TileMap.getArrayOfTiles()[0].length
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

    private void renderCollidableTiles(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates) {
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
            for (int j = topLeftTileCoordinates[1]; j < bottomLeftTileCoordinates[1]; j++) {
                if (0 <= i && i < TileMap.getArrayOfTiles().length
                        && 0 <= j && j < TileMap.getArrayOfTiles()[0].length) {
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
            if (staticGraphicEntity.getCollision() != null && staticGraphicEntity.getCollision().isColliding(coordinatesToCheck)) {
                return true;
            }
        }
        return false;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public boolean isIndoors() {
        return indoors;
    }

    public void setIndoors(boolean indoors) {
        this.indoors = indoors;
    }

    public boolean isSafeArea() {
        return safeArea;
    }

    public void setSafeArea(boolean safeArea) {
        this.safeArea = safeArea;
    }
}
