/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.Point;

/**
 *
 * @author Marti
 */
public class Node {
    
    private Point dp, np;
    
    public Node(Point DEF, Point NEW){
        this.dp = DEF;
        this.np = NEW;
    }
    
    public Point getDefaultPosition(){
        return dp;
    }
    
    public Point getNewPosition(){
        return np;
    }
    
}
