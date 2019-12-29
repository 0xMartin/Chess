package sempai;

import java.util.ArrayList;
import sempai.evaluator.Evaluator;

public class Algorithm {

    private MoveManager moveManager;
    private Evaluator evaluator;

    public Algorithm(MoveManager moveManager, Evaluator evaluator) {
        this.moveManager = moveManager;
        this.evaluator = evaluator;
    }

    public Move findBestTurn(int depth) {
        Move best = null;
        int max = -Main.MAX_VALUE;
        ArrayList<Move> moves = this.moveManager.getPossibleMoves(Main.ISWHITE);
        for (Move m : moves) {
            this.moveManager.moveForward(m);
            int value = minMax(depth - 1, -Main.MAX_VALUE, Main.MAX_VALUE, !Main.ISWHITE);
            this.moveManager.movenBackward();
            if (max < value) {
                max = value;
                best = m;
            }
        }
        return best;
    }

    private int minMax(int depth, int alpha, int beta, boolean isWhite) {
        if (depth == 0) {
            return this.evaluator.getScoreOfBoard();
        }
        ArrayList<Move> moves = this.moveManager.getPossibleMoves(isWhite);
        if (isWhite == Main.ISWHITE) {
            int best = -Main.MAX_VALUE;
            for (Move m : moves) {
                this.moveManager.moveForward(m);
                best = Math.max(best, minMax(depth - 1, alpha, beta, !isWhite));
                this.moveManager.movenBackward();
                alpha = Math.max(alpha, best);
                if (alpha >= beta) {
                    break;
                }
            }
            return best;
        } else {
            int best = Main.MAX_VALUE;
            for (Move m : moves) {
                this.moveManager.moveForward(m);
                best = Math.min(best, minMax(depth - 1, alpha, beta, !isWhite));
                this.moveManager.movenBackward();
                beta = Math.min(beta, best);
                if (alpha >= beta) {
                    break;
                }
            }
            return best;
        }
    }

}
