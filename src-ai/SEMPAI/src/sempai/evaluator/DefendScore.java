/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Kr�ma
 */
package sempai.evaluator;

import sempai.Figure;
import sempai.Main;

/**
 *
 * @author Marti
 */
public class DefendScore {

    private final Figure[][] BOARD;

    public DefendScore(Figure[][] board) {
        this.BOARD = board;
    }

    public int getScore() {
        int value = 0;
        //generate turns
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Figure f = this.BOARD[y][x];
                if (f != null) {
                    switch (f.getType()) {
                        case Figure.QUEEN:
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (dx != 0 || dy != 0) {
                                        value += f.isWhite() == Main.ISWHITE ? movesInLine(f.isWhite(), x, y, dx, dy) : -movesInLine(f.isWhite(), x, y, dx, dy);
                                    }
                                }
                            }
                            break;
                        case Figure.BISHOP:
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (Math.abs(dx) + Math.abs(dy) == 2) {
                                        value += f.isWhite() == Main.ISWHITE ? movesInLine(f.isWhite(), x, y, dx, dy) : -movesInLine(f.isWhite(), x, y, dx, dy);
                                    }
                                }
                            }
                            break;
                        case Figure.ROOK:
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (Math.abs(dx - dy) == 1) {
                                        value += f.isWhite() == Main.ISWHITE ? movesInLine(f.isWhite(), x, y, dx, dy) : -movesInLine(f.isWhite(), x, y, dx, dy);
                                    }
                                }
                            }
                            break;
                        case Figure.KNIGHT:
                            for (int k = 0; k <= 1; k++) {
                                for (int dx = -1; dx <= 1; dx += 2) {
                                    for (int dy = -1; dy <= 1; dy += 2) {
                                        if (isInBoard(x + dx * (2 - k), y + dy * (k + 1))) {
                                            Figure e = this.BOARD[y + dy * (k + 1)][x + dx * (2 - k)];
                                            if (e != null) {
                                                if (e.isWhite() == f.isWhite()) {
                                                    int u = Evaluator.DAValueFigure(e.getType());
                                                    value += f.isWhite() == Main.ISWHITE ? u : -u;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case Figure.PAWN:
                            int yoff = 1;
                            if (f.isWhite()) {
                                yoff = -1;
                            }
                            //attack
                            for (int i = -1; i <= 2; i += 2) {
                                if (isInBoard(x + i, y + yoff)) {
                                    Figure e = this.BOARD[y + yoff][x + i];
                                    if (e != null) {
                                        if (e.isWhite() == f.isWhite()) {
                                            int u = Evaluator.DAValueFigure(e.getType());
                                            value += f.isWhite() == Main.ISWHITE ? u : -u;
                                        }
                                    }
                                }
                            }
                            break;
                        case Figure.KING:
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (dx != 0 || dy != 0) {
                                        if (isInBoard(x + dx, y + dy)) {
                                            Figure e = this.BOARD[y + dy][x + dx];
                                            if (e != null) {
                                                if (e.isWhite() == f.isWhite()) {
                                                    int u = Evaluator.DAValueFigure(e.getType());
                                                    value += f.isWhite() == Main.ISWHITE ? u : -u;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }

        return value;
    }

    private int movesInLine(boolean isWhite, final int x, final int y, int dx, int dy) {
        int x2 = x;
        int y2 = y;
        while (true) {
            x2 += dx;
            y2 += dy;
            if (isInBoard(x2, y2)) {
                Figure e = this.BOARD[y2][x2];
                if (e != null) {
                    if (e.isWhite() == isWhite) {
                        return Evaluator.DAValueFigure(e.getType());
                    }
                    return 0;
                }
            } else {
                return 0;
            }
        }
    }

    private boolean isInBoard(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

}
