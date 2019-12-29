/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Krčma
 */
package sempai;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import sempai.evaluator.DefendScore;
import sempai.evaluator.Evaluator;

/**
 *
 * @author Marti
 */
public class Main {

    //#################################################
    public static String NAME = "SempAI";
    public static String AUTHOR = "Martin Krčma";
    public static String VERSION = "1.1";
    public static String INFO = "<h1><strong><span style=\"color: #00ccff;\">Semp</span><span style=\"color: #0000ff;\">AI</span></strong></h1><p><strong><span style=\"color: #0000ff;\"><span style=\"color: #3366ff;\">&nbsp;</span></span></strong><span style=\"color: #0000ff;\"><span style=\"color: #3366ff;\">Chess java AI</span></span></p><p>&nbsp;<span style=\"color: #3366ff;\">Created by <strong>Martin Krčma</strong></span></p><p><span style=\"color: #800080;\"> Last update: 27.4.2018</span></p>"
            + "<p><span style=\"color: #800080;\"> Version: 1.0</span></p>"
            + "<p><span style=\"color: #800080;\"> Copiright (c) 2018 Martin Krčma </span></p>";
    //#################################################

    public static final int MAX_VALUE = 20000;

    /**
     * @ISWHITE color of this AI
     */
    public static boolean ISWHITE;

    //only for read, writing only with move manager
    public final Figure[][] BOARD;

    //move manager for: moving, getting possible moves ...
    private MoveManager moveManager;

    //evaluate game
    private Evaluator evaluator;

    private Algorithm algorithm;

    public Main(boolean isWhite) {
        Main.ISWHITE = isWhite;
        this.BOARD = new Figure[8][8];
        this.moveManager = new MoveManager(this.BOARD);
        this.evaluator = new Evaluator(this.BOARD);
        this.algorithm = new Algorithm(this.moveManager, this.evaluator);
    }

    /**
     * Build board from string
     *
     * @param board board(String)
     */
    private void buildBoard(String board) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                char c = board.charAt(x + y * 8);
                int type = Figure.NONE;
                boolean isWhite = Character.toLowerCase(c) == c;
                switch (Character.toLowerCase(c)) {
                    case 'p':
                        type = Figure.PAWN;
                        break;
                    case 'b':
                        type = Figure.BISHOP;
                        break;
                    case 'n':
                        type = Figure.KNIGHT;
                        break;
                    case 'r':
                        type = Figure.ROOK;
                        break;
                    case 'q':
                        type = Figure.QUEEN;
                        break;
                    case 'k':
                        type = Figure.KING;
                        break;
                }
                if (type != Figure.NONE) {
                    Figure fig = new Figure(type, isWhite);
                    BOARD[y][x] = fig;
                }
            }
        }
    }

    /**
     * Print board
     *
     * @param board
     */
    public static void printBoard(Figure[][] board) {
        for (int y = 0; y < 8; y++) {
            System.out.println("");
            for (int x = 0; x < 8; x++) {
                Figure f = board[y][x];
                if (f != null) {
                    switch (f.getType()) {
                        case Figure.BISHOP:
                            System.out.print((f.isWhite() ? 'b' : 'B') + "|");
                            break;
                        case Figure.KING:
                            System.out.print((f.isWhite() ? 'k' : 'K') + "|");
                            break;
                        case Figure.KNIGHT:
                            System.out.print((f.isWhite() ? 'n' : 'N') + "|");
                            break;
                        case Figure.PAWN:
                            System.out.print((f.isWhite() ? 'p' : 'P') + "|");
                            break;
                        case Figure.QUEEN:
                            System.out.print((f.isWhite() ? 'q' : 'Q') + "|");
                            break;
                        case Figure.ROOK:
                            System.out.print((f.isWhite() ? 'r' : 'R') + "|");
                            break;
                    }
                } else {
                    System.out.print("-|");
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     * @see King(white) - k
     * @see Queen(white) - q
     * @see Rook(white) - r
     * @see Bishop(white) - b
     * @see Knight(white) - n
     * @see Pawn(white) - p
     * @see (BLACK) is upper case
     */
    public static void main(String[] args) {
        /**
         * @see 0 - board
         * @see 1 - AI color
         * @see 2 - depth
         */
        try {
            //find best move
            if (args.length == 3) {
                boolean isWhite = args[1].equals("w");
                int depth = Integer.parseInt(args[2]);
                if (depth < 1) {
                    System.exit(0);
                }
                //init
                Main sempAI = new Main(isWhite);
                //build board
                sempAI.buildBoard(args[0]);
                //init
                sempAI.moveManager.init();
                //run 
                Move best = sempAI.algorithm.findBestTurn(depth);
                if (best != null) {
                    System.out.println(Main.P(best.getFrom()) + "->" + Main.P(best.getTo()));
                } else {
                    System.out.println("null");
                }
            } else {
                Main sempAI = new Main(false);
                switch (args[0]) {
                    case "INFO":
                        System.out.println(
                                sempAI.NAME + ";"
                                + sempAI.AUTHOR + ";"
                                + sempAI.VERSION + ";"
                                + sempAI.INFO
                        );
                        break;
                    case "EVAL":
                        sempAI.buildBoard(args[1]);
                        System.out.println(sempAI.evaluator.getScoreOfBoard());
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Bad syntax");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String P(Point p) {
        return p.x + "," + p.y;
    }

}
