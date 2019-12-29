/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Krčma
 */
package sempai;

import java.awt.Point;

/**
 *
 * @author Marti
 */
public class Move {
    
    private int pawn_change_to = Figure.NONE;
    private Point from, to;
    
    public Move(Point from, Point to){
        this.from = from;
        this.to = to;
    }
    
    public Move(Point from, Point to, int pawn_change_to){
        this.from = from;
        this.to = to;
        this.pawn_change_to = pawn_change_to;
    }
    
    public Point getFrom(){
        return this.from;
    }
    
    public Point getTo(){
        return this.to;
    }
    
    public int getPawnChange(){
        return this.pawn_change_to;
    }
    
}
