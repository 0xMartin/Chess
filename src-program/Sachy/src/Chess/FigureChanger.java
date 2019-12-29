/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import Chess.figure.Bishop;
import Chess.figure.Figure;
import Chess.figure.Knight;
import Chess.figure.Queen;
import Chess.figure.Rook;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Marti
 */
public class FigureChanger {

    public Figure change(JFrame p, Figure f) {
        Figure out = null;

        ImageIcon queen;
        ImageIcon bishop;
        ImageIcon rook;
        ImageIcon knight;
        if (f.isWhite()) {

            queen = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/queen_white.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            bishop = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/bishop_white.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            rook = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/rook_white.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            knight = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/knight_white.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

        } else {

            queen = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/queen_black.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            bishop = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/bishop_black.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            rook = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/rook_black.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );

            knight = new ImageIcon(
                    Tool.createResizedCopy(
                            new ImageIcon(this.getClass().getResource("/Chess/src/img/knight_black.png")).getImage(),
                            50,
                            50,
                            false
                    )
            );
        }

        Object[] choices = {
            "Queen", "Bishop", "Rook", "Knight"
        };

        Object[] img = {
            queen, bishop, rook, knight
        };
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel(queen));
        panel.add(new JLabel(bishop));
        panel.add(new JLabel(rook));
        panel.add(new JLabel(knight));

        int response = JOptionPane.showOptionDialog(
                null,
                panel,
                "Figure changer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                "None of your business"
        );

        return FigureChanger.getFigure(response, f);
    }

    public static Figure getFigure(int response, Figure f) {
        Figure out = null;
        switch (response) {
            case 0:
                out = new Queen(
                        f.getPosition().x,
                        f.getPosition().y,
                        f.isWhite(),
                        50,
                        50
                );
                break;
            case 1:
                out = new Bishop(
                        f.getPosition().x,
                        f.getPosition().y,
                        f.isWhite(),
                        50,
                        50
                );
                break;
            case 2:
                out = new Rook(
                        f.getPosition().x,
                        f.getPosition().y,
                        f.isWhite(),
                        50,
                        50
                );
                break;
            case 3:
                out = new Knight(
                        f.getPosition().x,
                        f.getPosition().y,
                        f.isWhite(),
                        50,
                        50
                );
                break;
        }
        return out;
    }

}
