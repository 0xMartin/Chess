/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import Chess.figure.Figure;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Marti
 */
public class Form extends javax.swing.JFrame {

    private Point mouse;

    private About about;

    private Thread render_thread;

    public Figure selectedFigure;
    public ArrayList<Point> possibleTurns;
    public ArrayList<Point> impossibleTurns;

    private final float size = 0.88f;

    /**
     * Creates new form form
     */
    public Form() {
        initComponents();
        this.about = new About();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (size.width - this.getWidth()) / 2,
                (size.height - this.getHeight()) / 2
        );
        this.jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            int last = 0;

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (main.dataLogger != null) {
                    if (last != main.dataLogger.model.size()) {
                        last = main.dataLogger.model.size();
                        e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    }
                }
            }
        });
    }

    public void init() {
        //run rendering
        this.canvas.createBufferStrategy(3);
        run_rendering();
    }

    private void run_rendering() {
        this.render_thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Graphics2D g2 = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
                        g2.setFont(new Font("Tahoma", Font.BOLD, 16));
                        render(g2, size);
                        canvas.getBufferStrategy().show();
                    } catch (Exception ex) {
                        canvas.createBufferStrategy(3);
                    }
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
                    main.gameManager.figures.forEach((fig) -> {

                        if (selectedFigure == null) {
                            fig.render(g2, pX, pY, xOffSet, yOffSet, false, true);
                        } else {
                            if (!fig.equals(selectedFigure)) {
                                fig.render(g2, pX, pY, xOffSet, yOffSet, false, true);
                            }
                        }
                    });
                } catch (Exception ex) {
                }
                if (selectedFigure != null) {
                    selectedFigure.render(g2, pX, pY, mouse.x, mouse.y, true, false);
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
                //draw possible turns
                if (possibleTurns != null) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    g2.setColor(Color.green);
                    for (Point p : possibleTurns) {
                        g2.fillRect((int) (p.x * pX + xOffSet), (int) (p.y * pY + yOffSet), (int) pX, (int) pY);
                    }
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                //draw impossible turns
                if (impossibleTurns != null) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    g2.setColor(Color.red);
                    for (Point p : impossibleTurns) {
                        g2.fillRect((int) (p.x * pX + xOffSet), (int) (p.y * pY + yOffSet), (int) pX, (int) pY);
                    }
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jToolBar1 = new javax.swing.JToolBar();
        jButton4 = new javax.swing.JButton();
        undo = new javax.swing.JButton();
        redo = new javax.swing.JButton();
        canvas = new java.awt.Canvas();
        menu_bar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chess");
        setIconImage(new ImageIcon(this.getClass().getResource("/Chess/src/img/queen_black.png")).getImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        body.setMaximumSize(new java.awt.Dimension(748, 500));
        body.setMinimumSize(new java.awt.Dimension(748, 500));

        jLabel1.setText("Data logger");

        jScrollPane1.setAutoscrolls(true);

        jList1.setMinimumSize(new java.awt.Dimension(259, 487));
        jList1.setOpaque(false);
        jList1.setPreferredSize(new java.awt.Dimension(259, 487));
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jToolBar1.setRollover(true);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Chess/src/img/toolbar/settings.png"))); // NOI18N
        jButton4.setToolTipText("Global settings");
        jButton4.setBorderPainted(false);
        jButton4.setMaximumSize(new java.awt.Dimension(27, 27));
        jButton4.setMinimumSize(new java.awt.Dimension(27, 27));
        jButton4.setPreferredSize(new java.awt.Dimension(27, 27));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        undo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Chess/src/img/toolbar/undo.png"))); // NOI18N
        undo.setToolTipText("Undo");
        undo.setBorderPainted(false);
        undo.setEnabled(false);
        undo.setMaximumSize(new java.awt.Dimension(27, 27));
        undo.setMinimumSize(new java.awt.Dimension(27, 27));
        undo.setPreferredSize(new java.awt.Dimension(27, 27));
        undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoActionPerformed(evt);
            }
        });
        jToolBar1.add(undo);

        redo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Chess/src/img/toolbar/redo.png"))); // NOI18N
        redo.setToolTipText("Redo");
        redo.setBorderPainted(false);
        redo.setEnabled(false);
        redo.setMaximumSize(new java.awt.Dimension(27, 27));
        redo.setMinimumSize(new java.awt.Dimension(27, 27));
        redo.setPreferredSize(new java.awt.Dimension(27, 27));
        redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoActionPerformed(evt);
            }
        });
        jToolBar1.add(redo);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        canvas.setMinimumSize(new java.awt.Dimension(500, 500));
        canvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                canvasMouseReleased(evt);
            }
        });
        canvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvasMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bodyLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jMenu1.setText("Game");

        jMenuItem1.setText("New game");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem6.setText("Export game animation");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);
        jMenu1.add(jSeparator1);

        jMenuItem11.setText("Undo");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("Redo");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        menu_bar.add(jMenu1);

        jMenu2.setText("Settings");

        jMenuItem5.setText("Global");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem13.setText("Default settings");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        menu_bar.add(jMenu2);

        jMenu4.setText("Tools");

        jMenuItem9.setText("Animation player");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuItem10.setText("AI info");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem10);

        menu_bar.add(jMenu4);

        jMenu3.setText("Help");

        jMenuItem8.setText("About");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        menu_bar.add(jMenu3);

        setJMenuBar(menu_bar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(body, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(body, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(this, "Do you want to create a new game?", "New game", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            main.gameManager.newGame(this.canvas.getSize(), this.size);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        main.global_setting.show(this);
    }//GEN-LAST:event_jMenuItem5ActionPerformed


    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        this.about.show(this);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        //export animation
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess animation", "ca"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                main.dataLogger.saveGameAnimation(fileChooser.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        //import animation
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess animation", "ca"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                new AnimationViewer().show(this, fileChooser.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void canvasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasMousePressed
        //vzít figuru 
        this.mouse = evt.getPoint();
        this.impossibleTurns = new ArrayList<>();   //delete all impossible turns
        Figure fig = main.gameManager.getFigureInLocation(
                evt.getPoint(),
                this.canvas.getSize(),
                this.size
        );
        if (fig != null && fig.isWhite() == main.gameManager.getActualColor() && !main.gameManager.playAINow()) {
            //kliknutí na figuru
            this.selectedFigure = fig;
            this.possibleTurns = fig.getPossibleTurns(main.gameManager.figures, null);
        }
    }//GEN-LAST:event_canvasMousePressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        main.global_setting.show(this);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoActionPerformed
        if (main.gameManager.player1.AIprocess != null) {
            main.gameManager.player1.AIprocess.destroy();
        }
        if (main.gameManager.player2.AIprocess != null) {
            main.gameManager.player2.AIprocess.destroy();
        }
        undo();
    }//GEN-LAST:event_undoActionPerformed

    private void redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoActionPerformed
        if (main.gameManager.player1.AIprocess != null) {
            main.gameManager.player1.AIprocess.destroy();
        }
        if (main.gameManager.player2.AIprocess != null) {
            main.gameManager.player2.AIprocess.destroy();
        }
        redo();
    }//GEN-LAST:event_redoActionPerformed

    private void canvasMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasMouseDragged
        this.mouse = evt.getPoint();
    }//GEN-LAST:event_canvasMouseDragged

    private void canvasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasMouseReleased
        if (this.selectedFigure != null) {
            //položit figuru
            Point p = main.gameManager.getBoardPosition(
                    evt.getPoint(),
                    this.canvas.getSize(),
                    this.size
            );
            boolean b = false;
            for (Point x : this.possibleTurns) {
                if (x.equals(p)) {
                    b = true;
                }
            }
            if (this.selectedFigure != null && b) {
                if (Tool.isInBoard(p)) {
                    ArrayList<Figure> errors = main.gameManager.turn(
                            this.selectedFigure,
                            p
                    );
                    if (errors != null) {
                        //if king is not protected

                        Point king_position = Tool.findFigure(
                                main.gameManager.figures,
                                main.gameManager.getActualColor(),
                                Figure.KING
                        ).getPosition();
                        this.impossibleTurns.add(king_position);

                        errors.forEach((err) -> {
                            this.impossibleTurns.add(err.getPosition());
                        });
                    }
                }
            }
            this.selectedFigure = null;
            this.possibleTurns = new ArrayList<>();
        }

    }//GEN-LAST:event_canvasMouseReleased

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jList1ValueChanged

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        undo();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        redo();
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FileOutputStream fout = null;
        try {
            //save config
            fout = new FileOutputStream("config.data");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            main.config.player_info_1 = new Config.PlayerInfoPack(main.gameManager.player1);
            main.config.player_info_2 = new Config.PlayerInfoPack(main.gameManager.player2);
            oos.writeObject(main.config);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        (new File("config.data")).delete();
        main.config = new Config();
        main.gameManager.player1 = new Player(main.gameManager, true);
        main.gameManager.player1.name = "Player 1";
        main.gameManager.player2 = new Player(main.gameManager, false);
        main.gameManager.player2.name = "Player 2";
        main.global_setting = new Global_setting();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        try {
            String[] choices = {main.gameManager.player1.name, main.gameManager.player2.name};
            String input = (String) JOptionPane.showInputDialog(null, "Choose player",
                    "AI analitics", JOptionPane.QUESTION_MESSAGE, null, // Use
                    // default
                    // icon
                    choices, // Array of choices
                    choices[0]); // Initial choice
            if (input.equals(main.gameManager.player1.name)) {
                if (main.gameManager.player1.isAIAllowed) {
                    AIInfo aia = new AIInfo(this, main.gameManager.player1);
                    aia.setVisible(true);
                } else {
                    JOptionPane.showConfirmDialog(this, "AI is not allowed for this player", "Error", JOptionPane.OK_OPTION);
                }
            }
            if (input.equals(main.gameManager.player2.name)) {
                if (main.gameManager.player2.isAIAllowed) {
                    AIInfo aia = new AIInfo(this, main.gameManager.player2);
                    aia.setVisible(true);
                } else {
                    JOptionPane.showConfirmDialog(this, "AI is not allowed for this player", "Error", JOptionPane.OK_OPTION);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(this, "AI file not found", "Error", JOptionPane.OK_CANCEL_OPTION);
            Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void undo() {
        if (main.dataLogger.history_index - 1 >= 0) {
            //změna hráčů na tahu
            main.gameManager.player1.isOnTurn = !main.gameManager.player1.isOnTurn;
            main.gameManager.player2.isOnTurn = !main.gameManager.player2.isOnTurn;

            main.dataLogger.history_index--;
            //nastaví data z historie
            main.gameManager.figures = Tool.clone(
                    main.dataLogger.history.get(main.dataLogger.history_index)
            );
            main.dataLogger.turns = (LinkedList) main.dataLogger.history_turns.get(main.dataLogger.history_index).clone();
            try {
                main.dataLogger.setModel(Tool.copy(main.dataLogger.history_model.get(main.dataLogger.history_index)));
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (main.dataLogger.history_index == 0) {
                this.undo.setEnabled(false);
            } else {
                this.undo.setEnabled(true);
            }
            if (main.dataLogger.history_index == main.dataLogger.history.size() - 1) {
                this.redo.setEnabled(false);
            } else {
                this.redo.setEnabled(true);
            }
        }
    }

    private void redo() {
        if (main.dataLogger.history_index + 1 < main.dataLogger.history.size()) {
            //změna hráčů na tahu
            main.gameManager.player1.isOnTurn = !main.gameManager.player1.isOnTurn;
            main.gameManager.player2.isOnTurn = !main.gameManager.player2.isOnTurn;

            main.dataLogger.history_index++;
            //nastaví data z historie
            main.gameManager.figures = Tool.clone(
                    main.dataLogger.history.get(main.dataLogger.history_index)
            );
            main.dataLogger.turns = (LinkedList) main.dataLogger.history_turns.get(main.dataLogger.history_index).clone();
            try {
                main.dataLogger.setModel(Tool.copy(main.dataLogger.history_model.get(main.dataLogger.history_index)));
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (main.dataLogger.history_index == 0) {
                this.undo.setEnabled(false);
            } else {
                this.undo.setEnabled(true);
            }
            if (main.dataLogger.history_index == main.dataLogger.history.size() - 1) {
                this.redo.setEnabled(false);
            } else {
                this.redo.setEnabled(true);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    public java.awt.Canvas canvas;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar menu_bar;
    public javax.swing.JButton redo;
    public javax.swing.JButton undo;
    // End of variables declaration//GEN-END:variables

}
