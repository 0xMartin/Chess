/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Krčma
 */
package sempai;

/**
 *
 * @author Marti
 */
public class Figure {

    public static final int PAWN = 1,
            KNIGHT = 2,
            BISHOP = 3,
            ROOK = 4,
            QUEEN = 5,
            KING = 6,
            NONE = -1;

    private int type;
    private boolean isWhite;

    public Figure(int type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    public int getType() {
        return this.type;
    }
    
    public void setType(int type) {
        this.type = type;
    }

    public boolean isWhite() {
        return this.isWhite;
    }
    
    public void setColor(boolean isWhite) {
        this.isWhite = isWhite;
    }

}
