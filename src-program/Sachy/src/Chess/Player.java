/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.Point;
import java.util.ArrayList;
import Chess.figure.Figure;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marti
 */
public class Player implements Serializable {

    public boolean isAIAllowed = false;
    public int DEPTH = 5;
    public String AI_PATH = "";

    private final GameManager gameManager;

    public boolean isOnTurn;

    public boolean isWhite;

    public String name;

    public Player(GameManager gm, boolean isWhite) {
        this.gameManager = gm;
        this.isWhite = isWhite;
        this.isOnTurn = false;
    }

    public boolean turnAI() {
        if (this.AI_PATH.length() == 0) {
            return false;
        }
        try {
            //najde nejlepší tah
            Node BEST = runAIProcess();

            //najde figuru v poli
            Figure f = Tool.findFigure(
                    this.gameManager.figures,
                    BEST.getDefaultPosition(),
                    this.isWhite
            );
            //táhne na donou pozici
            f.turn(BEST.getNewPosition());
            //rošáda
            if (f.getType() == Figure.KING) {
                if (Math.abs(f.getPosition().x - BEST.getDefaultPosition().x) > 1) {
                    if (f.getPosition().x - BEST.getDefaultPosition().x == 2) {
                        Figure rook = Tool.findFigure(
                                this.gameManager.figures,
                                new Point(7, f.getPosition().y)
                        );
                        rook.turn(new Point(f.getPosition().x - 1, f.getPosition().y));
                    } else {
                        Figure rook = Tool.findFigure(
                                this.gameManager.figures,
                                new Point(0, f.getPosition().y)
                        );
                        rook.turn(new Point(f.getPosition().x + 1, f.getPosition().y));
                    }
                }
            }
            //pokud to jde zabije figuru
            Figure killed = this.gameManager.killFigure(BEST.getNewPosition(), f);
            //záznam dat o tahu
            main.dataLogger.addTurn(f, BEST.getDefaultPosition());
            if (killed != null) {
                main.dataLogger.removeFigure(killed);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public boolean turn(Figure figure, Point new_position) {
        try {
            if (figure.isWhite() != this.isWhite) {
                return false;
            }
            ArrayList<Point> pt = figure.getPossibleTurns(this.gameManager.figures, null);
            for (Point p : pt) {
                if (p.equals(new_position)) {
                    Point last = figure.getPosition();
                    figure.turn(new_position);
                    //rošáda
                    if (figure.getType() == Figure.KING) {
                        if (Math.abs(figure.getPosition().x - last.x) > 1) {
                            if (figure.getPosition().x - last.x == 2) {
                                Figure rook = Tool.findFigure(
                                        this.gameManager.figures,
                                        new Point(7, figure.getPosition().y)
                                );
                                rook.turn(new Point(figure.getPosition().x - 1, figure.getPosition().y));
                            } else {
                                Figure rook = Tool.findFigure(
                                        this.gameManager.figures,
                                        new Point(0, figure.getPosition().y)
                                );
                                rook.turn(new Point(figure.getPosition().x + 1, figure.getPosition().y));
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public Process AIprocess = null;
    
    private Node runAIProcess() {
        this.AIprocess = null;
        //create process
        String board = "";
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Figure fig = null;
                for (Figure f : this.getGameManager().figures) {
                    if (f.getPosition().equals(new Point(x, y))) {
                        fig = f;
                        break;
                    }
                }
                if (fig != null) {
                    switch (fig.getType()) {
                        case Figure.BISHOP:
                            board += fig.isWhite() ? 'b' : 'B';
                            break;
                        case Figure.KING:
                            board += fig.isWhite() ? 'k' : 'K';
                            break;
                        case Figure.KNIGHT:
                            board += fig.isWhite() ? 'n' : 'N';
                            break;
                        case Figure.PAWN:
                            board += fig.isWhite() ? 'p' : 'P';
                            break;
                        case Figure.QUEEN:
                            board += fig.isWhite() ? 'q' : 'Q';
                            break;
                        case Figure.ROOK:
                            board += fig.isWhite() ? 'r' : 'R';
                            break;
                    }
                } else {
                    board += '-';
                }
            }
        }
        String color = this.isWhite ? "w" : "b";
        String depth = this.DEPTH + "";
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", this.AI_PATH, board, color, depth);
        builder.redirectErrorStream(true);
        try {
            this.AIprocess = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.AIprocess.getInputStream()));
        //read output
        String data = "";
        try { 
            data = bufferedReader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] ponits = data.split("->");
        
        Node best = new Node(
                new Point(
                        Integer.parseInt(ponits[0].split(",")[0]),
                        Integer.parseInt(ponits[0].split(",")[1])
                ),
                new Point(
                        Integer.parseInt(ponits[1].split(",")[0]),
                        Integer.parseInt(ponits[1].split(",")[1])
                )
        );
        
        return best;
    }

}
