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
public class Knight implements Figure, Cloneable {

    public Object clone() throws CloneNotSupportedException {
        return (Knight) super.clone();
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
    public Knight(int x, int y, boolean white, int width, int height) {
        this.X = x;
        this.Y = y;
        this.White = white;
        if (white) {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/knight_white.png")).getImage(),
                    width,
                    height,
                    false
            );
        } else {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/knight_black.png")).getImage(),
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
        return Figure.KNIGHT;
    }

    @Override
    public ArrayList<Point> getPossibleTurns(ArrayList<Figure> figures, Figure ignorated) {
        ArrayList<Point> pt = new ArrayList<>();
        Point p = this.getPosition();
        for (int k = 0; k <= 1; k++) {
            for (int i = 0; i <= 1; i++) {
                for (int j = -1; j <= 1; j += 2) {
                    Point np = null;
                    if (k == 0) {
                        np = new Point(p.x + j, p.y + 2 - 4 * i);
                    } else {
                        np = new Point(p.x + 2 - 4 * i, p.y + j);
                    }
                    if (Tool.isInBoard(np)) {
                        Figure fig = Tool.findFigure(figures, np);
                        if (fig != null) {
                            if (fig.isWhite() != this.isWhite() && fig.getType() != Figure.KING || fig == ignorated) {
                                pt.add(np);
                            }
                        } else {
                            pt.add(np);
                        }
                    }
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
                if (fig.isWhite() != this.isWhite()) {
                    ArrayList<Point> points = fig.getPossibleTurns(figures, null);
                    for (Point p : points) {
                        if (p.equals(this.getPosition())) {
                            atackers.add(fig);
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

    @Override
    public void turn(Point position) {
        this.X = position.x;
        this.Y = position.y;
    }

}
