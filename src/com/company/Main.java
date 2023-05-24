package com.company;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static int BOARD_SIZE = 8;
    private static String whiteEmoji = "\u26AB";
    private static String blackEmoji = "\u26AA";
    private static String ANSI_RESET = "\u001B[0m";
    private static String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static int[][] botPositions = {
            {0, 1},
            {0, 3},
            {0, 5},
            {0, 7},
            {1, 0},
            {1, 2},
            {1, 4},
            {1, 6}
    };
    private static int[][] playerPositions = {
            {6, 1},
            {6, 3},
            {6, 5},
            {6, 7},
            {7, 0},
            {7, 2},
            {7, 4},
            {7, 6}
    };
    private static int[][] playerCanMove = new int[16][2];

    public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);

        System.out.println(Arrays.deepToString(playerCanMove));
        buildBoard();
    }

    private static void buildBoard(){
        playerCanMove = new int[16][2];
        int canCoordCount = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println("---------------------------------");

            for (int column = 0; column < BOARD_SIZE; column++) {
                String emptyField = row % 2 != 0 && column % 2 == 0 || row % 2 == 0 && column % 2 != 0 ? " " : "x";

                int[] currentPos = {row, column};

                if(Arrays.stream(botPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    emptyField = blackEmoji;
                }

                if(Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, currentPos))){
                    emptyField = whiteEmoji;
                }


                int[] cloneCurrentPos = new int[2];
                System.arraycopy(currentPos, 0, cloneCurrentPos, 0, 2);
                cloneCurrentPos[0]++; // move it forwards
                cloneCurrentPos[1]++; // move it to the right

                if(
                        Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, cloneCurrentPos)) &&
                        Arrays.stream(playerPositions).noneMatch(pos -> Arrays.equals(pos, currentPos))
                ){
                    emptyField = ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;

                    playerCanMove[canCoordCount] = currentPos;

                    canCoordCount++;
                }

                cloneCurrentPos[1] -= 2; // Move it to the left

                if(
                        Arrays.stream(playerPositions).anyMatch(pos -> Arrays.equals(pos, cloneCurrentPos)) &&
                        Arrays.stream(playerPositions).noneMatch(pos -> Arrays.equals(pos, currentPos)) &&
                        Arrays.stream(playerCanMove).noneMatch(pos -> Arrays.equals(pos, currentPos))
                ){
                    emptyField = ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;

                    playerCanMove[canCoordCount] = currentPos;

                    canCoordCount++;
                }


                System.out.print("| " + emptyField + " ");
            }

            System.out.print("|");
            System.out.println();
        }

        System.out.println("---------------------------------");


        System.out.println(Arrays.deepToString(playerCanMove));
    }


    private static void botPlay(){
//        System.out.println(Arrays.deepToString(botPositions));

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
        System.out.println("-----");
    }
}
