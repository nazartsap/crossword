package com.example.crossdle.game;

import com.example.crossdle.bank.WordBase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/***
 * Handles the generation of board layouts by picking random words from a word bank and
 * trying to fit them until a layout until it works.
 */
public class LayoutGenerator {
    public static char[][] layout;

    static void printLayout(char[][] board) {
        for (char[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Places a word horizontally in the layout grid.
     */
    public static void placeWordHorizontal(char[][] board, String word, int row, int column){
        for (int i = 0; i < word.length(); i++)
        {
            board[row][column] = word.charAt(i);
            column++;
        }
    }

    /**
     * Places a word vertically in the layout grid.
     */
    public static void placeWordVertical(char[][] board, String word, int row, int column){

        for (int i = 0; i < word.length(); i++)
        {
            board[row][column] = word.charAt(i);
            row++;
        }
    }

    public static void placeFirstWord(char[][] board, String word){
        Random rand = new Random();
        int upperbound = 2;
        int orientation = rand.nextInt(upperbound);
        int position = rand.nextInt(upperbound);
        if(orientation == 1){
            placeWordHorizontal(board, word, 0, position);
        }else{
            placeWordVertical(board, word, position, 0);
        }

    }

    public static boolean placeSecondWord(char[][] board, String word){
        if(board[0][2]!=Board.EMPTY_LAYOUT_CELL){
            for(int i = 0; i<board.length; i++){
                if(board[0][i]==word.charAt(0)){
                    placeWordVertical(board, word, 0, i);
                    return true;
                }
            }
        }else{
            for(int i = 0; i<board.length; i++){
                if(board[i][0]==word.charAt(0)){
                    placeWordHorizontal(board, word, i, 0);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks all horizontal neighbours to see if the cells are empty.
     */
    public static boolean checkHorizontalNeighbours(char[][] board,int row,int column, int length){
        for (int i = 1; i <=length; i++){
            if(board[row][column+i]!=Board.EMPTY_LAYOUT_CELL){
                return false;
            }
            if(row+1<6){
                if(board[row+1][column+i]!= Board.EMPTY_LAYOUT_CELL){
                    return false;
                }
            }
            if(row-1>-1){
                if(board[row-1][column+i]!=Board.EMPTY_LAYOUT_CELL){
                    return false;
                }
            }

        }
        if(column+length+1<6){
            if(board[row][column+length+1]!=Board.EMPTY_LAYOUT_CELL){
                return false;
            }
        }
        if(column-1>-1){
            return board[row][column - 1] == Board.EMPTY_LAYOUT_CELL;
        }
        return true;
    }

    /**
     * Checks all vertical neighbours to see if the cells are empty.
     */
    public static boolean checkVerticalNeighbours(char[][] board,int row,int column, int length){
        for (int i = 1; i <=length; i++){
            if(board[row+i][column]!=Board.EMPTY_LAYOUT_CELL){
                return false;
            }
            if(column+1<6){
                if(board[row+i][column+1]!=Board.EMPTY_LAYOUT_CELL){
                    return false;
                }
            }
            if(column-1>-1){
                if(board[row+i][column-1]!=Board.EMPTY_LAYOUT_CELL){
                    return false;
                }
            }
        }
        if(row+length+1<6){
            if(board[row+length+1][column]!=Board.EMPTY_LAYOUT_CELL){
                return false;
            }
        }
        if(row-1>-1){
            return board[row - 1][column] == Board.EMPTY_LAYOUT_CELL;
        }
        return true;
    }

    /**
     * Attempts to place a word withing the layout grid.
     */
    public static boolean placeRandomWord(char[][] board, String word){
        for (int row = 0; row < board.length; row++)
        {
            for (int column = 0; column < board[0].length; column++)
            {
                if(word.indexOf(board[row][column])!=-1){

                    if(canWordBePlaced(board, word, row, column, word.indexOf(board[row][column]))==("horizontal")){
                        placeWordHorizontal(board, word, row, column- word.indexOf(board[row][column]));
                        return true;
                    }
                    else if(canWordBePlaced(board, word, row, column, word.indexOf(board[row][column]))==("vertical")){
                        placeWordVertical(board, word, row- word.indexOf(board[row][column]), column);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if a word can be placed at a position on the layout grid.
     */
    public static String canWordBePlaced(char[][] board, String word, int row, int column, int intersectionIndex){
        int roomSecondHalf = word.length() -  intersectionIndex-1;
        int roomFirstHalf = word.length() - roomSecondHalf-1;


        if(column+roomSecondHalf<6 && column-roomFirstHalf>-1){
            if(checkHorizontalNeighbours(board, row, column, roomSecondHalf)){
                if(checkHorizontalNeighbours(board, row, column-roomFirstHalf, roomFirstHalf)){
                    return("horizontal");
                }
            }

        }
        if(row+roomSecondHalf<6 && row-roomFirstHalf>-1){
            if(checkVerticalNeighbours(board, row, column, roomSecondHalf)){
                if(checkVerticalNeighbours(board, row-roomFirstHalf, column, roomFirstHalf)){
                    return("vertical");
                }
            }
        }
        return null;
    }

    /**
     * Generates a crossword and returns true if was successful.
     */
    public static boolean generateCrossword(){
        char[][] board = Board.generateEmptyLayout(Board.DEFAULT_SIZE);

        String firstWord = WordBase.cached.getFiveLetterRandom();
        placeFirstWord(board, firstWord);
        System.out.println(firstWord);

        int count = 0;
        while (true) {
            String secondWord = WordBase.cached.getFiveLetterRandom();
            if(placeSecondWord(board,secondWord)){
                System.out.println(secondWord);
                break;
            }
        }

        while(true){
            String thirdWord = WordBase.cached.getFourLetterRandom();
            if(placeRandomWord(board, thirdWord)){
                System.out.println(thirdWord);
                break;
            }
        }

        while(count<100){
            count++;
            String fourthWord = WordBase.cached.getFourLetterRandom();
            if(placeRandomWord(board,fourthWord)){
                System.out.println(fourthWord);
                printLayout(board);
                layout = board;
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a generated board.
     */
    public static char[][] returnBoard() throws IOException {
        while(true){
            if(generateCrossword()){
                break;
            }
        }
        return layout;
    }
}
