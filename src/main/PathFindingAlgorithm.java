package main;

import entities.TileMap;

import java.util.ArrayList;

public class PathFindingAlgorithm {
    public Coordinates initialCoordinates;
    private Coordinates initialTileCoordinates;
    private Coordinates goalTileCoordinates;

    private int tilesInXAxis;
    private int tilesInYAxis;
    private int marginX = 5;
    private int marginY = 5;

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
        this.initialCoordinates = new Coordinates(initialWorldCoordinates.x, initialWorldCoordinates.y);
        this.initialTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(initialWorldCoordinates.x, initialWorldCoordinates.y);
        this.goalTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(goalWorldCoordinates.x, goalWorldCoordinates.y);

        int[] toTileMapCoordinates = new int[2];

        if (this.initialTileCoordinates.x < this.goalTileCoordinates.x) {
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

        if (this.initialTileCoordinates.y < this.goalTileCoordinates.y) {
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
                collidableNodes[i][j] = TileMap.getArrayOfTiles()[toTileMapCoordinates[0] + i][toTileMapCoordinates[1] + j].isCollidable();
            }
        }

        cost = new int[tilesInXAxis][tilesInYAxis];
    }

    public void computeBestPath() {
        computeNodeCosts();
        findPath();
    }

    public int[] getNextStep() {
        if (!path.isEmpty()) {
            int index = path.size() - 1;
            int[] step = path.get(index);
            return step;
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
        //System.out.println("computeCostsSurroundingNode " + x + ", " + y);
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
            nodesToVisitNext.remove(nodeWithLessCost);
            computeCostsSurroundingNode(nodeWithLessCost[0], nodeWithLessCost[1]);
        }
    }

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
            if (collidableNodes[i][j]) {
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
            path.add(new int[]{parent[0] - parentNode[0], parent[1] - parentNode[1]});
            findStep(parentNode);
        }
    }
}
