# Radegast's Game

This Java program runs a game called “Radegast’s Game”.

## How It Works

The game consists of modifying a floating terrain by adding blocks of dirt at specific
coordinates in the command line. The player has 10 opportunities to make modifications, after
which the terrain is flooded with money and a score is calculated based on how much liquid
money the player is able to capture on the map inside of pits on the map terrain. Because the
terrain is floating and money is considered as liquid, the player should try to form pits that
will enable collecting as much money as possible resulting in the highest score and avoid slopes
that lead to the edges of the terrain as this will cause the money to fall off the map.

The algorithm starts by taking a map terrain from a txt file called “input.txt” and putting it
in a 2 dimensional matrix, which will be an object of the class “Map”, to make it possible to
work on. The program then prints the terrain in the console with numerical vertical and
alphabetic horizontal legends to make it easier to navigate on and starts taking 10 inputs in
total in the form of coordinates from the user. If the user enters a coordinate which is not
available on the map, the program prints “Not a valid step!” and proceeds to take another
input. This process continues until 10 valid coordinates are entered after which a Map class
method is called named “findLakes”. Firstly; this algorithm calls a method named “isHole”,
which is a recursive method that finds holes on the terrain map and marks them with the
letter “h”. It checks every location on the map to find out if it has any slope that leads to any
of the edges of our terrain map. While doing so it also puts “*” character at the end of the
value to indicate that location has already been visited. This prevents the recursive method
from entering into a loop. If a point cannot reach to the edge of the terrain via moving in any
direction (north, south, west, east and diagonal directions) and is blocked by a height greater
than itself, this means that money poured on that spot will not be able to flow off the terrain
and will be contained on the map, which means this point is a “hole” that contributes to one
of the lakes that will form on the terrain. The algorithm traverses every point on the map
and does the marking according to this logic. After this the “findLake” method now calls the
“calculateVolume” method which proceeds to name these holes with uppercase alphabetic
letters by grouping them. The holes that have any contact with one another on the map are
considered as being in the same group, in other words, they are part of the same lake and are
named with the same letter accordingly. This recursive method also stores the height values
of these holes in a list to use them in calculating the player score in the future. After the
method finishes, the findLakes method calls a third recursive method named
“findSurfaceLevel” which this time stores the height of the every boundary point encircling
the current lake. Among these heights, the smallest one is chosen and is assigned as the
current lake’s surface height. This means when the terrain is flooded with liquid money the
liquid level will rise to this level at most. Then the algorithm calculates the volume of the
lake by taking the difference of every point with this lowest lake surface height and takes
the square root of this value. This process is done for all the lakes available on the map and
at the end a player score is acquired. The program then prints the current modified map with
different lakes assigned with different letters of the alphabet and under it prints the player
score.

### Prerequisites

- An IDE or text editor to run the Java code.

## Running the tests

The player will enter the name of the input file which should include column and row numbers
of the terrain map and the terrain itself. Then the player will enter 10 coordinates in the
console in the form of:

- d3
- a1
- f5
- ...

to put blocks on the terrain map.

Some example input and output cases are provided in the repository.
