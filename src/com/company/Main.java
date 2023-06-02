package com.company;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static final int BOARD_SIZE = 8;
    private static final String BLACK_EMOJI = " O ";
    private static final String WHITE_EMOJI = " X ";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String LINE = "-----------------------------------";
    private static int[][] botPositions = {
            {0, 1},
            {0, 3},
            {0, 5},
            {0, 7},
            {1, 0},
            {1, 2},
            {1, 4},
            {1, 6}
//            {5, 4},
//            {5, 2}
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
    private static String playerName;
    private static int[][] playerMustMove = new int[8][2];
    private static int[][] playerMustMoveTo = new int[8][2];
    private static int[][] playerMustMoveToFinal = new int[2][2];
    private static int[][] playerCanMove = new int[16][2];
    private static int[][] playerCanMoveFigures = new int[16][2];
    private static String currentMove = "player";
    private static boolean gameEnded = false;
    private static boolean selectedFigure = false;
    private static int[] figureMoveCoods = new int[2];
    private static int[][] canMoveToCoords = new int[2][2];

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to a Draughts Game");
        System.out.println("You will have 8 pieces of figures, that you can freely move around the board");
        System.out.println("You can only move one figure at a time, one step forward and one step sideways");
        System.out.println("You cannot enter a field on which you already have one piece. You can only walk forward, you cannot walk backwards");
        System.out.println("If you get a figure before your opponent's, you are forced to knock it out");
        System.out.println("You will play against a bot that will randomly choose its next positions");
        System.out.print("Now, enter you name: ");

        String rawPlayerName = sc.nextLine().trim().toLowerCase();
        playerName = rawPlayerName.substring(0, 1).toUpperCase() + rawPlayerName.substring(1);

        System.out.println(playerName + " Thanks for playing");
        System.out.println("Enjoy the game!");

        System.out.println(LINE);


        // Build board, select figure you want to move with, select destination where you want the figure
        // player move, check collision, bot move, check collision, build board -> -> ->
        do {
            // Reset variables
            playerMustMove = new int[8][2];
            playerMustMoveTo = new int[8][2];
            playerMustMoveToFinal = new int[2][2];
            playerCanMove = new int[16][2];
            playerCanMoveFigures = new int[16][2];
            selectedFigure = false;
            figureMoveCoods = new int[2];
            canMoveToCoords = new int[2][2];

            for (int i = 0; i < playerMustMove.length; i++) {
                playerMustMove[i][0] = -1;
                playerMustMoveTo[i][0] = -1;
            }

            playerMustMoveToFinal[0][0] = -1;
            playerMustMoveToFinal[1][0] = -1;
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
                    playerMustMove[mustMoveIndex][0] = playerPos[0];
                    playerMustMove[mustMoveIndex][1] = playerPos[1];
                    playerMustMoveTo[mustMoveIndex][0] = playerPosCopy[0];
                    playerMustMoveTo[mustMoveIndex][1] = playerPosCopy[1];

                    mustMoveIndex++;
                }

                playerPosCopy[1] -= 2;

                if(Arrays.stream(botPositions).anyMatch(botPos -> Arrays.equals(botPos, playerPosCopy))){
                    playerMustMove[mustMoveIndex][0] = playerPos[0];
                    playerMustMove[mustMoveIndex][1] = playerPos[1];
                    playerMustMoveTo[mustMoveIndex][0] = playerPosCopy[0];
                    playerMustMoveTo[mustMoveIndex][1] = playerPosCopy[1];

                    mustMoveIndex++;
                }
            }

            // Player's turn
            buildBoard();

            System.out.println("Now is " + playerName + "'s turn");

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

                if(playerMustMove[0][0] != -1 && Arrays.stream(playerMustMove).anyMatch(mustPos -> Arrays.equals(mustPos, figureMoveCoods))){
                    selectedFigure = true;
                }
                else if(playerMustMove[0][0] == -1 && Arrays.stream(playerCanMoveFigures).anyMatch(figurePos -> Arrays.equals(figurePos, figureMoveCoods))){
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

            // If there was position where player must go, update final variable
            int finalMoveIndex = 0;

            if(playerMustMove[0][0] != -1){
                for (int i = 0; i < playerMustMove.length; i++) {
                    if(Arrays.equals(playerMustMove[i], figureMoveCoods)){
                        playerMustMoveToFinal[finalMoveIndex][0] = playerMustMoveTo[i][0];
                        playerMustMoveToFinal[finalMoveIndex][1] = playerMustMoveTo[i][1];

                        finalMoveIndex++;
                    }
                }
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

                if(playerMustMove[0][0] != -1){
                    if(Arrays.stream(playerMustMoveToFinal).anyMatch(mustMovePos -> Arrays.equals(mustMovePos, newPos))){
                        playerRightCoord = true;
                    }
                    else{
                        System.out.println("Your selected a Wrong coordinates, please try it again");
                    }
                }
                else{
                    if(Arrays.stream(canMoveToCoords).anyMatch(canCoord -> Arrays.equals(canCoord, newPos))){
                        playerRightCoord = true;
                    }
                    else{
                        System.out.println("Your selected a Wrong coordinates, please try it again");
                    }
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

            boolean isGameOver = checkGameEnd();

            if(isGameOver){
                System.out.println();
                System.out.println();
                System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  Congratulations, you won!  " + ANSI_RESET);
                System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  At the end, you had " + playerPositions.length + " figures left  " + ANSI_RESET);

                boolean playAgain = playAgain();

                if(playAgain){
                    continue;
                }
                else{
                    break;
                }
            }

            botPlay();
        } while (!gameEnded);
    }

    private static void buildBoard(){
        playerCanMove = new int[16][2];
        int canCoordCount = 0;
        int[] cloneCurrentPos = new int[2];

        System.out.println();

        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println(LINE);

            for (int column = 0; column < BOARD_SIZE; column++) {
                String rowTextIndex = column == 0 ? row + " " : "";
                String emptyField = "   ";

                int[] currentPos = {row, column};

                // Check where figures can move
                System.arraycopy(currentPos, 0, cloneCurrentPos, 0, 2);
                cloneCurrentPos[0]++; // move it forwards
                cloneCurrentPos[1]++; // move it to the right

                if(
                        Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, cloneCurrentPos)) &&
                                Arrays.stream(playerPositions).noneMatch(pos -> Arrays.equals(pos, currentPos))
                ){
                    if(Arrays.stream(playerMustMoveTo).anyMatch(mustPos -> Arrays.equals(mustPos, currentPos))){
                        if(!selectedFigure){
                            emptyField = ANSI_PURPLE_BACKGROUND + WHITE_EMOJI + ANSI_RESET;
                        }
                        else if(Arrays.stream(playerMustMoveToFinal).anyMatch(mustFinalPos -> Arrays.equals(mustFinalPos, currentPos))){
                            emptyField = ANSI_RED_BACKGROUND + WHITE_EMOJI + ANSI_RESET;
                        }
                        else{
                            emptyField = WHITE_EMOJI;
                        }


                        cloneCurrentPos[0] = 0;
                        cloneCurrentPos[1] = 0;

                        playerCanMove[canCoordCount][0] = currentPos[0];
                        playerCanMove[canCoordCount][1] = currentPos[1];

                        System.out.print(rowTextIndex + "|" + emptyField);

                        continue;
                    }
                    else if(playerMustMoveTo[0][0] == -1 && Arrays.stream(canMoveToCoords).anyMatch(canPos -> Arrays.equals(canPos, currentPos))){
                        emptyField = ANSI_PURPLE_BACKGROUND + "   " + ANSI_RESET;
                    }
                    else if(!selectedFigure && playerMustMove[0][0] == -1){
                        emptyField = ANSI_GREEN_BACKGROUND + "   " + ANSI_RESET;
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
                    if(
                            Arrays.stream(playerMustMoveTo).anyMatch(mustPos -> Arrays.equals(mustPos, currentPos))
                    ){
                        emptyField = ANSI_PURPLE_BACKGROUND + WHITE_EMOJI + ANSI_RESET;

                        cloneCurrentPos[0] = 0;
                        cloneCurrentPos[1] = 0;

                        playerCanMove[canCoordCount][0] = currentPos[0];
                        playerCanMove[canCoordCount][1] = currentPos[1];

                        System.out.print(rowTextIndex + "|" + emptyField);

                        continue;
                    }
                    else if(playerMustMoveTo[0][0] == -1 && Arrays.stream(canMoveToCoords).anyMatch(canPos -> Arrays.equals(canPos, currentPos))){
                        emptyField = ANSI_PURPLE_BACKGROUND + "   " + ANSI_RESET;
                    }
                    else if(!selectedFigure && playerMustMove[0][0] == -1){
                        emptyField = ANSI_GREEN_BACKGROUND + "   " + ANSI_RESET;
                    }

                    playerCanMove[canCoordCount][0] = currentPos[0];
                    playerCanMove[canCoordCount][1] = currentPos[1];

                    playerCanMoveFigures[canCoordCount][0] = cloneCurrentPos[0];
                    playerCanMoveFigures[canCoordCount][1] = cloneCurrentPos[1];

                    canCoordCount++;
                }

                // Place figures onto board
                if(Arrays.stream(botPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    if(Arrays.stream(playerMustMove).anyMatch(mustPos -> Arrays.equals(mustPos, currentPos))){
                        emptyField = ANSI_PURPLE_BACKGROUND + WHITE_EMOJI + ANSI_RESET;
                    }
                    else{
                        emptyField = WHITE_EMOJI;
                    }
                }

                if(Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    if(selectedFigure && Arrays.equals(figureMoveCoods, currentPos)){
                        emptyField = ANSI_PURPLE_BACKGROUND + BLACK_EMOJI + ANSI_RESET;
                    }
                    else if(!selectedFigure && Arrays.stream(playerMustMove).anyMatch(mustPos -> Arrays.equals(mustPos, currentPos))){
                        emptyField = ANSI_RED_BACKGROUND + BLACK_EMOJI + ANSI_RESET;
                    }
                    else if(playerMustMove[0][0] == -1 && !selectedFigure && Arrays.stream(playerCanMoveFigures).anyMatch(figurePos -> Arrays.equals(figurePos, currentPos))){
                        emptyField = ANSI_YELLOW_BACKGROUND + BLACK_EMOJI + ANSI_RESET;
                    }
                    else{
                        emptyField = BLACK_EMOJI;
                    }
                }

                cloneCurrentPos[0] = 0;
                cloneCurrentPos[1] = 0;

                System.out.print(rowTextIndex + "|" + emptyField);
            }

            System.out.print("|");
            System.out.println();
        }

        System.out.println("----0---1---2---3---4---5---6---7--");

        System.out.println();
    }


    private static void botPlay(){
        currentMove = "bot";

        System.out.println();
        System.out.println("Now it bot's turn");

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


        System.out.println("Bot selected figure in Y Coordinate: " + botPositions[randomFigure][0] + " And in X Coordinate: " + botPositions[randomFigure][1]);

        collisionDetection();

        boolean isGameOver = checkGameEnd();

        if(isGameOver){
            System.out.println();
            System.out.println();
            System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  Unfortunately you lost, Bot won!  " + ANSI_RESET);
            System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  At the end bot has " + botPositions.length + " figures left  " + ANSI_RESET);

            gameEnded = true;

            playAgain();
        }


        currentMove = "player";
    }


    private static void collisionDetection(){
        int index = 0;
        int indexCount = 0;

        if(Objects.equals(currentMove, "player")){
            // Player figure deleted bot figure
            int[][] newRemArr = new int[botPositions.length - 1][2];

            for (int[] botPos: botPositions){
                index++;

                if(Arrays.stream(playerPositions).noneMatch(playerPos -> Arrays.equals(playerPos, botPos))) continue;

                for (int i = 0, k = 0; i < botPositions.length; i++) {
                    if(i == index - 1) continue;

                    newRemArr[k++] = botPositions[i];
                    indexCount++;
                }

                botPositions = newRemArr;
            }

            if(indexCount > 0){
                System.out.println();
                System.out.println(ANSI_BLACK + ANSI_RED_BACKGROUND + "  " + playerName + " hit Bot's figure  " + ANSI_RESET);
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
                    indexCount++;
                }

                playerPositions = newRemArr;
            }

            if(indexCount > 0){
                System.out.println();
                System.out.println(ANSI_BLACK + ANSI_RED_BACKGROUND + "  Bot hit " + playerName + "'s figure  " + ANSI_RESET);
            }
        }
    }


    private static boolean checkGameEnd(){
        if(botPositions.length == 0 || playerPositions.length == 0){
            // Game is over
            gameEnded = true;

            return true;
        }

        return false;
    }

    private static boolean playAgain(){
        Scanner sc = new Scanner(System.in);

        System.out.println();
        System.out.println("Do you want to play again? If yes, type 1, otherwise type 0");

        int newGame = sc.nextInt();
        sc.nextLine();

        if(newGame == 1){
            // Play again
            gameEnded = false;

            botPositions = new int[][]{
                {0, 1},
                {0, 3},
                {0, 5},
                {0, 7},
                {1, 0},
                {1, 2},
                {1, 4},
                {1, 6}
            };
            playerPositions = new int[][]{
                    {6, 1},
                    {6, 3},
                    {6, 5},
                    {6, 7},
                    {7, 0},
                    {7, 2},
                    {7, 4},
                    {7, 6},
            };

            currentMove = "player";

            System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  You are playing from the beginning  " + ANSI_RESET);
            System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  I assume you know the rules now  " + ANSI_RESET);
            System.out.println(ANSI_BLACK + ANSI_YELLOW_BACKGROUND + "  Good Luck!  " + ANSI_RESET);
            System.out.println();
            System.out.println();

            return true;
        }
        else{
            System.out.println("Thanks, have a great day!");

            return false;
        }
    }
}