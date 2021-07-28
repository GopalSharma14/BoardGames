package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

class Game {

    enum Player {
        X, O, EMPTY
    }

    static class Coordinate {
        int x;
        int y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            Coordinate c = (Coordinate) o;
            return c.x == x && c.y == y;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(x * 31 + y).hashCode();
        }
    }

    private Player[][] board;
    int count=0;
    boolean flag= false;
    Player[][] playerData=new Player[3][3];
    Coordinate prevMove=null;
    ArrayList<int[]> forbiddenMoves= new ArrayList<int[]>();
    ArrayList<int[]> forbiddenMovesAll= new ArrayList<int[]>();
    ArrayList<int[]> nextMove= new ArrayList<int[]>();
    boolean forbiddenMoveFlag= false;
    boolean nextMoveFlag= false;

    private BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    public Game() {
        this.board = new Player[9][9];
        for (int x = 0; x < board.length; x++) {
            this.board[x] = new Player[9];
            Arrays.fill(this.board[x], Player.EMPTY);
        }
    }

    private Coordinate getUserMove() {
        try {
            System.out.println("Enter your coordinates: ");
            String text = inputReader.readLine();
            int x = text.charAt(0) - 'a';
            int y = Character.digit(text.charAt(1), 10) - 1;
            return new Coordinate(
                    Math.max(0, Math.min(9 - 1, x)),
                    Math.max(0, Math.min(9 - 1, y))
            );
        } catch (Exception e) {
            return getUserMove();
        }
    }

    public int[] getMapValues(int x, int y) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(0, 0);
        map.put(3, 1);
        map.put(6, 2);
        return new int[]{map.get(x), map.get(y)};
    }

    public ArrayList<int[]> getNextMoveValues(int x, int y) {
        ArrayList<int[]> list = new ArrayList<int[]>();
        int[] col = new int[]{x * 3, x * 3 + 1, x * 3 + 2};
        int[] row = new int[]{y * 3, y * 3 + 1, y * 3 + 2};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(new int[]{col[i], row[j]});
            }
        }
        return list;
    }

    public ArrayList<int[]> getForbiddenValues(int x, int y) {
        ArrayList<int[]> list = new ArrayList<int[]>();
        int[] col = new int[]{x, x + 1, x + 2};
        int[] row = new int[]{y, y + 1, y + 2};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(new int[]{col[i], row[j]});
            }
        }
        return list;
    }

    private void render() {
        System.out.println("  a b c   d e f   g h i");
        for (int outerY = 0; outerY < 3; outerY++) {
            for (int innerY = 0; innerY < 3; innerY++) {
                StringBuilder line = new StringBuilder();
                line.append(outerY * 3 + innerY + 1);
                line.append(' ');
                for (int outerX = 0; outerX < 3; outerX++) {
                    for (int innerX = 0; innerX < 3; innerX++) {
                        Player p = board[outerX * 3 + innerX][outerY * 3 + innerY];
                        if (p == Player.X) line.append('X');
                        else if (p == Player.O) line.append('O');
                        else if (innerY < 2) line.append('_');
                        else line.append(' ');

                        if (innerX < 2) line.append('|');
                    }
                    if (outerX < 2) line.append(" | ");
                }
                System.out.println(line.toString());
            }
            if (outerY < 2) System.out.println("  ------|-------|------");
        }
        System.out.println("\n\n");
    }
