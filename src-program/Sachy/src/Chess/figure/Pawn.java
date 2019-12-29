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
public class Pawn implements Figure, Cloneable {

    public Object clone() throws CloneNotSupportedException {
        return (Pawn) super.clone();
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
    public Pawn(int x, int y, boolean white, int width, int height) {
        this.X = x;
        this.Y = y;
        this.White = white;
        if (white) {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/pawn_white.png")).getImage(),
                    width,
                    height,
                    false
            );
        } else {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/pawn_black.png")).getImage(),
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
        return Figure.PAWN;
    }

    @Override
    public ArrayList<Point> getPossibleTurns(ArrayList<Figure> figures, Figure ignorated) {
        ArrayList<Point> pt = new ArrayList<>();
        Point p = this.getPosition();
        //atack
        if (!this.isWhite()) {
            for (int j = -1; j <= 1; j += 2) {
                Figure enemy = Tool.findFigure(figures, new Point(p.x + j, p.y + 1));
                if (enemy != null) {
                    if (enemy.isWhite() != this.isWhite() && enemy.getType() != Figure.KING || enemy == ignorated) {
                        pt.add(new Point(p.x + j, p.y + 1));
                    }
                }
            }
        } else {
            for (int j = -1; j <= 1; j += 2) {
                Figure enemy = Tool.findFigure(figures, new Point(p.x + j, p.y - 1));
                if (enemy != null) {
                    if (enemy.isWhite() != this.isWhite() && enemy.getType() != Figure.KING || enemy == ignorated) {
                        pt.add(new Point(p.x + j, p.y - 1));
                    }
                }
            }
        }
        //fisrt turn pawn can go for 2 tiles
        if (this.isInDefaultPosition) {
            if (!this.isWhite()) {
                if (Tool.isPathClear(figures, (Point) p.clone(), new Point(p.x, p.y + 2), ignorated) == 0) {
                    pt.add(new Point(p.x, p.y + 2));
                }
            } else {
                if (Tool.isPathClear(figures, (Point) p.clone(), new Point(p.x, p.y - 2), ignorated) == 0) {
                    pt.add(new Point(p.x, p.y - 2));
                }
            }
        }
        //normal turn
        if (!this.isWhite()) {
            Point np = new Point(p.x, p.y + 1);
            if (Tool.isInBoard(np)) {
                if (Tool.isPathClear(figures, (Point) p.clone(), np, ignorated) == 0) {
                    pt.add(np);
                }
            }
        } else {
            Point np = new Point(p.x, p.y - 1);
            if (Tool.isInBoard(np)) {
                if (Tool.isPathClear(figures, (Point) p.clone(), np, ignorated) == 0) {
                    pt.add(np);
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

    boolean isInDefaultPosition = true;

    @Override
    public void turn(Point position) {
        this.isInDefaultPosition = false;
        this.X = position.x;
        this.Y = position.y;
    }

}
