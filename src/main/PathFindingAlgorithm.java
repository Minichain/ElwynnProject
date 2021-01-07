package main;

import scene.Scene;
import scene.TileMap;

import java.util.ArrayList;

public class PathFindingAlgorithm {
    private Coordinates initialTileCoordinates;
    private Coordinates goalTileCoordinates;
    private final int maxNumberOfIterations = 1000;
    private int iteration;

    private int tilesInXAxis;
    private int tilesInYAxis;
    private int marginX = 10;
    private int marginY = 10;

    private int[] initialNode = new int[2];
    private int[] goalNode = new int[2];
    private boolean[][] visitedNodes;
    private boolean[][] collidableNodes;
    private int[][][] parentNode;
    private int[][] cost;

    private boolean setNodeCostsTerminated = false;

    private ArrayList<int[]> nodesToVisitNext = new ArrayList<>();
    private ArrayList<int[]> path = new ArrayList<>();

    public PathFindingAlgorithm(Coordinates initialWorldCoordinates, Coordinates goalWorldCoordinates) {
        this.initialTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(initialWorldCoordinates.x, initialWorldCoordinates.y);
        this.goalTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(goalWorldCoordinates.x, goalWorldCoordinates.y);
        this.iteration = 0;

        int[] toTileMapCoordinates = new int[2];

        if ((int) this.initialTileCoordinates.x < (int) this.goalTileCoordinates.x) {
            tilesInXAxis = (int) this.goalTileCoordinates.x - (int) this.initialTileCoordinates.x + 1 + (marginX * 2);
            initialNode[0] = marginX;
            goalNode[0] = tilesInXAxis - 1 - marginX;
            toTileMapCoordinates[0] = (int) this.initialTileCoordinates.x - marginX;
        } else {
            tilesInXAxis = (int) this.initialTileCoordinates.x - (int) this.goalTileCoordinates.x + 1 + (marginX * 2);
            goalNode[0] = marginX;
            initialNode[0] = tilesInXAxis - 1 - marginX;
            toTileMapCoordinates[0] = (int) this.goalTileCoordinates.x - marginX;
        }

        if ((int) this.initialTileCoordinates.y < (int) this.goalTileCoordinates.y) {
            tilesInYAxis = (int) this.goalTileCoordinates.y - (int) this.initialTileCoordinates.y + 1 + (marginY * 2);
            initialNode[1] = marginY;
            goalNode[1] = tilesInYAxis - 1 - marginY;
            toTileMapCoordinates[1] = (int) this.initialTileCoordinates.y - marginY;
        } else {
            tilesInYAxis = (int) this.initialTileCoordinates.y - (int) this.goalTileCoordinates.y + 1 + (marginY * 2);
            goalNode[1] = marginY;
            initialNode[1] = tilesInYAxis - 1 - marginY;
            toTileMapCoordinates[1] = (int) this.goalTileCoordinates.y - marginY;
        }

        visitedNodes = new boolean[tilesInXAxis][tilesInYAxis];
        collidableNodes = new boolean[tilesInXAxis][tilesInYAxis];
        parentNode = new int[tilesInXAxis][tilesInYAxis][2];

        for (int i = 0; i < collidableNodes.length; i++) {
            for (int j = 0; j < collidableNodes[i].length; j++) {
                if (0 < (toTileMapCoordinates[0] + i) && (toTileMapCoordinates[0] + i) < collidableNodes.length
                        && 0 < (toTileMapCoordinates[1] + j) && (toTileMapCoordinates[1] + j) < collidableNodes[0].length) {
                    collidableNodes[i][j] = TileMap.getArrayOfTiles()[toTileMapCoordinates[0] + i][toTileMapCoordinates[1] + j].isCollidable();
                }
            }
        }

        cost = new int[tilesInXAxis][tilesInYAxis];
    }

    public void computeBestPath() {
        computeNodeCosts();
        findPath();
    }

    /**
     * It returns the next tile (step) where we have to move from the computed path.
     * If we are already in the tile, it removes it from the path and it returns the next "step".
     * */
    public int[] getNextStep(Coordinates currentWorldCoordinates) {
        if (!path.isEmpty()) {
            int index = path.size() - 1;
            Coordinates worldCoordinates = Coordinates.tileCoordinatesToWorldCoordinates(path.get(index)[0], path.get(index)[1]);
            worldCoordinates.x += TileMap.TILE_WIDTH / 2;
            worldCoordinates.y += TileMap.TILE_HEIGHT / 2;
            if (Math.abs(currentWorldCoordinates.x - worldCoordinates.x) <= 1
                    && Math.abs(currentWorldCoordinates.y - worldCoordinates.y) <= 1) {   //We are already on that tile.
                path.remove(index);
                index--;
            }
            if (index >= 0) {
                return path.get(index);
            }
        }
        return new int[]{0, 0};
    }