//    public void start(){
//        System.out.println("Choose your marker Player 1: ");
//        Player p1 = null;
//        try {
//            p1 = Player.valueOf(inputReader.readLine());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Player p2 = p1 == Player.X ? Player.O : Player.X;
//        System.out.println("Player 2 is: " +p2);
//
//        System.out.println("Deciding who will go first . . ");
////        try {
////            TimeUnit.SECONDS.sleep(5);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        Random rand = new Random();
//        int r = rand.nextInt(2);
//        Player firstPlayer = r==0? p1:p2;
//        System.out.println(firstPlayer+ " will go first");
////        try {
////            TimeUnit.SECONDS.sleep(1);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        play(firstPlayer);
//    }
public boolean winCondition(Player p, int x, int y) {
    if (board[x][y] == board[x + 1][y] && board[x + 1][y] == board[x + 2][y] && board[x + 2][y] == p ||
            board[x][y + 1] == board[x + 1][y + 1] && board[x + 1][y + 1] == board[x + 2][y + 1] && board[x + 2][y + 1] == p ||
            board[x][y + 2] == board[x + 1][y + 2] && board[x + 1][y + 2] == board[x + 2][y + 2] && board[x + 2][y + 2] == p ||
            board[x][y] == board[x][y + 1] && board[x][y + 1] == board[x][y + 2] && board[x][y + 2] == p ||
            board[x + 1][y] == board[x + 1][y + 1] && board[x + 1][y + 1] == board[x + 1][y + 2] && board[x + 1][y + 2] == p ||
            board[x + 2][y] == board[x + 2][y + 1] && board[x + 2][y + 1] == board[x + 2][y + 2] && board[x + 2][y + 2] == p ||
            board[x][y] == board[x + 1][y + 1] && board[x][y] == board[x + 2][y + 2] && board[x + 2][y + 2] == p ||
            board[x][y + 2] == board[x + 1][y + 1] && board[x + 1][y + 1] == board[x + 2][y] && board[x + 2][y] == p
    )
        return true;
    return false;
}

    public boolean matchWinCondition(Player p, Player[][] playerData) {
        if (playerData[0][0] == playerData[0][1] && playerData[0][0] == playerData[0][2] && playerData[0][0] == p ||
                playerData[1][0] == playerData[1][1] && playerData[1][0] == playerData[1][2] && playerData[1][0] == p ||
                playerData[2][0] == playerData[2][1] && playerData[2][0] == playerData[2][2] && playerData[2][0] == p ||
                playerData[0][0] == playerData[1][0] && playerData[1][0] == playerData[2][0] && playerData[0][0] == p ||
                playerData[0][1] == playerData[1][1] && playerData[0][1] == playerData[2][1] && playerData[0][1] == p ||
                playerData[0][2] == playerData[1][2] && playerData[0][2] == playerData[2][2] && playerData[0][2] == p ||
                playerData[0][0] == playerData[1][1] && playerData[0][0] == playerData[2][2] && playerData[0][0] == p ||
                playerData[2][0] == playerData[1][1] && playerData[2][0] == playerData[0][2] && playerData[2][0] == p)
            return true;
        return false;

    }

    /*
     * Things to remember:
     * 1. Take turns
     * 2. Valid moves (send opponent to correct local board)
     * 3. Detect when sub-section is won/full (don't allow for further play in section)
     * 4. Detect when global game is won/tied (declare winner and terminate)
     */
    public void play() {
        // TODO - implement game logic
        while (true) {
            render();
            Player currPlayer = flag == false ? Player.X : Player.O;
            Coordinate coordinate = getUserMove();

            if (prevMove != null) {
                nextMove = getNextMoveValues(prevMove.x % 3, prevMove.y % 3);
            } else {
                board[coordinate.x][coordinate.y] = currPlayer;
                prevMove = coordinate;
                flag = !flag;
                continue;
            }

            if (!forbiddenMovesAll.isEmpty()) {
                for (int[] item : forbiddenMovesAll) {
                    if (item[0] == coordinate.x && item[1] == coordinate.y) {
                        forbiddenMoveFlag = true;
                        break;
                    }
                }
            }

            for (int[] item : nextMove) {
                if (item[0] == coordinate.x && item[1] == coordinate.y) {
                    nextMoveFlag = true;
                    break;
                }
            }

            if (nextMoveFlag && forbiddenMoveFlag) {
                System.out.println("The box has been already won. Choose any other coordinates!!");
                forbiddenMoveFlag = !forbiddenMoveFlag;
                continue;
            } else if (nextMoveFlag) {
                if (board[coordinate.x][coordinate.y] != Player.EMPTY) {
                    System.out.println("Choose another position. Already filled up!");
                    continue;
                } else {
                    board[coordinate.x][coordinate.y] = currPlayer;
                    prevMove = coordinate;
                    nextMoveFlag = false;
                    flag = !flag;
                }
            } else {
                System.out.println("Invalid move!!! Please try again !!!");
                continue;
            }

            int newX = (coordinate.x - (coordinate.x % 3));
            int newY = (coordinate.y - (coordinate.y % 3));

            if (winCondition(currPlayer, newX, newY)) {
                int[] getValues = getMapValues(newX, newY);
                playerData[getValues[1]][getValues[0]] = currPlayer;
                forbiddenMoves = getForbiddenValues(newX, newY);
            }
            forbiddenMovesAll.addAll(forbiddenMoves);
            if (matchWinCondition(currPlayer, playerData)) {
                System.out.println(currPlayer + " wins the match. Congratulations player " + currPlayer);
                break;
            }
        }
        exit(1);
    }
}