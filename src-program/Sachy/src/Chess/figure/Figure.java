/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.figure;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Marti
 */
public interface Figure {

    public static final boolean WHITE = true,
            BLACK = false;

    /**
     * @PAWN Pěšák
     * @KNIGHT Kůň
     * @BISHOP Střelec
     * @ROOK Věž
     * @QUEEN Královna
     * @KING Král
     */
    public static final int PAWN = 1,
            KNIGHT = 2,
            BISHOP = 3,
            ROOK = 4,
            QUEEN = 5,
            KING = 6;

    /**
     * Render figure
     *
     * @param g2 Graphics
     * @param pX Width of one tile
     * @param pY Height of one tile
     * @param xOffSet X offse
     * @param yOffSet Y offse
     * @param selected True -> select figure
     */
    public void render(Graphics2D g2, float pX, float pY, float xOffSet, float yOffSet, boolean selected, boolean realPosition);

    /**
     * Get position of the figure
     *
     * @return Pont(x, y)
     */
    public Point getPosition();

    /**
     * If this is true then figure is white
     *
     * @return
     */
    public boolean isWhite();

    /**
     * Returt type of figure(pawn, knight, bishop, rook, queen, king);
     *
     * @return Figure type
     */
    public int getType();

    /**
     * Return all possible turns with this figure
     *
     * @param figures All figures
     * @param ignorated Figure that will be ignorated
     * @return Point(x,y)
     */
    public ArrayList<Point> getPossibleTurns(ArrayList<Figure> figures, Figure ignorated);

    /**
     * Return all figure that defend this figure
     *
     * @param figures All figures
     * @return ArrayList<Figure>
     */
    public ArrayList<Figure> getDefenders(ArrayList<Figure> figures);

    /**
     * Return all figure that atack(can kill) this figure
     *
     * @param figures All figures
     * @return ArrayList<Figure>
     */
    public ArrayList<Figure> getAtackers(ArrayList<Figure> figures);

    /**
     * Figure go to the positon
     *
     * @param position
     */
    public void turn(Point position);

}
