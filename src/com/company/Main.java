package com.company;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static int BOARD_SIZE = 8;
    private static String whiteEmoji = "\u26AB";
    private static String blackEmoji = "\u26AA";
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

    public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);

        for (int i = 0; i < 10; i++) {
            buildBoard();
            botPlay();
        }
    }

    private static void buildBoard(){
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println("---------------------------------");

            for (int column = 0; column < BOARD_SIZE; column++) {
                String emptyField = row % 2 != 0 && column % 2 == 0 || row % 2 == 0 && column % 2 != 0 ? " " : "x";

                int[] currentPos = {row, column};

                for(int[] pos: botPositions){
                    if (Arrays.equals(pos, currentPos)) {
                        emptyField = blackEmoji;

                        break;
                    }
                }

                for(int[] pos: playerPositions){
                    if (Arrays.equals(pos, currentPos)) {
//                        emptyField = whiteEmoji;

                        break;
                    }
                }


                System.out.print("| " + emptyField + " ");
            }

            System.out.print("|");
            System.out.println();
        }

        System.out.println("---------------------------------");
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
