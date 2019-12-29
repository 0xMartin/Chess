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
public class Bishop implements Figure, Cloneable {

    public Object clone() throws CloneNotSupportedException {
        return (Bishop) super.clone();
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
    public Bishop(int x, int y, boolean white, int width, int height) {
        this.X = x;
        this.Y = y;
        this.White = white;
        if (white) {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/bishop_white.png")).getImage(),
                    width,
                    height,
                    false
            );
        } else {
            this.image = Tool.createResizedCopy(
                    new ImageIcon(this.getClass().getResource("/Chess/src/img/bishop_black.png")).getImage(),
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
        return Figure.BISHOP;
    }

    @Override
    public ArrayList<Point> getPossibleTurns(ArrayList<Figure> figures, Figure ignorated) {
        ArrayList<Point> pt = new ArrayList<>();
        Point p = this.getPosition();
        for (int d = 0; d < 4; d++) {
            Point end = null;
            int dist2 = 0; //vzdálenost figury od kraje hrací plochy
            //nastavení koncového bodu
            switch (d) {
                case 0:
                    dist2 = p.x;
                    if (p.y < dist2) {
                        dist2 = p.y;
                    }
                    end = new Point(p.x - dist2, p.y - dist2);
                    break;
                case 1:
                    dist2 = p.x;
                    if (7 - p.y < dist2) {
                        dist2 = 7 - p.y;
                    }
                    end = new Point(p.x - dist2, p.y + dist2);
                    break;
                case 2:
                    dist2 = 7 - p.x;
                    if (7 - p.y < dist2) {
                        dist2 = 7 - p.y;
                    }
                    end = new Point(p.x + dist2, p.y + dist2);
                    break;
                case 3:
                    dist2 = 7 - p.x;
                    if (p.y < dist2) {
                        dist2 = p.y;
                    }
                    end = new Point(p.x + dist2, p.y - dist2);
                    break;
            }
            int dist = Tool.isPathClear(figures, (Point) p.clone(), end, ignorated);   //vzdálenos od jiné figury
            if (dist == 0) {
                dist = dist2;
            }
            int c = 0;
            Point ps = (Point) p.clone();
            while (!ps.equals(end) && c < dist) {
                c++;
                if (ps.x < end.x) {
                    ps.x++;
                }
                if (ps.y < end.y) {
                    ps.y++;
                }
                if (ps.x > end.x) {
                    ps.x--;
                }
                if (ps.y > end.y) {
                    ps.y--;
                }
                if (c == dist) {
                    //pukud je nepřítel
                    Figure f = Tool.findFigure(figures, ps);
                    if (f != null) {
                        if (f.isWhite() != this.isWhite() && f.getType() != Figure.KING || f == ignorated) {
                            pt.add((Point) ps.clone());
                        }
                    } else {
                        pt.add((Point) ps.clone());
                    }
                } else {
                    pt.add((Point) ps.clone());
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
