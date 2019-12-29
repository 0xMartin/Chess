/*
 *Chess AI
 *Name: SempAI
 *Copyright (c) 2018 Martin Kr�ma
 */
package sempai.evaluator;

import sempai.Figure;

/**
 *
 * @author Marti
 */
public class PawnStructures {

    private final Figure[][] BOARD;
    
    public PawnStructures(Figure[][] board) {
        this.BOARD = board;
    }
    
    public int getScore() {
        return 0;
    }
    
}
