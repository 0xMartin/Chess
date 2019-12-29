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
public class MaterialScore {

    private final int PAWN_SCORE = 100;
    private final int KNIGHT_SCORE = 300;
    private final int BISHOP_SCORE = 350;
    private final int ROOK_SCORE = 525;
    private final int QUEEN_SCORE = 1000;
    private final int KING_SCORE = Main.MAX_VALUE;

    private final Figure[][] BOARD;

    public MaterialScore(Figure[][] board) {
        this.BOARD = board;
    }

    public int getScore() {
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Figure fig = this.BOARD[y][x];
                if (fig != null) {
                    boolean color = fig.isWhite() == Main.ISWHITE;
                    int ny = fig.isWhite() ? y : 7 - y;
                    switch (fig.getType()) {
                        case Figure.PAWN:
                            score += color
                                    ? this.PAWN_SCORE + this.pawn_w_pe[ny][x]
                                    : -this.PAWN_SCORE - this.pawn_w_pe[ny][x];
                            break;
                        case Figure.KNIGHT:
                            score += color
                                    ? this.KNIGHT_SCORE + this.knight_pe[y][x]
                                    : -this.KNIGHT_SCORE - this.knight_pe[y][x];
                            break;
                        case Figure.BISHOP:
                            score += color
                                    ? this.BISHOP_SCORE + this.bishop_w_pe[ny][x]
                                    : -this.BISHOP_SCORE - this.bishop_w_pe[ny][x];
                            break;
                        case Figure.ROOK:
                            score += color
                                    ? this.ROOK_SCORE + this.rook_w_pe[ny][x]
                                    : -this.ROOK_SCORE - this.rook_w_pe[ny][x];
                            break;
                        case Figure.QUEEN:
                            score += color
                                    ? this.QUEEN_SCORE + this.queen_pe[y][x]
                                    : -this.QUEEN_SCORE - this.queen_pe[y][x];
                            break;
                        case Figure.KING:
                            score += color
                                    ? this.king_w_pe[ny][x]
                                    : -this.king_w_pe[ny][x];
                            break;
                    }
                }
            }
        }
        return score;
    }

    private final int[][] king_w_pe = new int[][]{
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-4, -6, -6, -8, -8, -6, -6, -4},
        {-2, -4, -4, -4, -4, -4, -4, -2},
        {4, 4, 0, 0, 0, 0, 4, 4},
        {4, 6, 2, 0, 0, 2, 6, 4}
    };

    private final int[][] queen_pe = new int[][]{
        {-4, -2, -2, -1, -1, -2, -2, -4},
        {-2, 0, 0, 0, 0, 0, 0, -2},
        {-2, 0, 1, 1, 1, 1, 0, -2},
        {-1, 0, 1, 1, 1, 1, 0, -1},
        {-1, 0, 1, 1, 1, 1, 0, -1},
        {-2, 0, 1, 1, 1, 1, 0, -2},
        {-2, 0, 0, 0, 0, 0, 0, -2},
        {-4, -2, -2, -1, -1, -2, -2, -4},};

    private final int[][] rook_w_pe = new int[][]{
        {0, 0, 0, 0, 0, 0, 0, 0},
        {1, 2, 2, 2, 2, 2, 2, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {0, 0, 0, 1, 1, 0, 0, 0}
    };

    private final int[][] knight_pe = new int[][]{
        {-10, -8, -6, -6, -6, -6, -8, -10},
        {-8, -4, 0, 0, 0, 0, -4, -8},
        {-6, 0, 2, 3, 3, 2, 0, -6},
        {-6, 0, 3, 4, 4, 3, 0, -6},
        {-6, 0, 3, 4, 4, 3, 0, -6},
        {-6, 0, 2, 3, 3, 2, 0, -6},
        {-8, -4, 0, 0, 0, 0, -4, -8},
        {-10, -8, -6, -6, -6, -6, -8, -10}
    };

    private final int[][] bishop_w_pe = new int[][]{
        {-4, -2, -2, -2, -2, -2, -2, -4},
        {-2, 0, 0, 0, 0, 0, 0, -2},
        {-2, 0, 1, 2, 2, 1, 0, -2},
        {-2, 1, 1, 1, 1, 1, 1, -2},
        {-2, 0, 2, 2, 2, 2, 0, -2},
        {-2, 2, 2, 2, 2, 2, 2, -2},
        {-2, 10, 0, 0, 0, 0, 10, -2},
        {-4, -2, -2, -2, -2, -2, -2, -4}
    };

    private final int[][] pawn_w_pe = new int[][]{
        {0, 0, 0, 0, 0, 0, 0, 0},
        {10, 10, 10, 10, 10, 10, 10, 10},
        {2, 2, 4, 6, 6, 4, 2, 2},
        {1, 1, 2, 5, 5, 2, 1, 1},
        {0, 0, 0, 4, 4, 0, 0, 0},
        {1, -1, -2, 0, 0, -2, -1, 2},
        {1, 2, 2, -4, -4, 2, 2, 2},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };

}
