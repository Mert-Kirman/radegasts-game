/**
 * Program that takes user input for modifying the game terrain and proceeds the game by calling Map class methods
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RadegastsGame {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the Radegast's Game !");
        System.out.println("Please enter the name of the input file:");
        String fileName = input.nextLine(); // Filename of the input file
        File file = new File(fileName); // Create file object to open the file
        Scanner inputFile = new Scanner(file); // Scanner object for reading the file contents

        int numOfColumns = inputFile.nextInt(); // Get the number of columns of the 2D matrix representing a terrain map
        int numOfRows = inputFile.nextInt();

        ArrayList<ArrayList<String>> map = new ArrayList<>(); // Arraylist for storing the terrain 2D matrix data
        ArrayList<String> mapRows = new ArrayList<>();

        for(int i = 0; i < numOfRows; i++) {
            // Read integers from the 2D matrix given in input file, place them inside a row accordingly
            for(int j = 0; j < numOfColumns; j++) {
                String rowNum = inputFile.next();
                mapRows.add(rowNum);
            }
            // Add copy of the current row to the 2D map ArrayList
            map.add(new ArrayList<>(mapRows));
            mapRows.clear();
        }

        inputFile.close();

        // Create the terrain object, which will be modified as the game progresses
        Map terrain = new Map(map);

        // Print the current terrain
        terrain.printMap();

        // User will do a total of 10 modifications in the terrain map
        int modificationNumber = 1;
        while(modificationNumber <= 10) {
            System.out.printf("Add stone %d / 10 to coordinate:", modificationNumber);
            String coordinate = input.nextLine();

            if(terrain.validStep(coordinate)) { // If the coordinates entered by the user are valid
                String column = "";
                String row = "";
                for(int i = 0; i < coordinate.length(); i++) {
                    char ch = coordinate.charAt(i);
                    if(Character.isLetter(ch)) {
                        column += ch;
                    }
                    else {
                        row += ch;
                    }
                }
                terrain.modifyMap(column, row); // Increment the value by 1 at the specified coordinate
                modificationNumber++;
                terrain.printMap();
                System.out.println("---------------");
            }
            else {
                System.out.println("Not a valid step!");
            }
        }
        input.close();

        terrain.findLakes();
        terrain.printMap();
        System.out.printf("Final score: %.2f", terrain.getScore());
    }
}