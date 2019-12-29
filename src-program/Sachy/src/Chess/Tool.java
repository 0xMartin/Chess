/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Chess.figure.Bishop;
import Chess.figure.Figure;
import Chess.figure.King;
import Chess.figure.Knight;
import Chess.figure.Pawn;
import Chess.figure.Queen;
import Chess.figure.Rook;
import static java.awt.PageAttributes.MediaType.E;
import javax.swing.DefaultListModel;

/**
 *
 * @author Marti
 */
public class Tool {

    /**
     * Resize image
     *
     * @param originalImage Image
     * @param scaledWidth Width
     * @param scaledHeight Height
     * @param preserveAlpha
     * @return
     */
    public static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        if (scaledWidth == 0 || scaledHeight == 0) {
            return null;
        }
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

    /**
     * True -> figure is in upper side of the board
     *
     * @param position Figure position
     * @return
     */
    public static boolean isInUpperSide(Point position) {
        int y = position.y;
        return y >= 0 && y <= 3;
    }

    /**
     * Find figure in array
     *
     * @param figures All figures
     * @param position Position of figures
     * @return
     */
    public static Figure findFigure(ArrayList<Figure> figures, Point position) {
        for (Figure fig : figures) {
            if (fig.getPosition().equals(position)) {
                return fig;
            }
        }
        return null;
    }

    /**
     * Find figure in array
     *
     * @param figures All figures
     * @param position Position of figures
     * @param isWhite Is white?
     * @return
     */
    public static Figure findFigure(ArrayList<Figure> figures, Point position, boolean isWhite) {
        for (Figure fig : figures) {
            if (fig.getPosition().equals(position)) {
                if (fig.isWhite() == isWhite) {
                    return fig;
                }
            }
        }
        return null;
    }

    /**
     * Return distace from nerest figure in the path (start -> end)
     *
     * @param figures All figures
     * @param start Start position
     * @param end End position
     * @param ignorated Figure that will be ignorated
     * @return int (distance)
     */
    public static int isPathClear(ArrayList<Figure> figures, Point start, Point end, Figure ignorated) {
        int c = 0;
        while (!start.equals(end)) {
            c++;
            if (start.x < end.x) {
                start.x++;
            }
            if (start.y < end.y) {
                start.y++;
            }
            if (start.x > end.x) {
                start.x--;
            }
            if (start.y > end.y) {
                start.y--;
            }
            Figure f = Tool.findFigure(figures, start);
            if (f != null && f != ignorated) {
                return c;
            }
        }
        return 0;
    }

    /**
     * True -> position is in board
     *
     * @param position Point
     * @return boolean
     */
    public static boolean isInBoard(Point position) {
        return position.x >= 0 && position.x <= 7 && position.y >= 0 && position.y <= 7;
    }

    /**
     * True -> new position is save for king
     *
     * @param figures All figures
     * @param king King
     * @param position New position
     * @return boolean
     */
    public static boolean isSaveForKing(ArrayList<Figure> figures, Figure king, Point position) {
        for (Figure f : figures) {
            if (f.getType() == Figure.KING) {
                if (f != king) {
                    if (Math.pow(f.getPosition().x - position.x, 2) + Math.pow(f.getPosition().y - position.y, 2) <= 2) {
                        return false;
                    }
                }
            }
        }
        for (Figure fig : figures) {
            if (fig.getType() != Figure.KING) {
                if (fig.isWhite() != king.isWhite()) {
                    ArrayList<Point> pt = fig.getPossibleTurns(figures, king);
                    /*
                                 Pokud je figura pěšeč tak král muže před něj i
                                 když pěšec má možný tah před sebou, ale pěšec
                                 neohrožuje figury před sebou
                     */
                    if (fig.getType() == Figure.PAWN) {
                        if (fig.isWhite()) {
                            if (new Point(fig.getPosition().x - 1, fig.getPosition().y - 1).equals(position)) {
                                return false;
                            }

                            if (new Point(fig.getPosition().x + 1, fig.getPosition().y - 1).equals(position)) {
                                return false;
                            }
                        } else {
                            if (new Point(fig.getPosition().x - 1, fig.getPosition().y + 1).equals(position)) {
                                return false;
                            }

                            if (new Point(fig.getPosition().x + 1, fig.getPosition().y + 1).equals(position)) {
                                return false;
                            }
                        }
                        for (Point p : pt) {
                            if (p.x != fig.getPosition().x) {
                                if (p.equals(position)) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        for (Point p : pt) {
                            if (p.equals(position)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static ArrayList<Figure> clone(ArrayList<Figure> figures) {
        ArrayList<Figure> output = new ArrayList<>();
        for (Figure fig : figures) {
            switch (fig.getType()) {
                case Figure.BISHOP: {
                    try {
                        output.add((Figure) ((Bishop) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                }
                break;
                case Figure.KING:
                    try {
                        output.add((King) ((King) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                    break;
                case Figure.KNIGHT:
                    try {
                        output.add((Knight) ((Knight) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                    break;
                case Figure.PAWN:
                    try {
                        output.add((Pawn) ((Pawn) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                    break;
                case Figure.QUEEN:
                    try {
                        output.add((Queen) ((Queen) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                    break;
                case Figure.ROOK:
                    try {
                        output.add((Rook) ((Rook) fig).clone());
                    } catch (CloneNotSupportedException ex) {
                    }
                    break;
            }
        }
        return output;
    }

    public static Figure findFigure(ArrayList<Figure> figures, boolean isWhite, int type) {
        for (Figure fig : figures) {
            if (fig.getType() == type) {
                if (fig.isWhite() == isWhite) {
                    return fig;
                }
            }
        }
        return null;
    }

    public static String pointToXX(Point p) {
        String c = String.valueOf((char) (65 + p.x));
        return c + (int) (8 - p.y) + "";
    }

    public static DefaultListModel copy(DefaultListModel model) throws CloneNotSupportedException {
        DefaultListModel model2 = new DefaultListModel();
        for (int i = 0; i < model.getSize(); i++) {
            model2.addElement(((DataLogger.DataEntry)model.elementAt(i)).clone());
        }
        return model2;
    }

}
