/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Marti
 */
public class Config implements Serializable {

    public Config() {
    }

    public PlayerInfoPack player_info_1, player_info_2;

    public Color board_dark = new Color(110, 110, 120),
            board_light = new Color(240, 240, 240),
            board_background = new Color(255, 255, 240),
            board_text = Color.BLACK;

    static class PlayerInfoPack implements Serializable {

        public String name;
        public int depth;
        public boolean isAIAllowed;
        public String AI_PATH;
        
        public PlayerInfoPack(Player p){
            this.name = p.name;
            this.depth = p.DEPTH;
            this.isAIAllowed = p.isAIAllowed;
            this.AI_PATH = p.AI_PATH;
        }
    }

}