    public ArrayList<int[]> getPath() {
        return path;
    }

    private void computeNodeCosts() {
        visitedNodes[initialNode[0]][initialNode[1]] = true;
        cost[initialNode[0]][initialNode[1]] = 0;

        computeCostsSurroundingNode(initialNode[0], initialNode[1]);
    }

    private void computeCostsSurroundingNode(int x, int y) {
        //Log.l("computeCostsSurroundingNode " + x + ", " + y);
        if (iteration >= maxNumberOfIterations) {
//            Log.l("Path finding algorithm. Max number of Iterations reached! iteration: " + iteration);
            return;
        }
        iteration++;

        for (int i = x - 1; i <= (x + 1); i++) {
            for (int j = y - 1; j <= (y + 1); j++) {
                if (i == x && j == y) {
                    // Ignore
                } else if (!setNodeCostsTerminated && computeNodeCost(i, j, new int[]{x, y}) != -1) {
                    nodesToVisitNext.add(new int[]{i, j});
                }
            }
        }

        if (!setNodeCostsTerminated && !nodesToVisitNext.isEmpty()) {
            int[] nodeWithLessCost = new int[]{-1, -1};
            int previousNodeCost = -1;
            for (int[] node : nodesToVisitNext) {
                if (previousNodeCost == -1 || cost[node[0]][node[1]] < previousNodeCost) {
                    nodeWithLessCost = node;
                    previousNodeCost = cost[node[0]][node[1]];
                }
            }
            if (nodeWithLessCost[0] != x || nodeWithLessCost[1] != y) {
                nodesToVisitNext.remove(nodeWithLessCost);
                computeCostsSurroundingNode(nodeWithLessCost[0], nodeWithLessCost[1]);  //FIXME <-- java.lang.StackOverflowError
            }
        }
    }

    //FIXME sometimes it gets stuck in this method
    private int computeNodeCost(int i, int j, int[] parentNode) {
        if (i >= 0 && i < tilesInXAxis
                && j >= 0 && j < tilesInYAxis
                && !visitedNodes[i][j]) {

            this.parentNode[i][j] = parentNode;
            visitedNodes[i][j] = true;

            if (i == goalNode[0] && j == goalNode[1]) {
                setNodeCostsTerminated = true;
            }

            int G, H, F;
            if (collidableNodes[i][j]
                    || Scene.getInstance().checkCollisionWithEntities(Coordinates.tileCoordinatesToWorldCoordinates(xNodeToTileCoordinate(i), yNodeToTileCoordinate(j)))) {
                F = -1;
            } else {
                G = (int) (Math.sqrt(Math.pow(i - initialNode[0], 2.0) + Math.pow(j - initialNode[1], 2.0)) * 10.0);
                H = (int) (Math.sqrt(Math.pow(i - goalNode[0], 2.0) + Math.pow(j - goalNode[1], 2.0)) * 10.0);
                F = G + H;
            }

            cost[i][j] = F;

            return F;
        }
        return -1;
    }

    private void findPath() {
        findStep(parentNode[goalNode[0]][goalNode[1]]);
    }

    private void findStep(int[] parent) {
        if (parent[0] == initialNode[0] && parent[1] == initialNode[1]) {
            return;
        }
        int[] parentNode = this.parentNode[parent[0]][parent[1]];
        if (parent[0] != parentNode[0] || parent[1] != parentNode[1]) {
            int[] newPath = new int[2];
            newPath[0] = xNodeToTileCoordinate(parent[0]);
            newPath[1] = yNodeToTileCoordinate(parent[1]);
            path.add(newPath);
            findStep(parentNode);
        }
    }

    private int[] nodeToTileCoordinates(int i, int j) {
        return new int[]{xNodeToTileCoordinate(i), yNodeToTileCoordinate(j)};
    }

    private int xNodeToTileCoordinate(int i) {
        return i - initialNode[0] + (int) initialTileCoordinates.x;
    }

    private int yNodeToTileCoordinate(int j) {
        return j - initialNode[1] + (int) initialTileCoordinates.y;
    }
}
