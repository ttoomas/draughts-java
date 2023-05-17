package com.company;

import java.util.Scanner;

public class Main {
    private static int BOARD_SIZE = 9;

    public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);

        buildBoard();
    }


    private static void buildBoard(){
        int fullCount = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println("-------------------------------------");

            for (int column = 0; column < BOARD_SIZE; column++) {
                String emptyField = fullCount % 2 != 0 ? " " : "x";

                System.out.print("| " + emptyField + " ");

                fullCount++;
            }

            System.out.print("|");
            System.out.println();
        }

        System.out.println("-------------------------------------");
    }
}
