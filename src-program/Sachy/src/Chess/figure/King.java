/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.figure;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import Chess.Tool;

/**
 *
 * @author Marti
 */
public class King implements Figure, Cloneable {

    public Object clone() throws CloneNotSupportedException {
        return (King) super.clone();
    }

    private int X, Y;
    private final boolean White;
    private final BufferedImage image;

    /**
     *
     * @param x X position on the board
     * @param y Y position on the board
     * @param white If this is "true" than this figure is white
     * @param width Width of figure image
     * @param height Height of figure image
     */
    public King(int x, int y, boolean white, int width, int height) {
        this.X = x;
        this.Y = y;
        this.White = white;
        if (white) {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/king_white.png")).getImage(),
                    width,
                    height,
                    false
            );
        } else {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/king_black.png")).getImage(),
                    width,
                    height,
                    false
            );
        }
    }

    @Override
    public void render(Graphics2D g2, float pX, float pY, float xOffSet, float yOffSet, boolean select, boolean realPosition) {
        if (realPosition) {
            //draw figure
            g2.drawImage(
                    this.image,
                    (int) (pX * (this.X + 0.5) + xOffSet - this.image.getWidth() / 2),
                    (int) (pY * (this.Y + 0.5) + yOffSet - this.image.getHeight() / 2),
                    null
            );
        } else {
            //draw figure
            g2.drawImage(
                    this.image,
                    (int) (xOffSet - this.image.getWidth() / 2),
                    (int) (yOffSet - this.image.getHeight() / 2),
                    null
            );
        }
    }

    @Override
    public Point getPosition() {
        return new Point(this.X, this.Y);
    }

    @Override
    public int getType() {
        return Figure.KING;
    }

    @Override
    public ArrayList<Point> getPossibleTurns(ArrayList<Figure> figures, Figure ignorated) {
        ArrayList<Point> pt = new ArrayList<>();
        Point p = this.getPosition();
        //normal turns
        for (int l = -1; l <= 1; l++) {
            for (int j = -1; j <= 1; j++) {
                if (l != 0 || j != 0) {
                    Figure enemy = Tool.findFigure(figures, new Point(p.x + j, p.y + l));
                    if (enemy != null) {
                        if (enemy.isWhite() != this.isWhite() || enemy == ignorated) {
                            if (enemy.getDefenders(figures).isEmpty()) {
                                Point np = new Point(p.x + j, p.y + l);
                                if (Tool.isSaveForKing(figures, this, np)) {
                                    if (Tool.isInBoard(np)) {
                                        pt.add(np);
                                    }
                                }
                            }
                        }
                    } else {
                        Point np = new Point(p.x + j, p.y + l);
                        if (Tool.isInBoard(np)) {
                            if (Tool.isSaveForKing(figures, this, np)) {
                                pt.add(np);
                            }
                        }
                    }
                }
            }
        }
        //rošády
        if (this.isInDefaultPosition) {
            //malá
            Point end = new Point(this.getPosition().x + 3, this.getPosition().y);
            Figure f = Tool.findFigure(figures, end);
            if (f != null && f.getType() == Figure.ROOK) {
                if (Tool.isPathClear(figures, this.getPosition(), end, null) == 3) {
                    pt.add(new Point(this.getPosition().x + 2, this.getPosition().y));
                }
            }
            //veliká
            end = new Point(this.getPosition().x - 4, this.getPosition().y);
            f = Tool.findFigure(figures, end);
            if (f != null && f.getType() == Figure.ROOK) {
                if (Tool.isPathClear(figures, this.getPosition(), end, null) == 4) {
                    pt.add(new Point(this.getPosition().x - 2, this.getPosition().y));
                }
            }
        }
        return pt;
    }

    @Override
    public ArrayList<Figure> getDefenders(ArrayList<Figure> figures) {
        ArrayList<Figure> defenders = new ArrayList<>();

        for (Figure fig : figures) {
            if (fig != this) {
                if (fig.isWhite() == this.isWhite()) {
                    //if (fig.getType() != Figure.KING) -> nutné pro zabrání zacyklení 
                    if (fig.getType() != Figure.KING) {
                        ArrayList<Point> pt = fig.getPossibleTurns(figures, this);
                        for (Point p : pt) {
                            if (p.equals(this.getPosition())) {
                                defenders.add(fig);
                            }
                        }
                    } else {
                        double dist = Math.pow(this.getPosition().x - fig.getPosition().x, 2) + Math.pow(this.getPosition().y - fig.getPosition().y, 2);
                        if (dist <= Math.sqrt(2)) {
                            defenders.add(fig);
                        }
                    }
                }
            }
        }

        return defenders;
    }

    @Override
    public ArrayList<Figure> getAtackers(ArrayList<Figure> figures) {
        ArrayList<Figure> atackers = new ArrayList<>();
        for (Figure fig : figures) {
            if (fig != this) {
                if (fig.getType() != Figure.KING) {
                    if (fig.isWhite() != this.isWhite()) {
                        ArrayList<Point> pt = fig.getPossibleTurns(figures, this);
                        /*
                                 *Pokud je figura pěšeč tak král muže před něj i
                                 *když pěšec má možný tah před sebou, ale pěšec
                                 *neohrožuje figury před sebou
                         */
                        if (fig.getType() == Figure.PAWN) {
                            for (Point p : pt) {
                                if (p.x != fig.getPosition().x) {
                                    if (p.equals(this.getPosition())) {
                                        atackers.add(fig);
                                    }
                                }
                            }
                        } else {
                            for (Point p : pt) {
                                if (p.equals(this.getPosition())) {
                                    atackers.add(fig);
                                }
                            }
                        }

                    }
                }
            }
        }
        return atackers;
    }

    @Override
    public boolean isWhite() {
        return this.White;
    }

    boolean isInDefaultPosition = true;

    @Override
    public void turn(Point position) {
        this.isInDefaultPosition = false;
        this.X = position.x;
        this.Y = position.y;
    }

}
