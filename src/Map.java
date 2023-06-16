/**
 * Class that contains methods and constants used during the game
 */

import java.util.ArrayList;

public class Map {
    private ArrayList<ArrayList<String>> map; // 2D Array List that we will use as a map for heights of the terrain
    private ArrayList<String> horizontalCoordinates = new ArrayList<>(); // Includes all the horizontal alphabetic coordinates of the 2D matrix
    private ArrayList<String> allPossibleCoordinates = new ArrayList<>(); // Includes all the valid coordinates the user can give as input
    private String currentLakeName; // Name of the current lake which will be used in the findLake() method
    private int numOfLettersUsed = 1; //Indicates how many times the name of the lake has been changed
    private ArrayList<Integer> lakeGroundHeight = new ArrayList<>(); // Arraylist for storing the height of the ground of a lake
    private ArrayList<Integer> lakeSurfaceHeight = new ArrayList<>(); // Arraylist for storing the height of the boundary encircling the lake
    private ArrayList<Double> score = new ArrayList<>(); // For storing square root value of lakeVolume of each lake
    private final int[][] DIRECTIONS = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}}; // For moving in the terrain map

    // No arg constructor
    public Map() {
    }

    public Map(ArrayList<ArrayList<String>> map) {
        this.map = map;
        this.currentLakeName = "A";
        this.createHorizontalCoordinateList();
    }

    /**
     * Method for printing the terrain map
     */
    public void printMap() {
        int columnNum = this.map.get(0).size(); // Number of integers in a row of the map matrix

        for(int row = 0; row < this.map.size(); row++) {
            if(row < 10) {
                System.out.print("  " + row); // Print numerical vertical coordinates as legend
            }
            else {
                System.out.print(" " + row);
            }

            for(int column = 0; column < columnNum; column++) {
                if(this.map.get(row).get(column).length() == 1) {
                    System.out.print("  " + this.map.get(row).get(column));
                }
                else {
                    System.out.print(" " + this.map.get(row).get(column));
                }
            }
            System.out.print(" ");
            System.out.println();
        }

        // Position the horizontal alphabetical coordinate in a correct format
        System.out.print("   ");

        // Print two whitespaces for single letter coordinates and one whitespace for the two letter coordinates
        for(String s : this.horizontalCoordinates) {
            if(s.length() == 1) { // Single letter horizontal coordinate
                System.out.print("  " + s);
            }
            else { // Two letter horizontal coordinate
                System.out.print(" " + s);
            }
        }
        System.out.print(" ");
        System.out.println();
    }

    /**
     * Method for modifying the 2D matrix of the current object
     * @param column Number of the matrix column we want to modify
     * @param rowString Number of the matrix row we want to modify
     */
    public void modifyMap(String column, String rowString) {
        int row = Integer.parseInt(rowString);
        int col = this.horizontalCoordinates.indexOf(column); // Find the index which has the string value of the "column" parameter
        String oldValue = this.map.get(row).get(col);
        String newValue = "" + (Integer.parseInt(oldValue) + 1);
        this.map.get(row).set(col, newValue); // Increase the value at the given coordinates by 1
    }

    /**
     * Method for creating an arraylist including horizontal alphabetical coordinates of the Map object's 2D matrix
     */
    public void createHorizontalCoordinateList() {
        int columnNum = this.map.get(0).size(); // Number of integers in a row of the map matrix

        for(int i = 0; i < columnNum; i++) {
            char letter = 'a';
            int numOfLetters = 'z' - 'a' + 1; // Total number of letters in english alphabet

            // Add alphabetic horizontal coordinates to the object's horizontal coordinates array list
            if(i < numOfLetters) { // Single letter horizontal coordinates
                this.horizontalCoordinates.add("" + (char)(letter + i));
            }
            else { // Multiple letter coordinates if number of columns in a row exceeds the number of letters in the alphabet
                int firstLetter = 'a' + i / numOfLetters - 1;
                int secondLetter = 'a' + i % numOfLetters;
                this.horizontalCoordinates.add((char)firstLetter + "" + (char)secondLetter);
            }
        }
        this.allCoordinates();
    }

    /**
     * Method for finding all the valid coordinates that exist in the 2D Matrix map
     */
    public void allCoordinates() {
        for(int i = 0; i < this.map.size(); i++) {
            for(String s : horizontalCoordinates) {
                this.allPossibleCoordinates.add(s + i);
            }
        }
    }

    /**
     * Method for checking whether the input entered by the user is valid or not
     * @param input The coordinate entered by the user
     * @return True if the input coordinate exists in the 2D Matrix
     */
    public boolean validStep(String input) {
        if(this.allPossibleCoordinates.contains(input)) {
            return true;
        }
        return false;
    }

    /**
     * Method that finds lakes on the terrain map
     */
    public void findLakes() {
        // First, find all the holes on the given terrain map and mark them
        for(int row = 0; row < this.map.size(); row++) {
            for(int column = 0; column < this.map.get(0).size(); column++) {
                int startingValue = findValue(row, column);
                String result = isHole(row, column, startingValue);
                if(result.equals("blocked")) {
                    this.map.get(row).set(column, "h" + findValue(row, column) + "*"); // Mark current location as a hole and as visited
                }
            }
        }

        // Then name adjacent holes with the same name indicating they form a lake, calculate this lake's volume and its score
        for(int row = 0; row < this.map.size(); row++) {
            for(int column = 0; column < this.map.get(0).size(); column++) {
                if(this.map.get(row).get(column).startsWith("h")) { // If current location is a hole
                    calculateVolume(row, column);
                    findSurfaceLevel(row, column);

                    // Find the lowest point that is a part of the surface boundary of the current lake
                    int lowestSurfaceHeight = this.lakeSurfaceHeight.get(0);
                    for(int i:this.lakeSurfaceHeight) {
                        if(i < lowestSurfaceHeight) {
                            lowestSurfaceHeight = i;
                        }
                    }

                    //Find total volume of the current lake, take the square root of it and add its score to the total score
                    int totalGroundHeight = 0;
                    for(int i : this.lakeGroundHeight) {
                        totalGroundHeight += i;
                    }
                    int totalSurfaceHeight = this.lakeGroundHeight.size() * lowestSurfaceHeight;
                    int totalLakeVolume = totalSurfaceHeight - totalGroundHeight;
                    this.score.add(Math.pow(totalLakeVolume, 0.5));

                    this.lakeGroundHeight.clear();
                    this.lakeSurfaceHeight.clear();
                    changeLakeName();
                }
            }
        }
    }

    /**
     * Recursive method that finds holes on the terrain map and marks them with the letter "h"
     * @param row current row index of the terrain matrix
     * @param column current column index of the terrain matrix
     * @param startingVal location where we start trying whether the water poured on here can flow outside or not
     * @return whether all directions around the current location block the water flow or not
     */
    public String isHole(int row, int column, int startingVal) {
        int currentVal = findValue(row, column); // Height value of the current location

        for(int[] d:this.DIRECTIONS) {
            if((row + d[0] < 0) || (row + d[0] >= this.map.size()) || (column + d[1] < 0) || (column + d[1] >= this.map.get(0).size())) {
                return "leak"; // Next location is out of terrain boundaries, so the water will leak
            }
            if(isVisited(this.map.get(row + d[0]).get(column + d[1]))) { // If next location is visited, skip it
                continue;
            }
            int nextLocationVal = findValue(row + d[0], column + d[1]); // Height value of the next location
            if(startingVal >= nextLocationVal) {
                this.map.get(row).set(column, currentVal + "*");
                String result = isHole(row + d[0], column + d[1], currentVal);
                this.map.get(row).set(column, currentVal + "");
                if(result.equals("leak")) { // If water poured onto this location cannot leak off the map it is part of a lake
                    return "leak";
                }
            }
        }
        return "blocked"; // Water cannot move anywhere from this point
    }

    /**
     * Recursive method that names the current lake with letter/letters from the alphabet and adds each location's height to an array list
     * @param row current row
     * @param column current column
     */
    public void calculateVolume(int row, int column) {
        this.lakeGroundHeight.add(findValue(row, column)); // Add the height of the current location to the array list
        this.map.get(row).set(column, this.currentLakeName); // Name this location with the current lake name
        for(int[] d:DIRECTIONS) {
            if(this.map.get(row + d[0]).get(column + d[1]).startsWith("h")) { // Spot and move to the adjacent hole that is a part of the same lake
                calculateVolume(row + d[0], column + d[1]);
            }
        }
    }

    /**
     * Recursive method for adding the heights of the surface boundary points of a given lake to an array list
     * @param row current row
     * @param column current column
     */
    public void findSurfaceLevel(int row, int column) {
        // First add the heights of the surface boundary points if they are not already in the array list
        for(int[] d:DIRECTIONS) {
            if(!this.map.get(row + d[0]).get(column + d[1]).startsWith(this.currentLakeName)) {
                int boundaryHeight = findValue(row + d[0], column + d[1]);
                if(!this.lakeSurfaceHeight.contains(boundaryHeight)) {
                    this.lakeSurfaceHeight.add(boundaryHeight);
                }
            }
        }

        // Then move on to the adjacent hole that is a part of the same lake
        this.map.get(row).set(column, this.currentLakeName + "*"); // Mark this location as visited with "*"
        for(int[] d:DIRECTIONS) {
            if(this.map.get(row + d[0]).get(column + d[1]).startsWith(this.currentLakeName) && !this.map.get(row + d[0]).get(column + d[1]).endsWith("*")) {
                findSurfaceLevel(row + d[0], column + d[1]);
            }
        }
        this.map.get(row).set(column, this.currentLakeName); // Unmark this location
    }

    /**
     * Method for finding the height value of a given location
     * @param row specified row
     * @param column specified column
     * @return Height value of the specified location in the matrix
     */
    public int findValue(int row, int column) {
        String currentValueString = this.map.get(row).get(column);
        String currentValueHeight = "";
        for(int i = 0; i < currentValueString.length(); i++) {
            if(Character.isDigit(currentValueString.charAt(i))) {
                currentValueHeight += currentValueString.charAt(i);
            }
        }
        return Integer.parseInt(currentValueHeight);
    }

    /**
     * Method for checking if the specified location has been visited before inside the recursion
     * @param currentValueString current location
     * @return true if current location has been visited before
     */
    public boolean isVisited(String currentValueString) {
        if(currentValueString.endsWith("*")) { // "*" is the mark we put at the end of a value string indicating being visited before
            return true;
        }
        return false;
    }

    /**
     * Method that sets the next lake name
     */
    public void changeLakeName() {
        int currentLakeNameInt = this.numOfLettersUsed; // States how many letters we have used so far
        int numOfLetters = 'Z' - 'A' + 1; // Number of letters in the alphabet
        if(currentLakeNameInt >= numOfLetters) { // Two digit alphabetic lake name
            int firstLetter;
            int secondLetter;
            if(currentLakeNameInt % numOfLetters != 0) {
                firstLetter = 'A' + currentLakeNameInt / numOfLetters - 1;
                secondLetter = 'A' + currentLakeNameInt % numOfLetters;
            }
            else { // If modulo gives zero we could either be at _A or _Z, so we need to differentiate
                if(this.currentLakeName.charAt(this.currentLakeName.length() - 1) == 'Y') { // If current name of the lake ends with 'Y'
                    firstLetter = 'A' + currentLakeNameInt / numOfLetters - 2;
                    secondLetter = 'Z'; // Next letter should be 'Z'
                }
                else {
                    firstLetter = 'A' + currentLakeNameInt / numOfLetters - 1;
                    secondLetter = 'A';
                }

            }
            this.currentLakeName = "" + (char)firstLetter + (char)secondLetter;
        }
        else { // Single digit alphabetic lake name
            this.currentLakeName = "" + (char)('A' + currentLakeNameInt);
        }
        this.numOfLettersUsed++;
    }

    /**
     * Method for calculating the total score of the game
     * @return total score
     */
    public double getScore() {
        double score = 0;
        for(double d:this.score) {
            score += d;
        }
        return score;
    }
}
