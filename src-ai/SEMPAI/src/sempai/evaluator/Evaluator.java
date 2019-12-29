/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Krčma
 */
package sempai.evaluator;

import sempai.Figure;

/**
 *
 * @author Marti
 */
public class Evaluator {

    private MaterialScore materialScore;

    private AttackScore attackScore;

    private DefendScore defendScore;

    private PawnStructures pawnStructures;

    public Evaluator(Figure[][] board) {
        this.materialScore = new MaterialScore(board);
        this.attackScore = new AttackScore(board);
        this.defendScore = new DefendScore(board);
        this.pawnStructures = new PawnStructures(board);
    }

    public int getScoreOfBoard() {
        int score = 0;
        score = this.materialScore.getScore();
        //score += this.attackScore.getScore();
        //score += this.defendScore.getScore();
        //score += this.pawnStructures.getScore();
        return score;
    }

    public static int DAValueFigure(int type) {
        switch (type) {
            case Figure.BISHOP:
                return 3;
            case Figure.KNIGHT:
                return 3;
            case Figure.PAWN:
                return 1;
            case Figure.QUEEN:
                return 9;
            case Figure.ROOK:
                return 5;
            default:
                return 0;
        }
    }

}
