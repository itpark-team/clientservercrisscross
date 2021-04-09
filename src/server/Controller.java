package server;

public class Controller {
    private int fieldSize;
    private char[][] field;

    public final char CRISS = 'O';
    public final char CROSS = 'X';
    public final char EMPTY = '.';

    public final String WIN_CROSS = "WinCross";
    public final String WIN_CRISS = "WinCriss";
    public final String DRAW = "Draw";
    public final String CONTINUE_GAME = "Continue";

    public Controller() {
        fieldSize = 3;
        field = new char[fieldSize][fieldSize];
    }

    public void ClearField() {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                field[i][j] = EMPTY;
            }
        }

        //field[0][0]=CROSS;
    }

    public String GetFieldInString() {
        String output = "";

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                output += field[i][j];
            }
            output += "\n";
        }
        return output;
    }

    public boolean SetSign(int i, int j, char sign) {

        if (i < 0 || i > fieldSize - 1 || j < 0 || j > fieldSize - 1) {
            return false;
        }

        if (field[i][j] != EMPTY) {
            return false;
        }

        field[i][j] = sign;
        return true;
    }

    public String GetGameResult() {
        if (
            field[0][0] == CROSS && field[0][1] == CROSS && field[0][2] == CROSS
            ||
            field[1][0] == CROSS && field[1][1] == CROSS && field[1][2] == CROSS
            ||
            field[2][0] == CROSS && field[2][1] == CROSS && field[2][2] == CROSS
            ||

            field[0][0] == CROSS && field[1][0] == CROSS && field[2][0] == CROSS
            ||
            field[0][1] == CROSS && field[1][1] == CROSS && field[2][1] == CROSS
            ||
            field[0][2] == CROSS && field[1][2] == CROSS && field[2][2] == CROSS
            ||

            field[0][0] == CROSS && field[1][1] == CROSS && field[2][2] == CROSS
            ||
            field[0][2] == CROSS && field[1][1] == CROSS && field[2][0] == CROSS
        ) {
            return WIN_CROSS;
        }

        if (
            field[0][0] == CRISS && field[0][1] == CRISS && field[0][2] == CRISS
            ||
            field[1][0] == CRISS && field[1][1] == CRISS && field[1][2] == CRISS
            ||
            field[2][0] == CRISS && field[2][1] == CRISS && field[2][2] == CRISS
            ||

            field[0][0] == CRISS && field[1][0] == CRISS && field[2][0] == CRISS
            ||
            field[0][1] == CRISS && field[1][1] == CRISS && field[2][1] == CRISS
            ||
            field[0][2] == CRISS && field[1][2] == CRISS && field[2][2] == CRISS
            ||

            field[0][0] == CRISS && field[1][1] == CRISS && field[2][2] == CRISS
            ||
            field[0][2] == CRISS && field[1][1] == CRISS && field[2][0] == CRISS

        ) {
            return WIN_CRISS;
        }


        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if(field[i][j]==EMPTY){
                    return CONTINUE_GAME;
                }
            }
        }

        return DRAW;
    }


}
