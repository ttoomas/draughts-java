package com.company;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static int BOARD_SIZE = 8;
    private static String whiteEmoji = "\u26AB";
    private static String blackEmoji = "\u26AA";
    private static String ANSI_RESET = "\u001B[0m";
    private static String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static int[][] botPositions = {
            {0, 1},
            {0, 3},
            {0, 5},
            {0, 7},
            {1, 0},
            {1, 2},
//            {1, 4},
//            {1, 6}
            {5, 0},
            {5, 6}
    };
    private static int[][] playerPositions = {
            {6, 1},
            {6, 3},
            {6, 5},
            {6, 7},
            {7, 0},
            {7, 2},
            {7, 4},
            {7, 6},
    };
    private static int[][] playerMustMove = new int[8][2];
    private static int[][] playerCanMove = new int[16][2];
    private static int[][] playerCanMoveFigures = new int[16][2];
    private static String currentMove = "player";
    private static boolean gameEnded = false;
    private static boolean selectedFigure = false;
    private static int[] figureMoveCoods = new int[2];
    private static int[][] canMoveToCoords = new int[2][2];

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Build board, select figure you want to move with, select destination where you want the figure
        // player move, check collision, bot move, check collision, build board -> -> ->
        do {
            // Reset variables
            playerMustMove = new int[8][2];
            playerCanMove = new int[16][2];
            playerCanMoveFigures = new int[16][2];
            selectedFigure = false;
            figureMoveCoods = new int[2];
            canMoveToCoords = new int[2][2];

            for(int[] mustPos : playerMustMove){
                mustPos[0] = -1;
            }

            canMoveToCoords[0][0] = -1;
            canMoveToCoords[1][0] = -1;

            // check if there is any positions where figure must move
            int mustMoveIndex = 0;

            for (int[] playerPos : playerPositions){
                int[] playerPosCopy = new int[2];
                System.arraycopy(playerPos, 0, playerPosCopy, 0, 2);
                playerPosCopy[0]--; // Forwards
                playerPosCopy[1]++; // Left

                if(Arrays.stream(botPositions).anyMatch(botPos -> Arrays.equals(botPos, playerPosCopy))){
                    playerMustMove[mustMoveIndex] = playerPos;

                    mustMoveIndex++;
                }

                playerPosCopy[1] -= 2;

                if(
                    Arrays.stream(botPositions).anyMatch(botPos -> Arrays.equals(botPos, playerPosCopy))
                ){
                    playerMustMove[mustMoveIndex] = playerPos;

                    mustMoveIndex++;
                }
            }

            System.out.println(Arrays.deepToString(playerMustMove));

            // Player's turn
            buildBoard();

            System.out.println("Now is player's turn");

            // Figure you want to move
            System.out.println("Enter coordinates of figure you want to move");

            do {
                System.out.print("Enter Y Coordinate: ");
                int figureYCoord = sc.nextInt();
                sc.nextLine();
                if(figureYCoord < 0 || figureYCoord > 7){
                    System.out.println("Y Coordinate is out of bound, bust be between 0 and 7. Try it again!");

                    continue;
                }

                System.out.print("Enter X Coordinate: ");
                int figureXCoord = sc.nextInt();
                sc.nextLine();
                if(figureXCoord < 0 || figureXCoord > 7){
                    System.out.println("X Coordinate is out of bound, bust be between 0 and 7. Try it again!");

                    continue;
                }

                figureMoveCoods[0] = figureYCoord;
                figureMoveCoods[1] = figureXCoord;

                if(Arrays.stream(playerCanMoveFigures).anyMatch(figurePos -> Arrays.equals(figurePos, figureMoveCoods))){
                    selectedFigure = true;
                }
                else{
                    System.out.println("You selected a wrong coordinates, please try it again");
                }
            } while (!selectedFigure);

            // select positions you can move to
            for (int i = 0; i < playerCanMoveFigures.length; i++) {
                if(Arrays.equals(playerCanMoveFigures[i], figureMoveCoods)){
                    if(canMoveToCoords[0][0] == -1){
                        canMoveToCoords[0] = playerCanMove[i];
                    }
                    else{
                        canMoveToCoords[1] = playerCanMove[i];
                    }
                }
            }

            if(canMoveToCoords[1][0] == -1) canMoveToCoords = new int[][]{canMoveToCoords[0]};

            // If there is bot figure, leave only one position in array
            if(
                Arrays.stream(botPositions).anyMatch(botPos -> Arrays.equals(botPos, canMoveToCoords[0]))
            ){
                canMoveToCoords = new int[][]{canMoveToCoords[0]};
            }
            else if(Arrays.stream(botPositions).anyMatch(botPos -> Arrays.equals(botPos, canMoveToCoords[canMoveToCoords.length - 1]))){
                canMoveToCoords = new int[][]{canMoveToCoords[canMoveToCoords.length - 1]};
            }

            buildBoard();


            // New coordinates
            boolean playerRightCoord = false;
            int[] newPos = new int[2];

            System.out.println("Now select new coordinates of selected figure");

            do {
                System.out.print("Enter Y coordinate: ");
                int newYCoord = sc.nextInt();
                sc.nextLine();
                if(newYCoord < 0 || newYCoord > 7){
                    System.out.println("Y Coordinate is out of bound, bust be between 0 and 7. Try it again!");

                    continue;
                }

                System.out.print("Enter X Coordinate: ");
                int newXCoord = sc.nextInt();
                sc.nextLine();
                if(newXCoord < 0 || newXCoord > 7){
                    System.out.println("X Coordinate is out of bound, bust be between 0 and 7. Try it again!");

                    continue;
                }

                newPos[0] = newYCoord;
                newPos[1] = newXCoord;

                if(Arrays.stream(canMoveToCoords).anyMatch(canCoord -> Arrays.equals(canCoord, newPos))){
                    playerRightCoord = true;
                }
                else{
                    System.out.println("Your selected a Wrong coordinates, please try it again");
                }
            } while (!playerRightCoord);


            // Find index of selected figure and update it to the new positions
            int figureIndex = -1;

            for (int i = 0; i < playerPositions.length; i++) {
                if(Arrays.equals(playerPositions[i], figureMoveCoods)){
                    figureIndex = i;

                    break;
                }
            }

            playerPositions[figureIndex][0] = newPos[0];
            playerPositions[figureIndex][1] = newPos[1];

            collisionDetection();
            botPlay();

            System.out.println(playerPositions.length);
            System.out.println(botPositions.length);
        } while (!gameEnded);
    }

    private static void buildBoard(){
        playerCanMove = new int[16][2];
        int canCoordCount = 0;
        int[] cloneCurrentPos = new int[2];

        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println("---------------------------------");

            for (int column = 0; column < BOARD_SIZE; column++) {

                String emptyField = row % 2 != 0 && column % 2 == 0 || row % 2 == 0 && column % 2 != 0 ? " " : "x";

                int[] currentPos = {row, column};

                // Check where figures can move
                System.arraycopy(currentPos, 0, cloneCurrentPos, 0, 2);
                cloneCurrentPos[0]++; // move it forwards
                cloneCurrentPos[1]++; // move it to the right

                if(
                        Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, cloneCurrentPos)) &&
                        Arrays.stream(playerPositions).noneMatch(pos -> Arrays.equals(pos, currentPos))
                ){
                    if(Arrays.stream(canMoveToCoords).anyMatch(canPos -> Arrays.equals(canPos, currentPos))){
                        emptyField = ANSI_PURPLE_BACKGROUND + " " + ANSI_RESET;
                    }
                    else if(canMoveToCoords[0][0] != -1){
                        emptyField = " ";
                    }
                    else if(playerMustMove[0][0] == -1){
                        emptyField = ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;
                    }

                    playerCanMove[canCoordCount][0] = currentPos[0];
                    playerCanMove[canCoordCount][1] = currentPos[1];

                    playerCanMoveFigures[canCoordCount][0] = cloneCurrentPos[0];
                    playerCanMoveFigures[canCoordCount][1] = cloneCurrentPos[1];

                    canCoordCount++;
                }


                cloneCurrentPos[1] -= 2; // Move it to the left

                if(
                        Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, cloneCurrentPos)) &&
                        Arrays.stream(playerPositions).noneMatch(pos -> Arrays.equals(pos, currentPos))
                ){
                    if(Arrays.stream(canMoveToCoords).anyMatch(canPos -> Arrays.equals(canPos, currentPos))){
                        emptyField = ANSI_PURPLE_BACKGROUND + " " + ANSI_RESET;
                    }
                    else{
                        emptyField = ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;
                    }

                    playerCanMove[canCoordCount][0] = currentPos[0];
                    playerCanMove[canCoordCount][1] = currentPos[1];

                    playerCanMoveFigures[canCoordCount][0] = cloneCurrentPos[0];
                    playerCanMoveFigures[canCoordCount][1] = cloneCurrentPos[1];

                    canCoordCount++;
                }

                // Place figures onto board
                if(Arrays.stream(botPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    emptyField = blackEmoji;
                }

                if(Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    if(selectedFigure && Arrays.equals(figureMoveCoods, currentPos)){
                        emptyField = ANSI_PURPLE_BACKGROUND + whiteEmoji + ANSI_RESET;
                    }
                    else if(!selectedFigure && Arrays.stream(playerCanMoveFigures).anyMatch(figurePos -> Arrays.equals(figurePos, currentPos))){
                        emptyField = ANSI_YELLOW_BACKGROUND + whiteEmoji + ANSI_RESET;
                    }
                    else{
                        emptyField = whiteEmoji;
                    }
                }

                cloneCurrentPos[0] = 0;
                cloneCurrentPos[1] = 0;


                System.out.print("| " + emptyField + " ");
            }

            System.out.print("|");
            System.out.println();
        }

        System.out.println("---------------------------------");
    }


    private static void botPlay(){
//        System.out.println(Arrays.deepToString(botPositions));
        currentMove = "bot";

        // Go one step forward and one step to left/right side
        int randomFigure;
        int side;
        int[] clonedBotPosition = new int[2];
        boolean isBotCollision = false;

        do {
            randomFigure = (int) Math.floor(Math.random() * botPositions.length);
            side = Math.random() < 0.5 ? -1 : 1;
            if(botPositions[randomFigure][1] <= 0) side = 1;
            if(botPositions[randomFigure][1] >= 7) side = -1;

            System.arraycopy(botPositions[randomFigure], 0, clonedBotPosition, 0, 2);

            clonedBotPosition[0]++;
            clonedBotPosition[1] += side;

            for(int[] pos : botPositions){
                if (Arrays.equals(pos, clonedBotPosition)) {
                    isBotCollision = true;
                    break;
                }
                else{
                    isBotCollision = false;
                }
            }
        } while (isBotCollision || botPositions[randomFigure][0] >= 7);

        botPositions[randomFigure][0]++;
        botPositions[randomFigure][1] += side;

//        System.out.println(Arrays.deepToString(botPositions));
        collisionDetection();

        currentMove = "player";
    }


    private static void collisionDetection(){
        int index = 0;

        if(Objects.equals(currentMove, "player")){
            // Player figure deleted bot figure
            int[][] newRemArr = new int[botPositions.length - 1][2];

            for (int[] botPos: botPositions){
                index++;

                if(Arrays.stream(playerPositions).noneMatch(playerPos -> Arrays.equals(playerPos, botPos))) continue;

                for (int i = 0, k = 0; i < botPositions.length; i++) {
                    if(i == index - 1) continue;

                    newRemArr[k++] = botPositions[i];
                }
                botPositions = newRemArr;
            }
        }
        else{
            // Bot figure deleted player figure
            int[][] newRemArr = new int[playerPositions.length - 1][2];

            for (int[] playerPos: playerPositions){
                index++;

                if(Arrays.stream(botPositions).noneMatch(botPos -> Arrays.equals(botPos, playerPos))) continue;

                for (int i = 0, k = 0; i < playerPositions.length; i++) {
                    if(i == index - 1) continue;

                    newRemArr[k++] = playerPositions[i];
                }
                playerPositions = newRemArr;
            }
        }
    }
}