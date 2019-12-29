/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import Chess.figure.Bishop;
import Chess.figure.Figure;
import Chess.figure.King;
import Chess.figure.Knight;
import Chess.figure.Pawn;
import Chess.figure.Queen;
import Chess.figure.Rook;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Marti
 */
public class AnimationViewer extends javax.swing.JFrame {

    private Thread render_thread;

    private final float size = 0.88f;

    private ArrayList<ArrayList<Figure>> animation;

    private int animation_position = 0;

    private Thread play;

    /**
     * Creates new form AnimationViewer
     */
    public AnimationViewer() {
        initComponents();
        this.animation = new ArrayList<>();
        this.play = new Thread() {
            public void run() {
                while (true) {
                    jLabel7.setText(jLabel7.getText() + ">");
                    if (jLabel7.getText().length() > 3) {
                        jLabel7.setText("");
                    }
                    try {
                        Thread.sleep(Integer.parseInt(jTextField1.getText()));
                        if (animation_position < animation.size() - 1) {
                            animation_position++;
                        } else {
                            return;
                        }
                        jLabel2.setText((animation_position + 1) + " of " + animation.size());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AnimationViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }

    public void show(JFrame frame, File file) throws FileNotFoundException, IOException {
        this.jLabel4.setText("Name: " + file.getName());
        this.jLabel5.setText("Path: " + file);
        //default settings
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) ((screen.width - this.getWidth()) / 2f), (int) ((screen.height - this.getHeight()) / 2f));
        this.setVisible(true);
        //run rendering
        this.canvas.createBufferStrategy(3);
        run_rendering();
        //import animation
        ArrayList<String> turns = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String turn;
        while ((turn = reader.readLine()) != null) {
            turns.add(turn);
        }
        //build animation
        ArrayList<Figure> figures = new ArrayList<>();
        float pX = (canvas.getWidth() * size) / 8f;
        float pY = (canvas.getHeight() * size) / 8f;
        crateNewFigures(figures, true, (int) (pX * 0.9f), (int) (pY * 0.9f));
        crateNewFigures(figures, false, (int) (pX * 0.9f), (int) (pY * 0.9f));
        for (int i = 0; i < turns.size(); i++) {
            try {
                String t = turns.get(i);
                Point[] pt = turnDecoder(t);
                if (pt != null) {
                    this.animation.add(Tool.clone(figures));
                }
                Figure f = Tool.findFigure(figures, pt[0]);
                try {
                    String t2 = turns.get(i + 1);
                    if (t2.split(" ")[3].equals("removed")) {
                        Figure k = Tool.findFigure(figures, pt[1]);
                        //odstranění figury
                        figures.remove(k);
                    }
                } catch (Exception ex) {
                }
                try {
                    String t2 = turns.get(i + 1);
                    if (t2.split(" ")[0].equals("Changed")) {
                        //změná
                        int response = 0;
                        switch (t2.split(" ")[2]) {
                            case "Queen":
                                response = 0;
                                break;
                            case "Bishop":
                                response = 1;
                                break;
                            case "Rook":
                                response = 2;
                                break;
                            case "Knight":
                                response = 3;
                                break;
                        }
                        figures.remove(f);
                        f = FigureChanger.getFigure(response, f);
                        figures.add(f);
                    }
                } catch (Exception ex) {
                }
                //pohyb figury
                f.turn(pt[1]);
                //rošáda
                if (f.getType() == Figure.KING) {
                    int d = pt[1].x - pt[0].x;
                    if (d > 1) {
                        Figure r = Tool.findFigure(figures, new Point(7, pt[0].y));
                        r.turn(new Point(5, pt[0].y));
                    }
                    if (d < -1) {
                        Figure r = Tool.findFigure(figures, new Point(0, pt[0].y));
                        r.turn(new Point(3, pt[0].y));
                    }
                }
            } catch (Exception ex) {
            }
        }
        this.animation.add(Tool.clone(figures));
        jLabel2.setText((animation_position + 1) + " of " + animation.size());
    }

    private void crateNewFigures(ArrayList<Figure> figures, boolean white, int pX, int pY) {
        int first_line;
        int second_line;
        if (white) {
            first_line = 6;
            second_line = 7;
        } else {
            first_line = 1;
            second_line = 0;
        }

        //pawns
        for (int i = 0; i < 8; i++) {
            figures.add(
                    new Pawn(i, first_line, white, pX, pY)
            );
        }
        //rooks
        figures.add(
                new Rook(0, second_line, white, pX, pY)
        );
        figures.add(
                new Rook(7, second_line, white, pX, pY)
        );
        //kinghts
        figures.add(
                new Knight(1, second_line, white, pX, pY)
        );
        figures.add(
                new Knight(6, second_line, white, pX, pY)
        );
        //bishops
        figures.add(
                new Bishop(2, second_line, white, pX, pY)
        );
        figures.add(
                new Bishop(5, second_line, white, pX, pY)
        );
        //king and queen
        figures.add(
                new King(4, second_line, white, pX, pY)
        );
        figures.add(
                new Queen(3, second_line, white, pX, pY)
        );

    }

    /**
     * Return point, point
     *
     * @param text X0 -> X0
     * @return
     */
    private Point[] turnDecoder(String text) {
        Point[] p = new Point[2];
        int x, y;
        x = (int) (text.charAt(0) - 65);
        y = 8 - Integer.parseInt(text.charAt(1) + "");
        p[0] = new Point(x, y);
        x = (int) (text.charAt(text.length() - 2) - 65);
        y = 8 - Integer.parseInt(text.charAt(text.length() - 1) + "");
        p[1] = new Point(x, y);
        return p;
    }

    private void run_rendering() {
        this.render_thread = new Thread() {
            public void run() {
                while (true) {
                    Graphics2D g2 = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
                    g2.setFont(new Font("Tahoma", Font.BOLD, 16));
                    render(g2, size);
                    canvas.getBufferStrategy().show();
                }
            }

            /**
             *
             * @param g2 Graphics
             * @param size 0.0 <-> 1.0
             */
            private void render(Graphics2D g2, float size) {
                //constatnts
                float f = (float) ((1.0 - size) / 2f);
                float xOffSet = (int) (canvas.getWidth() * f);
                float yOffSet = (int) (canvas.getHeight() * f);
                float pX = (canvas.getWidth() * size) / 8;
                float pY = (canvas.getHeight() * size) / 8;
                //render background  
                renderBackground(g2, xOffSet, yOffSet, pX, pY, size);
                //render all figures
                try {
                    animation.get(animation_position).forEach((fig) -> {
                        fig.render(g2, pX, pY, xOffSet, yOffSet, false, true);
                    });
                } catch (Exception ex) {
                }
            }

            private void renderBackground(Graphics2D g2, float xOffSet, float yOffSet, float pX, float pY, float size) {
                g2.setColor(main.config.board_background);
                g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        //draw tiles
                        if (x % 2 == 0) {
                            if (y % 2 == 0) {
                                g2.setColor(main.config.board_light);
                            } else {
                                g2.setColor(main.config.board_dark);
                            }
                        } else {
                            if (y % 2 == 0) {
                                g2.setColor(main.config.board_dark);
                            } else {
                                g2.setColor(main.config.board_light);
                            }
                        }
                        g2.fillRect((int) (x * pX + xOffSet), (int) (y * pY + yOffSet), (int) pX, (int) pY);
                        //number and chars
                        g2.setColor(main.config.board_text);
                        if (x == 0) {
                            g2.drawString(
                                    (8 - y) + "",
                                    (int) (-pX / 2f + xOffSet + g2.getFontMetrics().stringWidth((char) (65 + y) + "") / 2f),
                                    (int) (y * pY + yOffSet + pY / 2f + g2.getFontMetrics().getHeight() / 2f)
                            );
                        }
                        if (y == 7) {
                            g2.drawString(
                                    (char) (65 + x) + "",
                                    (int) (pX * (x + 0.5f) + xOffSet - g2.getFontMetrics().stringWidth((char) (1 + x) + "") / 2),
                                    (int) (8 * pY + yOffSet + g2.getFontMetrics().getHeight())
                            );
                        }
                    }
                }
                //outline
                g2.setColor(new Color(0, 0, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(
                        (int) xOffSet,
                        (int) yOffSet,
                        (int) (canvas.getWidth() * size),
                        (int) (canvas.getHeight() * size)
                );
            }
        };
        this.render_thread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        body = new javax.swing.JPanel();
        canvas = new java.awt.Canvas();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(204, 204, 0));
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        body.setBackground(new java.awt.Color(250, 250, 230));

        canvas.setMinimumSize(new java.awt.Dimension(500, 500));

        jButton1.setText("PLAY");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("STOP");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("TO BEGIN");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("TO END");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("<");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText(">");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setText("Delay");

        jTextField1.setText("500");
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel2.setText("1 of x");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Chess animation");

        jLabel4.setText("Name:");

        jLabel5.setText("Path:");
        jScrollPane1.setViewportView(jLabel5);

        jLabel6.setText("ms");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton7.setText("<<");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText(">>");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bodyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(bodyLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(bodyLayout.createSequentialGroup()
                    .addGap(520, 520, 520)
                    .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap()))
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(bodyLayout.createSequentialGroup()
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jButton5)
                            .addComponent(jButton6)
                            .addComponent(jButton8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(106, 106, 106))))
            .addGroup(bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(bodyLayout.createSequentialGroup()
                    .addGap(379, 379, 379)
                    .addComponent(jLabel7)
                    .addGap(44, 44, 44)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(jLabel2)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(body, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(body, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (this.animation_position < this.animation.size() - 1) {
            this.animation_position++;
            jLabel2.setText((animation_position + 1) + " of " + animation.size());
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.play.stop();
        jLabel7.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.animation_position = 0;
        jLabel2.setText((animation_position + 1) + " of " + animation.size());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.animation_position = this.animation.size() - 1;
        jLabel2.setText((animation_position + 1) + " of " + animation.size());
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (this.animation_position > 0) {
            this.animation_position--;
            jLabel2.setText((animation_position + 1) + " of " + animation.size());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            this.play.start();
        } catch (Exception ex) {
            this.play = new Thread() {
                public void run() {
                    while (true) {
                        jLabel7.setText(jLabel7.getText() + ">");
                        if (jLabel7.getText().length() > 3) {
                            jLabel7.setText("");
                        }
                        try {
                            Thread.sleep(Integer.parseInt(jTextField1.getText()));
                            if (animation_position < animation.size() - 1) {
                                animation_position++;
                            } else {
                                return;
                            }
                            jLabel2.setText((animation_position + 1) + " of " + animation.size());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AnimationViewer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            this.play.start();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.render_thread.stop();
    }//GEN-LAST:event_formWindowClosing

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        if (this.jTextField1.getText().length() > 6) {
            this.jTextField1.setText(this.jTextField1.getText().substring(0, 6));
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (this.animation_position - 1 > 0) {
            this.animation_position -= 2;
            jLabel2.setText((animation_position + 1) + " of " + animation.size());
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (this.animation_position < this.animation.size() - 2) {
            this.animation_position += 2;
            jLabel2.setText((animation_position + 1) + " of " + animation.size());
        }
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private java.awt.Canvas canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
