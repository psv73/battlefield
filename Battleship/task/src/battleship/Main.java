package battleship;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    final static int fieldSize = 11;

    static int[][] battleField1 = new int[fieldSize][fieldSize];
    static int[][] battleField2 = new int[fieldSize][fieldSize];

    static ArrayList<Ship> shipsPlayer1 = createShipArray();
    static ArrayList<Ship> shipsPlayer2 = createShipArray();


    private final static char fog = '~';
    private final static char hitChar = 'X';
    private final static char shipChar = 'O';
    private final static char missChar = 'M';

    public static void main(String[] args) {
        // Write your code here

        Scanner scanner = new Scanner(System.in);

        fillBattleField(battleField1);
        fillBattleField(battleField2);

        System.out.println("Player 1, place your ships on the game field\n");
        addShips(scanner, shipsPlayer1, battleField1);
        printBattleField(true, battleField1);
        System.out.println("\nPress Enter and pass the move to another player");
        System.out.print("...");
        scanner.nextLine();
        scanner.nextLine();

        System.out.println("Player 2, place your ships on the game field\n");
        addShips(scanner, shipsPlayer2, battleField2);
        printBattleField(true, battleField2);
        System.out.println();

        startGame(scanner);
    }

    static void startGame(Scanner scanner) {
        // Start the game
        boolean player1 = true;
        boolean repeat = false;
        int[][] battleField = battleField1;
        ArrayList<Ship> ships = shipsPlayer1;

        while (true) {

            if (!repeat) {
                System.out.println("Press Enter and pass the move to another player");
                System.out.print("...");
                scanner.nextLine();
                scanner.nextLine();

                System.out.println();
                if (player1) {
                    ships = shipsPlayer2;
                    battleField = battleField2;

                    printBattleField(false, battleField2);
                    System.out.println("---------------------");
                    printBattleField(true, battleField1);
                } else {
                    ships = shipsPlayer1;
                    battleField = battleField1;

                    printBattleField(false, battleField1);
                    System.out.println("---------------------");
                    printBattleField(true, battleField2);
                }
                System.out.println("\nPlayer " + (player1 ? 1 : 2) + ", it's you turn:\n");
            } else {
                repeat = false;
                System.out.println();
            }

            System.out.print("> ");

            String str = scanner.next();

            System.out.println();

            int[] coordinates = checkCoordinates(str.toUpperCase());

            if (coordinates[0] == 0) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                repeat = true;
            } else if (checkShoot(coordinates, battleField)) {

                boolean shipsExists = false;
                String message = "";

                for (Ship ship : ships) {
                    if (!ship.isDestroyed()) {
                        int[][] array = ship.getLocation();
                        for (int[] ints : array) {
                            if (ints[0] == coordinates[0] && ints[1] == coordinates[1]) {
                                ship.setCellDestroyed();
                                if (ship.isDestroyed()) {
                                    message = "You sank a ship!";
                                } else {
                                    message = "You hit a ship!";
                                }
                                player1 = !player1;
                                break;
                            }
                        }
                    }
                }

                for (Ship ship : ships
                ) {
                    if (!ship.isDestroyed()) shipsExists = true;
                }

                if (!shipsExists) {
                    System.out.println("You sank the last ship. You won. Congratulations!");
                    break;
                } else {
                    System.out.println(message);
                }
            } else {
                player1 = !player1;
                System.out.println("You missed!");
            }
        }
    }

    static ArrayList<Ship> createShipArray() {
        ArrayList<Ship> ships = new ArrayList<>();

        ships.add(new Ship("Aircraft Carrier", 5, false));
        ships.add(new Ship("Battleship", 4, false));
        ships.add(new Ship("Submarine", 3, false));
        ships.add(new Ship("Cruiser", 3, false));
        ships.add(new Ship("Destroyer", 2, false));

        return ships;
    }

    static void addShips(Scanner scanner, ArrayList<Ship> ships, int[][] battleField) {
        // Add ships to the field
        for (Ship ship : ships) {

            printCMDPrompt(ship.getName(), ship.getCells(), battleField);

            while (!ship.isExist()) {

                String begin = scanner.next();
                String end = scanner.next();

                ship.setExist(addShipToField(begin.toUpperCase(), end.toUpperCase(), ship, battleField));

                if (!ship.isExist()) {
                    System.out.print("> ");
                }
            }
            System.out.println();
        }
    }

    static boolean addShipToField(final String begin, final String end, final Ship ship, int[][] battlefield) {
        boolean cellAdded = false;

        final int numberOffset = '0';
        final int charOffset = '@';

        int x1 = begin.charAt(0) - charOffset;
        int x2 = end.charAt(0) - charOffset;

        if (x1 > x2) {
            int tmp = x2;
            x2 = x1;
            x1 =tmp;
        }

        int y1 = (begin.length() > 2 ? '9' + 1 : begin.charAt(1)) - numberOffset;
        int y2 = (end.length() > 2 ? '9' + 1 : end.charAt(1)) - numberOffset;

        if (y1 > y2) {
            int tmp = y2;
            y2 = y1;
            y1 =tmp;
        }

        if (x1 == x2 && Math.min(y1, y2) > 0) {
            cellAdded = checkLengthAndDistance(y1, y2, x1, true, ship, battlefield);
            if (cellAdded) {
                for (int k = 0; k < ship.getCells(); k++) {
                    ship.saveCoordinates(k, Math.min(y1, y2) + k, x1);
                }
            }
        } else if (y1 == y2 && Math.min(x1, x2) > 0) {
            cellAdded = checkLengthAndDistance(x1, x2, y1, false, ship, battlefield);
            if (cellAdded) {
                for (int k = 0; k < ship.getCells(); k++) {
                    ship.saveCoordinates(k, y1, Math.min(x1, x2) + k);
                }
            }
        } else {
            System.out.println("Error! Wrong ship location! Try again:\n");
        }

        return cellAdded;
    }

    static boolean checkShoot(int[] coordinates, int[][] battleField) {

        boolean hit = false;
        final int x = coordinates[1];
        final int y = coordinates[0];

        if (battleField[x][y] == shipChar || battleField[x][y] == hitChar) {
            hit = true;
            battleField[x][y] = hitChar;
        } else {
            battleField[x][y] = missChar;
        }

        return hit;
    }

    static void printBattleField(boolean showShips, int[][] battleField) {
        for (int i = 0; i < battleField.length; i++) {
            for (int j = 0; j < battleField[i].length; j++) {
                if (i == 0 && j == 0) {
                    System.out.print("  ");
                } else if (j == battleField.length - 1 && i == 0) {
                    System.out.print("10");
                } else {
                    if (!showShips && battleField[i][j] == shipChar) {
                        System.out.print(fog + " ");
                    } else {
                        System.out.print((char) battleField[i][j] + " ");
                    }
                }
            }
            System.out.println();
        }
//        System.out.println();
    }

    static void fillBattleField(int[][] battleField) {
        int charOffset = '@';
        int numberOffset = '/';

        for (int i = 0; i < battleField.length; i++) {
            for (int j = 0; j < battleField[i].length; j++) {
                if (i == 0) {
                    battleField[i][j] = ++numberOffset;
                } else if (j == 0) {
                    battleField[i][j] = ++charOffset;
                } else {
                    battleField[i][j] = fog;
                }
            }
        }
    }

    static void printCMDPrompt(String message, int cells, int[][] battleField) {
        printBattleField(true, battleField);
        System.out.println();
        System.out.println("Enter the coordinates of the " + message + " (" + cells + " cells):\n");
        System.out.print("> ");
    }

    static boolean checkLengthAndDistance(int a, int b, int shipLine, boolean horizontal,
                                          Ship ship, int[][] battleField) {
        int max = Math.max(a, b);
        int min = Math.min(a, b);

        int area11;
        int area12;
        int area21;
        int area22;

        if (horizontal) {
            area11 = shipLine > 1 ? shipLine - 1 : 1;
            area12 = shipLine < 10 ? shipLine + 1 : 10;
            area21 = min > 1 ? min - 1 : 1;
            area22 = max < 10 ? max + 1 : 10;
        } else {
            area11 = min > 1 ? min - 1 : 1;
            area12 = max < 10 ? max + 1 : 10;
            area21 = shipLine > 1 ? shipLine - 1 : 1;
            area22 = shipLine < 10 ? shipLine + 1 : 10;
        }

        if (max - min + 1 == ship.getCells()) {
            for (int i = area11; i <= area12; i++) {
                for (int j = area21; j <= area22; j++) {
                    if (horizontal && (j >= min && j <= max) && shipLine == i) {
                        if (battleField[i][j] == shipChar) {
                            System.out.println("Error! Wrong ship location! Try again:\n");
                            return false;
                        } else {
                            battleField[i][j] = shipChar;
                        }
                    } else if (!horizontal && (i >= min && i <= max) && shipLine == j) {
                        if (battleField[i][j] == shipChar) {
                            System.out.println("Error! Wrong ship location! Try again:\n");
                            return false;
                        }
                    } else {
                        if (battleField[i][j] == shipChar) {
                            System.out.println("Error! You placed it too close to another one. Try again:\n");
                            return false;
                        }
                    }
                }
            }
            for (int i = a; i <= b; i++) {
                if (horizontal) {
                    battleField[shipLine][i] = shipChar;
                } else {
                    battleField[i][shipLine] = shipChar;
                }
            }
            return true;
        } else {
            System.out.println("\nError! Wrong length of the " + ship.getName() + "! Try again:\n");
            return false;
        }
    }

    static int[] checkCoordinates(String coordinates) {

        final int charOffset = '@';

        final int x = Integer.parseInt(coordinates.substring(1));
        final int y = coordinates.charAt(0) - charOffset;

        int[] cd = new int[2];

        if ((y >= 'A' - charOffset && y <= 'J' - charOffset) && (x > 0 && x < 11)) {
            cd[0] = x;
            cd[1] = y;
        }

        return cd;
    }
}

class Ship {

    private String name;
    private int cells;
    private boolean exist;
    private int destroyedCells;
    private int[][] location;

    Ship(String name, int cells, boolean exist) {
        this.name = name;
        this.cells = cells;
        this.exist = exist;
        this.location = new int[cells][2];
    }

    String getName() {
        return this.name;
    }

    int getCells() {
        return this.cells;
    }

    boolean isExist() {
        return this.exist;
    }

    void setCells(int cells) {
        this.cells = cells;
    }

    void setExist(boolean exist) {
        this.exist = exist;
    }

    void setName(String name) {
        this.name = name;
    }

    void setCellDestroyed() {
        this.destroyedCells++;
    }

    int[][] getLocation() {
        return this.location;
    }

    boolean isDestroyed() {
        return this.cells == this.destroyedCells;
    }

    void saveCoordinates(int index, int x, int y) {
        this.location[index][0] = x;
        this.location[index][1] = y;
    }
}