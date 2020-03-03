package main;

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
    private int[][] costG;
    private int[][] costH;
    private int[][] costF;

    private boolean setNodeCostsTerminated = false;

    private ArrayList<int[]> goalToInitialNodePath;
    private ArrayList<int[]> initialToGoalNodePath;

    private boolean pathComputationTerminated = false;

    private ArrayList<int[]> nodesToVisitNext = new ArrayList<>();

    public PathFindingAlgorithm(Coordinates initialWorldCoordinates, Coordinates goalWorldCoordinates) {
        //this.initialWorldCoordinates = Coordinates.worldCoordinatesToTileCoordinates((int) initialWorldCoordinates.x, (int) initialWorldCoordinates.y);
        //this.goalWorldCoordinates = Coordinates.worldCoordinatesToTileCoordinates((int) goalWorldCoordinates.x, (int) goalWorldCoordinates.y);

        this.initialWorldCoordinates = new int[]{0, 0};
        this.goalWorldCoordinates = new int[]{15, 5};

        if (this.initialWorldCoordinates[0] < this.goalWorldCoordinates[0]) {
            this.width = this.goalWorldCoordinates[0] - this.initialWorldCoordinates[0] + 1;
            initialNode[0] = 0;
            goalNode[0] = this.width - 1;
        } else {
            this.width = this.initialWorldCoordinates[0] - this.goalWorldCoordinates[0] + 1;
            goalNode[0] = 0;
            initialNode[0] = this.width - 1;
        }
//        this.width += marginX;

        if (this.initialWorldCoordinates[1] < this.goalWorldCoordinates[1]) {
            this.height = this.goalWorldCoordinates[1] - this.initialWorldCoordinates[1] + 1;
            initialNode[1] = 0;
            goalNode[1] = this.height - 1;
        } else {
            this.height = this.initialWorldCoordinates[1] - this.goalWorldCoordinates[1] + 1;
            goalNode[1] = 0;
            initialNode[1] = this.height - 1;
        }
//        this.height += marginY;

        costAssignedNodes = new boolean[width][height];
        visitedNodes = new boolean[width][height];
        collidableNodes = new boolean[width][height];

        collidableNodes[5][0] = true;
        collidableNodes[5][1] = true;
        collidableNodes[5][2] = true;
        collidableNodes[5][3] = true;
        collidableNodes[5][4] = true;

        collidableNodes[10][5] = true;
        collidableNodes[10][4] = true;
        collidableNodes[10][3] = true;
        collidableNodes[10][2] = true;


        costG = new int[width][height];
        costH = new int[width][height];
        costF = new int[width][height];
    }

    public void computeBestPath() {
        computeNodeCosts();

        System.out.println("costF:");
        Utils.printArray(costF);
        System.out.println("costAssignedNodes:");
        Utils.printArray(costAssignedNodes);

        ArrayList<int[]> path = findPath();

        System.out.println("The path is:");
        for (int i = 0; i < path.size(); i++) {
            System.out.println("Step " + i + " : " + path.get(i)[0] + ", " + path.get(i)[1]);
        }
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
        for (int j = y - 1; j <= (y + 1); j++) {
            for (int i = x - 1; i <= (x + 1); i++) {
                if (i == x && j == y) {
                    // Ignore
                } else if (!setNodeCostsTerminated && setNodeCost(i, j) != -1) {
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

    private int setNodeCost(int i, int j) {
        if (i >= 0 && i < width
                && j >= 0 && j < height
                && !costAssignedNodes[i][j]) {
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

    private ArrayList<int[]> findPath() {
        goalToInitialNodePath = new ArrayList<>();
        initialToGoalNodePath = new ArrayList<>();

        nextPathStep(goalNode[0], goalNode[1]);

        for (int i = (goalToInitialNodePath.size() - 1); i >= 0; i--) {
            int[] step = goalToInitialNodePath.get(i);
            initialToGoalNodePath.add(new int[]{- step[0], - step[1]});
        }

        return initialToGoalNodePath;
    }

    private void nextPathStep(int x, int y) {
        int minH = -1;
        int[] nodeWithLessHCost = new int[2];
        for (int j = y - 1; j <= (y + 1); j++) {
            for (int i = x - 1; i <= (x + 1); i++) {
                if ((i != x && j != y)
                        && i > 0 && i < width
                        && j > 0 && j < height) {
//                    System.out.println("AdriHell:: (x, y): " + x + ", " + y);
//                    System.out.println("AdriHell:: (x, y): " + i + ", " + j);
//                    System.out.println("AdriHell:: visited node? " + costAssignedNodes[i][j]);
                    if (!visitedNodes[i][j] && costAssignedNodes[i][j] && (minH == -1 || costH[i][j] < minH)) {
                        nodeWithLessHCost = new int[]{i, j};
                        minH = costH[i][j];
                    }
                }
            }
        }
        visitedNodes[nodeWithLessHCost[0]][nodeWithLessHCost[1]] = true;
        goalToInitialNodePath.add(new int[]{nodeWithLessHCost[0] - x, nodeWithLessHCost[1] - y});
        if (nodeWithLessHCost[0] == initialNode[0] && nodeWithLessHCost[1] == initialNode[1]) {
            pathComputationTerminated = true;
            return;
        }
        nextPathStep(nodeWithLessHCost[0], nodeWithLessHCost[1]);
    }
}
