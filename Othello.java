package com.company;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import static java.lang.System.exit;

class Othello {
    enum COLOR { NONE, WHITE, BLACK };
    boolean flag = true;
    public COLOR[][] spaces = new COLOR[8][8];
    boolean w,nw,n,ne,e,se,s,sw;


    public Othello() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                spaces[x][y] = COLOR.NONE;
            }
        }

        spaces[3][4] = COLOR.WHITE;
        spaces[4][3] = COLOR.WHITE;
        spaces[3][3] = COLOR.BLACK;
        spaces[4][4] = COLOR.BLACK;

    }

    private String renderSpace(int x, int y) {

        switch(spaces[x][y]) {
            case BLACK: return "B";
            case WHITE: return "W";
        }
        return " ";
    }

    public void render() {
        System.out.print("  a b c d e f g h\n");
        for(int y = 0; y < 8; y++) {
            System.out.printf("%d %s %s %s %s %s %s %s %s\n",
                    y+1,
                    renderSpace(0,y),
                    renderSpace(1,y),
                    renderSpace(2,y),
                    renderSpace(3,y),
                    renderSpace(4,y),
                    renderSpace(5,y),
                    renderSpace(6,y),
                    renderSpace(7,y)
            );
        }
    }

    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int _x, int _y) {
            x = _x;
            y = _y;
        }
    }

    private Coordinate getMoveInput() {
        System.out.println("Enter your coordinates: ");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String text = in.readLine();
            return new Coordinate(
                    Math.max(0, Math.min(7, text.charAt(0) - 'a')),
                    Math.max(0, Math.min(7, Character.digit(text.charAt(1), 10)-1))
            );
        }
        catch(Exception e) {
            return getMoveInput();
        }
    }
    public boolean flipLine(COLOR player, int dx, int dy, int x, int y, COLOR[][] spaces){
        if((x+dx<0) || (x+dx>7))
            return false;
        if((y+dy<0) || (y+dy>7))
            return false;
        if(spaces[y+dy][x+dx]==COLOR.NONE)
            return false;
        if(spaces[y+dy][x+dx]==player)
            return true;
        else {
            if (flipLine(player, dx, dy, x + dx, y + dy, spaces)) {
                spaces[y+dy][x + dx] = player;
                return true;
            } else {
                return false;
            }
        }
    }

    public void flipSpaces(COLOR player, int x, int y, COLOR[][] spaces){
        flipLine(player,-1,-1,x,y,spaces);
        flipLine(player,-1,0,x,y,spaces);
        flipLine(player,-1,1,x,y,spaces);
        flipLine(player,0,-1,x,y,spaces);
        flipLine(player,0,1,x,y,spaces);
        flipLine(player,1,-1,x,y,spaces);
        flipLine(player,1,0,x,y,spaces);
        flipLine(player,1,1,x,y,spaces);
    }
    public boolean check_line(COLOR player, int dx, int dy, int x, int y, COLOR[][] spaces){
        if(spaces[y][x]==player)
            return true;
        if((x+dx<0) || (x+dx>7))
            return false;
        if((y+dy<0) || (y+dy>7))
            return false;
        if(spaces[y][x]==COLOR.NONE)
            return false;
        return check_line(player,dx,dy,x+dx,y+dy,spaces);
    }

    public boolean isValid(COLOR player, int dx, int dy,int x, int y, COLOR[][] spaces){
        COLOR player2 = player==COLOR.BLACK?COLOR.WHITE: COLOR.BLACK;
        if((x+dx<0) || (x+dx>7))
            return false;
        if((y+dy<0) || (y+dy>7))
            return false;
        if(spaces[y+dy][x+dx]!=player2)
            return false;
        if((x+dx+dx<0) || (x+dx+dx>7))
            return false;
        if((y+dy+dy<0) || (y+dy+dy>7))
            return false;

        return check_line(player, dx,dy,x+dx+dx,y+dy+dy,spaces);
    }

    public boolean validMove(COLOR player,int x, int y,COLOR[][] spaces){
                    if(spaces[y][x]==COLOR.NONE){
                        nw= isValid(player,-1,-1,x,y,spaces);
                        n= isValid(player,-1,0,x,y,spaces);
                        ne= isValid(player,-1,1,x,y,spaces);
                        w= isValid(player,0,-1,x,y,spaces);
                        e= isValid(player,0,1,x,y,spaces);
                        sw= isValid(player,1,-1,x,y,spaces);
                        s= isValid(player,1,0,x,y,spaces);
                        se= isValid(player,1,1,x,y,spaces);
                        if(nw || n|| ne|| w|| e|| sw||s|| se)
                            return true;
                    }
            return false;
    }

    public void play() {
        while(true) {
            render();
            int black=0;
            int white=0;
            int count=0;
            Coordinate c = getMoveInput();

            System.out.println(c.x+ " "+c.y);
            //TODO: Fill in this function with the actual game logic.
            // switch between players
            // check if a move is valid you can capture opposite color
            // handle the capturing
            // one side has no valid move if should change to the other player automatically
            // check for the winner

            COLOR player = flag==false? COLOR.BLACK: COLOR.WHITE;
            if (validMove(player, c.y, c.x, spaces)) {
                spaces[c.x][c.y] = player;
                flipSpaces(player, c.y, c.x, spaces);
                for (int i = 0; i < spaces.length; i++) {
                    for (int j = 0; j < spaces[i].length; j++) {
                        if (spaces[i][j] == COLOR.BLACK) {
                            count++;
                            black += 1;
                        } else if (spaces[i][j] == COLOR.WHITE) {
                            white += 1;
                            count++;
                        }

                    }
                }
                if (count == 64) {
                    if (black > white)
                        System.out.println("Black wins!!!");
                    else if (white > black)
                        System.out.println("White wins!!");
                    else {
                        System.out.println("Draw!!!");
                    }
                    break;
                }

                System.out.println("Black are: " + black + " White are: " + white);
                System.out.println("Count is: " + count);
                flag = !flag;
            } else {
                System.out.println("Invalid move!!! Try again!!");
            }

        }
        exit(1);
    }
}