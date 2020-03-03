package main;

import entities.TileMap;
import utils.Utils;

import java.util.ArrayList;

public class PathFindingAlgorithm {
    private int[] initialWorldCoordinates;
    private int[] goalWorldCoordinates;

    private int width;
    private int height;
    private int marginX = 0;   //TODO rename this variable
    private int marginY = 0;   //TODO rename this variable

    private int[] initialNode = new int[2];
    private int[] goalNode = new int[2];
    private boolean[][] costAssignedNodes;
    private boolean[][] visitedNodes;
    private boolean[][] collidableNodes;
    private int[][][] parentNode;
    private int[][] costG;
    private int[][] costH;
    private int[][] costF;

    private boolean setNodeCostsTerminated = false;

    private ArrayList<int[]> nodesToVisitNext = new ArrayList<>();

    private ArrayList<int[]> path = new ArrayList<>();

    public PathFindingAlgorithm(Coordinates initialWorldCoordinates, Coordinates goalWorldCoordinates) {
        this.initialWorldCoordinates = Coordinates.worldCoordinatesToTileCoordinates((int) initialWorldCoordinates.x, (int) initialWorldCoordinates.y);
        this.goalWorldCoordinates = Coordinates.worldCoordinatesToTileCoordinates((int) goalWorldCoordinates.x, (int) goalWorldCoordinates.y);

        //this.initialWorldCoordinates = new int[]{0, 0};
        //this.goalWorldCoordinates = new int[]{15, 5};

        int[] toTileMapCoordinates = new int[]{-1, -1};

        if (this.initialWorldCoordinates[0] < this.goalWorldCoordinates[0]) {
            this.width = this.goalWorldCoordinates[0] - this.initialWorldCoordinates[0] + 1;
            initialNode[0] = marginX;
            goalNode[0] = this.width - 1 - marginX;
            toTileMapCoordinates[0] = this.initialWorldCoordinates[0] - marginX;
        } else {
            this.width = this.initialWorldCoordinates[0] - this.goalWorldCoordinates[0] + 1;
            goalNode[0] = marginX;
            initialNode[0] = this.width - 1 - marginX;
            toTileMapCoordinates[0] = this.goalWorldCoordinates[0] - marginX;
        }
        this.width += marginX * 2;

        if (this.initialWorldCoordinates[1] < this.goalWorldCoordinates[1]) {
            this.height = this.goalWorldCoordinates[1] - this.initialWorldCoordinates[1] + 1;
            initialNode[1] = marginY;
            goalNode[1] = this.height - 1 - marginY;
            toTileMapCoordinates[1] = this.initialWorldCoordinates[1] - marginY;
        } else {
            this.height = this.initialWorldCoordinates[1] - this.goalWorldCoordinates[1] + 1;
            goalNode[1] = marginY;
            initialNode[1] = this.height - 1 - marginY;
            toTileMapCoordinates[1] = this.goalWorldCoordinates[1] - marginY;
        }
        this.height += marginY * 2;

        costAssignedNodes = new boolean[width][height];
        visitedNodes = new boolean[width][height];
        collidableNodes = new boolean[width][height];
        parentNode = new int[width][height][2];

        for (int i = 0; i < collidableNodes.length; i++) {
            for (int j = 0; j < collidableNodes[i].length; j++) {
                collidableNodes[i][j] = TileMap.getArrayOfTiles()[toTileMapCoordinates[0] + i][toTileMapCoordinates[1] + j].isCollidable();
            }
        }

        costG = new int[width][height];
        costH = new int[width][height];
        costF = new int[width][height];
    }

    public int[] computeBestPath() {
        computeNodeCosts();

        //System.out.println("costF:");
        //Utils.printArray(costF);

        findPath();

        //System.out.println("The path is:");
        //for (int i = 0; i < path.size(); i++) {
            //System.out.println("Step " + i + " : " + path.get(i)[0] + ", " + path.get(i)[1]);
        //}

        if (!path.isEmpty()) {
            return path.get(path.size() - 1);
        }
        return new int[]{0, 0};
    }

    private void computeNodeCosts() {
        costAssignedNodes[initialNode[0]][initialNode[1]] = true;
        costG[initialNode[0]][initialNode[1]] = 0;
        costH[initialNode[0]][initialNode[1]] = 0;
        costF[initialNode[0]][initialNode[1]] = 0;

        computeCostsSurroundingNode(initialNode[0], initialNode[1]);
    }

    private void computeCostsSurroundingNode(int x, int y) {
        //System.out.println("computeCostsSurroundingNode " + x + ", " + y);
        for (int i = x - 1; i <= (x + 1); i++) {
            for (int j = y - 1; j <= (y + 1); j++) {
                if (i == x && j == y) {
                    // Ignore
                } else if (!setNodeCostsTerminated && setNodeCost(i, j, new int[]{x, y}) != -1) {
                    nodesToVisitNext.add(new int[]{i, j});
                }
            }
        }

        if (!setNodeCostsTerminated && !nodesToVisitNext.isEmpty()) {
            int[] nodeWithLessCost = new int[]{-1, -1};
            int previousNodeCost = -1;
            for (int[] node : nodesToVisitNext) {
                if (previousNodeCost == -1 || costF[node[0]][node[1]] < previousNodeCost) {
                    nodeWithLessCost = node;
                    previousNodeCost = costF[node[0]][node[1]];
                }
            }
            nodesToVisitNext.remove(nodeWithLessCost);
            computeCostsSurroundingNode(nodeWithLessCost[0], nodeWithLessCost[1]);
        }
    }

    private int setNodeCost(int i, int j, int[] parentNode) {
        if (i >= 0 && i < width
                && j >= 0 && j < height
                && !costAssignedNodes[i][j]) {

            this.parentNode[i][j] = parentNode;
            costAssignedNodes[i][j] = true;

            if (i == goalNode[0] && j == goalNode[1]) {
                System.out.println("Node Costs Terminated at node " + i + ", " + j);
                setNodeCostsTerminated = true;
            }

            int G, H, F;
            if (collidableNodes[i][j]) {
                G = -1;
                H = -1;
                F = -1;
            } else {
                G = (int) (Math.sqrt(Math.pow(i - initialNode[0], 2.0) + Math.pow(j - initialNode[1], 2.0)) * 10.0);
                H = (int) (Math.sqrt(Math.pow(i - goalNode[0], 2.0) + Math.pow(j - goalNode[1], 2.0)) * 10.0);
                F = G + H;
            }

            costG[i][j] = G;
            costH[i][j] = H;
            costF[i][j] = F;

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
        path.add(new int[]{parent[0] - parentNode[0], parent[1] - parentNode[1]});
        findStep(parentNode);
    }
}
