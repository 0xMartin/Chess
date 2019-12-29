/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.Dimension;
import java.awt.Point;
import Chess.figure.Figure;
import java.util.ArrayList;
import Chess.figure.Bishop;
import Chess.figure.King;
import Chess.figure.Knight;
import Chess.figure.Pawn;
import Chess.figure.Queen;
import Chess.figure.Rook;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marti
 */
public class GameManager {

    public ArrayList<Figure> figures;

    public Player player1, player2;

    public boolean inGame;

    public GameManager() {
        this.inGame = false;
        this.figures = new ArrayList<>();
        this.player1 = new Player(this, Figure.WHITE);
        this.player1.name = "Player 1";
        this.player2 = new Player(this, Figure.BLACK);
        this.player2.name = "Player 2";
        this.player1.isOnTurn = true;
        thread();
    }

    /**
     * Run AI
     */
    private void thread() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        if (inGame) {
                            if (player1.isOnTurn && player1.isAIAllowed) {
                                boolean success = player1.turnAI();
                                if (success) {
                                    player1.isOnTurn = false;
                                    player2.isOnTurn = true;
                                    main.dataLogger.addToHistory();
                                    //konec hry ?
                                    isgameOver();
                                }
                            }
                            if (player2.isOnTurn && player2.isAIAllowed) {
                                boolean success = player2.turnAI();
                                if (success) {
                                    player1.isOnTurn = true;
                                    player2.isOnTurn = false;
                                    main.dataLogger.addToHistory();
                                    //konec hry ?
                                    isgameOver();
                                }
                            }

                        }
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        thread.start();
    }

    /**
     *
     * @param size Size of canvas
     * @param f Size of board in canvas
     */
    public void newGame(Dimension size, float f) {
        this.inGame = true;
        //defaul config
        this.player1.isOnTurn = true;
        this.player2.isOnTurn = false;
        //size (width, height) of on tile
        float pX = (size.width * f) / 8f;
        float pY = (size.height * f) / 8f;
        this.figures.clear();
        //white
        crateNewFigures(true, (int) (pX * 0.9f), (int) (pY * 0.9f));
        //black
        crateNewFigures(false, (int) (pX * 0.9f), (int) (pY * 0.9f));
        //data logger
        main.dataLogger.clear();
        main.dataLogger.history.clear();
        main.dataLogger.history_index = 0;
        main.dataLogger.history_model.clear();
        main.dataLogger.history_turns.clear();
        try {
            main.dataLogger.addToHistory();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crateNewFigures(boolean white, int pX, int pY) {
        int first_line;
        int second_line;
        if (white) {
            first_line = 6;
            second_line = 7;
        } else {
            first_line = 1;
            second_line = 0;
        }

        //pawns
        for (int i = 0; i < 8; i++) {
            this.figures.add(
                    new Pawn(i, first_line, white, pX, pY)
            );
        }
        //rooks
        this.figures.add(
                new Rook(0, second_line, white, pX, pY)
        );
        this.figures.add(
                new Rook(7, second_line, white, pX, pY)
        );
        //kinghts
        this.figures.add(
                new Knight(1, second_line, white, pX, pY)
        );
        this.figures.add(
                new Knight(6, second_line, white, pX, pY)
        );
        //bishops
        this.figures.add(
                new Bishop(2, second_line, white, pX, pY)
        );
        this.figures.add(
                new Bishop(5, second_line, white, pX, pY)
        );
        //king and queen
        this.figures.add(
                new King(4, second_line, white, pX, pY)
        );
        this.figures.add(
                new Queen(3, second_line, white, pX, pY)
        );

    }

    /**
     * Return figure that is under your cursor
     *
     * @param mouse
     * @param size
     * @param f
     * @return
     */
    public Figure getFigureInLocation(Point mouse, Dimension size, float f) {
        //constatnts
        float f1 = (float) ((1.0 - f) / 2f);
        int xOffSet = (int) (size.getWidth() * f1);
        int yOffSet = (int) (size.getHeight() * f1);
        float pX = (float) ((size.getWidth() * f) / 8);
        float pY = (float) ((size.getHeight() * f) / 8);

        int x = (int) ((mouse.x - xOffSet) / pX);
        int y = (int) ((mouse.y - yOffSet) / pY);

        for (Figure fig : this.figures) {
            if (fig.getPosition().equals(new Point(x, y))) {
                return fig;
            }
        }

        return null;
    }

    public Point getBoardPosition(Point mouse, Dimension size, float f) {
        //constatnts
        float f1 = (float) ((1.0 - f) / 2f);
        int xOffSet = (int) (size.getWidth() * f1);
        int yOffSet = (int) (size.getHeight() * f1);
        float pX = (float) ((size.getWidth() * f) / 8);
        float pY = (float) ((size.getHeight() * f) / 8);

        int x = (int) ((mouse.x - xOffSet) / pX);
        int y = (int) ((mouse.y - yOffSet) / pY);
        return new Point(x, y);
    }

    /**
     * Make turn in game, automatic verifying
     *
     * @param figure Selected figure
     * @param new_position New position
     * @return Success
     */
    public ArrayList<Figure> turn(Figure figure, Point new_position) {
        if (!this.inGame) {
            return null;
        }
        try {
            Point last = (Point) figure.getPosition().clone();
            //vytvoření kipie hry pro případné vrácení
            ArrayList<Figure> fig_copy = Tool.clone(this.figures);

            //hráč 1
            if (this.player1.isOnTurn) {
                boolean success = this.player1.turn(figure, new_position);
                if (success) {
                    Figure killed = killFigure(new_position, figure);
                    //pokud není král v bezmečí vráti vše do puvodní pozice a zbytek se nevykoná
                    ArrayList king_attackers = isKingProtected_arr(this.player1.isWhite);
                    if (king_attackers != null) {
                        if (king_attackers.size() > 0) {
                            this.figures = fig_copy;
                            return king_attackers;
                        }
                    }
                    //výměna tahu
                    this.player1.isOnTurn = false;
                    this.player2.isOnTurn = true;
                    //záznam dat o tahu
                    main.dataLogger.addTurn(figure, last);
                    if (killed != null) {
                        main.dataLogger.removeFigure(killed);
                    }
                    //záměna
                    if (figure.getType() == Figure.PAWN) {
                        if (figure.getPosition().y == 0 || figure.getPosition().y == 7) {
                            Figure changed = main.figureChanger.change(main.form, figure);
                            if (changed != null) {
                                this.figures.remove(figure);
                                this.figures.add(changed);
                            }
                        }
                    }
                    main.dataLogger.addToHistory();
                    //konec hry ?
                    isgameOver();
                    return null;
                }
            }
            //hráč 2
            if (this.player2.isOnTurn) {
                boolean success = this.player2.turn(figure, new_position);
                if (success) {
                    Figure killed = killFigure(new_position, figure);
                    //pokud není král v bezmečí vráti vše do puvodní pozice a zbytek se nevykoná
                    ArrayList king_attackers = isKingProtected_arr(this.player2.isWhite);
                    if (king_attackers != null) {
                        if (king_attackers.size() > 0) {
                            this.figures = fig_copy;
                            return king_attackers;
                        }
                    }
                    //výměna tahu
                    this.player2.isOnTurn = false;
                    this.player1.isOnTurn = true;
                    //záznam dat o tahu
                    main.dataLogger.addTurn(figure, last);
                    if (killed != null) {
                        main.dataLogger.removeFigure(killed);
                    }
                    //záměna
                    if (figure.getType() == Figure.PAWN) {
                        if (figure.getPosition().y == 0 || figure.getPosition().y == 7) {
                            Figure changed = main.figureChanger.change(main.form, figure);
                            if (changed != null) {
                                this.figures.remove(figure);
                                this.figures.add(changed);
                            }
                        }
                    }
                    main.dataLogger.addToHistory();
                    //konec hry ?
                    isgameOver();
                    return null;
                }
            }

        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Kill figure in position
     *
     * @param positon Position
     * @param killer Figure who kill figure in position
     * @return Killed figure
     */
    public Figure killFigure(Point positon, Figure killer) {
        for (Figure fig : this.figures) {
            if (fig.getPosition().equals(positon) && fig != killer) {
                this.figures.remove(fig);
                return fig;
            }
        }
        return null;
    }

    /**
     * Get color of player in turn
     *
     * @return
     */
    public boolean getActualColor() {
        if (this.player1.isOnTurn) {
            return this.player1.isWhite;
        }
        if (this.player2.isOnTurn) {
            return this.player2.isWhite;
        }
        return false;
    }

    public boolean playAINow() {
        if (this.player1.isOnTurn) {
            return this.player1.isAIAllowed;
        }
        if (this.player2.isOnTurn) {
            return this.player2.isAIAllowed;
        }
        return false;
    }

    /**
     * True -> king is protected
     *
     * @param isWhite Color of king
     * @return Boolean
     */
    public boolean isKingProtected(boolean isWhite) {
        for (Figure fig : this.figures) {
            if (fig.getType() == Figure.KING) {
                if (fig.isWhite() == isWhite) {
                    return Tool.isSaveForKing(this.figures, fig, fig.getPosition());
                }
            }
        }
        return false;
    }

    /**
     * True -> king is protected
     *
     * @param isWhite Color of king
     * @return atackers
     */
    public ArrayList<Figure> isKingProtected_arr(boolean isWhite) {
        for (Figure fig : this.figures) {
            if (fig.getType() == Figure.KING) {
                if (fig.isWhite() == isWhite) {
                    if (!Tool.isSaveForKing(this.figures, fig, fig.getPosition())) {
                        return fig.getAtackers(this.figures);
                    }
                }
            }
        }
        return null;
    }

    public void isgameOver() {
        /*
        AI ai = new AI(this.player1);
        if (ai.getMoves_out(this.figures, this.player1.isWhite).isEmpty()) {
            JOptionPane.showConfirmDialog(main.form, "Player " + this.player1.name + " was defeated");
            inGame = false;
        }
        if (ai.getMoves_out(this.figures, this.player2.isWhite).isEmpty()) {
            JOptionPane.showConfirmDialog(main.form, "Player " + this.player2.name + " was defeated");
            inGame = false;
        }
         */
    }

}
