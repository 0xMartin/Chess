/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Krčma
 */
package sempai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Marti
 */
public class MoveManager {

    //board
    public final Figure[][] board;

    //last moves
    private LinkedList<Move> moves;
    private LinkedList<Figure> killedFigures;

    //kings positions
    private Point wk, bk;

    public MoveManager(Figure[][] board) {
        this.board = board;
        this.moves = new LinkedList<>();
        this.killedFigures = new LinkedList<>();
    }

    public void init() {
        //find kings
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Figure f = this.board[y][x];
                if (f != null) {
                    if (f.getType() == Figure.KING) {
                        if (f.isWhite()) {
                            this.wk = new Point(x, y);
                        } else {
                            this.bk = new Point(x, y);
                        }
                    }
                }
            }
        }
    }

    /**
     * Make move
     *
     * @param move Move(from -> to)
     */
    public void moveForward(Move move) {

        //this move add to moves (for turn back and getting possible turns)
        this.moves.add(move);

        Point from = move.getFrom();
        Point to = move.getTo();

        //from new position get killed figure
        this.killedFigures.add(this.board[to.y][to.x]);

        //make move
        this.board[to.y][to.x] = this.board[from.y][from.x];
        this.board[from.y][from.x] = null;
        
        //make figure change
        if (move.getPawnChange() != Figure.NONE) {
            Figure f = this.board[to.y][to.x];
            if (f.getType() == Figure.PAWN) {
                f.setType(move.getPawnChange());
            }
        }

        //relocate king point
        Figure king = this.board[to.y][to.x];
        if (king.getType() == Figure.KING) {
            if (king.isWhite()) {
                this.wk = to;
            } else {
                this.bk = to;
            }
        }

    }

    /**
     * Last move get back
     */
    public void movenBackward() {

        Point from = this.moves.getLast().getFrom();
        Point to = this.moves.getLast().getTo();

        //make figure change back
        if (this.moves.getLast().getPawnChange() != Figure.NONE) {
            Figure f = this.board[to.y][to.x];
            if (f.getType() == Figure.PAWN) {
                f.setType(this.moves.getLast().getPawnChange());
            }
        }

        //make move back
        this.board[from.y][from.x] = this.board[to.y][to.x];

        //return back killed figure
        this.board[to.y][to.x] = this.killedFigures.getLast();
        this.killedFigures.removeLast();

        //remove this move because now its moved back
        this.moves.removeLast();

        //relocate king point
        Figure king = this.board[from.y][from.x];
        if (king.getType() == Figure.KING) {
            if (king.isWhite()) {
                this.wk = from;
            } else {
                this.bk = from;
            }
        }

    }

    /**
     * Get all possible moves
     *
     * @param isWhite
     * @return moves
     */
    public ArrayList<Move> getPossibleMoves(boolean isWhite) {

        ArrayList<Move> possibleMoves = new ArrayList<>();

        //generate turns
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Figure f = this.board[y][x];
                if (f != null) {
                    if (f.isWhite() == isWhite) {
                        switch (f.getType()) {
                            case Figure.QUEEN:
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dy = -1; dy <= 1; dy++) {
                                        if (dx != 0 || dy != 0) {
                                            movesInLine(possibleMoves, isWhite, x, y, dx, dy);
                                        }
                                    }
                                }
                                break;
                            case Figure.BISHOP:
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dy = -1; dy <= 1; dy++) {
                                        if (Math.abs(dx) + Math.abs(dy) == 2) {
                                            movesInLine(possibleMoves, isWhite, x, y, dx, dy);
                                        }
                                    }
                                }
                                break;
                            case Figure.ROOK:
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dy = -1; dy <= 1; dy++) {
                                        if (Math.abs(dx - dy) == 1) {
                                            movesInLine(possibleMoves, isWhite, x, y, dx, dy);
                                        }
                                    }
                                }
                                break;
                            case Figure.KNIGHT:
                                for (int k = 0; k <= 1; k++) {
                                    for (int dx = -1; dx <= 1; dx += 2) {
                                        for (int dy = -1; dy <= 1; dy += 2) {
                                            if (isInBoard(x + dx * (2 - k), y + dy * (k + 1))) {
                                                Figure e = this.board[y + dy * (k + 1)][x + dx * (2 - k)];
                                                if (e != null) {
                                                    if (e.isWhite() != isWhite && e.getType() != Figure.KING) {
                                                        ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x + dx * (2 - k), y + dy * (k + 1))), isWhite);
                                                    }
                                                } else {
                                                    ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x + dx * (2 - k), y + dy * (k + 1))), isWhite);
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
                                if (isInBoard(x, y + yoff)) {
                                    Figure e = this.board[y + yoff][x];
                                    if (e == null) {
                                        if (y + yoff == 0 || y + yoff == 7) {
                                            //pawn change
                                            ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff), Figure.QUEEN), isWhite);
                                            ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff), Figure.ROOK), isWhite);
                                            ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff), Figure.BISHOP), isWhite);
                                            ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff), Figure.KNIGHT), isWhite);
                                        } else {
                                            //norma move
                                            ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff)), isWhite);
                                        }
                                    }
                                }
                                //first move
                                if ((y == 6 && isWhite) || (y == 1 && !isWhite)) {
                                    if (isInBoard(x, y + yoff * 2)) {
                                        Figure e2 = this.board[y + yoff * 2][x];
                                        Figure e1 = this.board[y + yoff][x];
                                        if (e2 == null && e1 == null) {
                                            //zamezení braní mimochodem
                                            boolean b = true;
                                            for (int j = -1; j <= 1; j++) {
                                                if (isInBoard(x + j, y + yoff * 2)) {
                                                    Figure fi = this.board[y + yoff * 2][x + j];
                                                    if (fi != null) {
                                                        if (fi.isWhite() != isWhite) {
                                                            if (fi.getType() == Figure.PAWN) {
                                                                b = false;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (b) {
                                                ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x, y + yoff * 2)), isWhite);
                                            }
                                        }
                                    }
                                }
                                //attack
                                for (int i = -1; i <= 2; i += 2) {
                                    if (isInBoard(x + i, y + yoff)) {
                                        Figure e = this.board[y + yoff][x + i];
                                        if (e != null) {
                                            if (e.isWhite() != isWhite && e.getType() != Figure.KING) {
                                                ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x + i, y + yoff)), isWhite);
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
                                                Figure e = this.board[y + dy][x + dx];
                                                if (e != null) {
                                                    if (e.isWhite() != isWhite && e.getType() != Figure.KING) {
                                                        ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x + dx, y + dy)), isWhite);
                                                    }
                                                } else {
                                                    ADDMove(possibleMoves, new Move(new Point(x, y), new Point(x + dx, y + dy)), isWhite);

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
        }

        return possibleMoves;

    }

    /**
     * Add move to array and test king
     *
     * @param moves
     * @param move
     * @param isWhite
     */
    private void ADDMove(ArrayList<Move> moves, Move move, boolean isWhite) {
        moveForward(move);
        boolean isKingProtected = isKingProtected(isWhite);
        movenBackward();
        if (isKingProtected) {
            moves.add(move);
        }
    }

    /**
     * Find and add moves to array
     *
     * @param moves Array
     * @param isWhite Color of figure in position [x,y]
     * @param x X position
     * @param y Y position
     * @param dx
     * @param dy
     */
    private void movesInLine(ArrayList<Move> moves, boolean isWhite, final int x, final int y, int dx, int dy) {
        int x2 = x;
        int y2 = y;
        while (true) {
            x2 += dx;
            y2 += dy;
            if (isInBoard(x2, y2)) {
                Figure e = this.board[y2][x2];
                if (e != null) {
                    if (e.isWhite() != isWhite && e.getType() != Figure.KING) {
                        ADDMove(moves, new Move(new Point(x, y), new Point(x2, y2)), isWhite);
                    }
                    return;
                } else {
                    ADDMove(moves, new Move(new Point(x, y), new Point(x2, y2)), isWhite);
                }
            } else {
                return;
            }
        }
    }

    private boolean isInBoard(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    /**
     * Get figures that attacking king
     *
     * @param isWhite Color of king
     * @param linean True -> linear (queen, bishop, rook) nonlinear
     * (pawn,knight)
     * @return
     */
    public boolean isKingProtected(boolean isWhite) {
        Point king = isWhite ? this.wk : this.bk;

        //linear
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    if (isAttackerInLine(isWhite, king.x, king.y, dx, dy)) {
                        return false;
                    }
                }
            }
        }

        //nonlinear
        //kingh
        for (int k = 0; k <= 1; k++) {
            for (int dx = -1; dx <= 1; dx += 2) {
                for (int dy = -1; dy <= 1; dy += 2) {
                    if (isInBoard(king.x + dx * (2 - k), king.y + dy * (k + 1))) {
                        Figure e = this.board[king.y + dy * (k + 1)][king.x + dx * (2 - k)];
                        if (e != null) {
                            if (e.isWhite() != isWhite && e.getType() == Figure.KNIGHT) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        //pawn
        int dy = isWhite ? 1 : -1;
        for (int j = -1; j <= 1; j += 2) {
            //bílý
            if (isInBoard(king.x + j, king.y + dy)) {
                Figure e = this.board[king.y + dy][king.x + j];
                if (e != null) {
                    if (e.getType() == Figure.PAWN) {
                        if (e.isWhite() != isWhite && e.isWhite()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Find attacker position in line
     *
     * @param moves Array
     * @param isWhite Color of figure in position [x,y]
     * @param x X position
     * @param y Y position
     * @param dx
     * @param dy
     */
    private boolean isAttackerInLine(boolean isWhite, final int x, final int y, int dx, int dy) {
        int x2 = x;
        int y2 = y;
        while (true) {
            x2 += dx;
            y2 += dy;
            if (isInBoard(x2, y2)) {
                Figure e = this.board[y2][x2];
                if (e != null) {
                    if (e.isWhite() != isWhite) {
                        switch (e.getType()) {
                            case Figure.QUEEN:
                                return true;
                            case Figure.ROOK:
                                if (Math.abs(dx - dy) == 1) {
                                    return true;
                                }
                                break;
                            case Figure.BISHOP:
                                if (Math.abs(dx) + Math.abs(dy) == 2) {
                                    return true;
                                }
                                break;
                            default:
                                break;
                        }

                    }
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
