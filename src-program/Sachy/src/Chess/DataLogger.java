/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import Chess.figure.Figure;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Marti
 */
public class DataLogger {

    public DefaultListModel model;

    public LinkedList<ArrayList<Figure>> history;
    public LinkedList<DefaultListModel> history_model;
    public LinkedList<LinkedList<DataEntry>> history_turns;
    public int history_index = 0;

    public LinkedList<DataEntry> turns;
    private final JList component;
    private final int def_height;

    public DataLogger(JList component) {
        component.setCellRenderer(new CellRenderer());
        this.component = component;
        this.def_height = this.component.getHeight();
        this.model = new DefaultListModel();
        setModel(this.model);
        this.turns = new LinkedList<>();

        this.history = new LinkedList<>();
        this.history_model = new LinkedList<>();
        this.history_turns = new LinkedList<>();
    }

    public void addToHistory() throws CloneNotSupportedException {
        boolean b = false;
        while (this.history_index < this.history.size() - 1) {
            this.history.removeLast();
            this.turns.removeLast();
            this.history_model.removeLast();
            //this.model.remove(this.model.size() - 1);
            b = true;
        }
        this.history.add(Tool.clone(main.gameManager.figures));
        this.history_model.add(Tool.copy(this.model));
        this.history_turns.add((LinkedList) this.turns.clone());
        this.history_index = this.history.size() - 1;

        if (b) {
            this.model = this.history_model.get(this.history_model.size() - 1);
            setModel(this.model);
        }

        main.form.redo.setEnabled(false);
        if (this.history_index - 1 >= 0) {
            main.form.undo.setEnabled(true);
        } else {
            main.form.undo.setEnabled(false);
        }
    }

    public void setModel(DefaultListModel model) {
        this.model = model;
        this.component.setModel(this.model);
    }

    public void clear() {
        this.model.clear();
        this.turns.clear();
        resizeHight();
    }

    public void figureChanged(Figure figure) {
        String img = "/Chess/src/img/";
        String type = "";
        switch (figure.getType()) {
            case Figure.BISHOP:
                img += "bishop_";
                type = "Bishop";
                break;
            case Figure.KING:
                img += "king_";
                type = "King";
                break;
            case Figure.KNIGHT:
                img += "knight_";
                type = "Knight";
                break;
            case Figure.PAWN:
                img += "pawn_";
                type = "Pawn";
                break;
            case Figure.QUEEN:
                img += "queen_";
                type = "Queen";
                break;
            case Figure.ROOK:
                img += "rook_";
                type = "Rook";
                break;
        }
        DataEntry d = new DataEntry("Changed to " + type, img, Color.GREEN);

        this.turns.add(d);
        this.model.addElement(d);
        resizeHight();
    }

    public void addText(String text) {
        DataEntry d = new DataEntry(text, "", Color.BLACK);
        this.model.addElement(d);
        resizeHight();
    }

    private String getImageForFigure(Figure fig) {
        String img = "/Chess/src/img/";
        switch (fig.getType()) {
            case Figure.BISHOP:
                img += "bishop_";
                break;
            case Figure.KING:
                img += "king_";
                break;
            case Figure.KNIGHT:
                img += "knight_";
                break;
            case Figure.PAWN:
                img += "pawn_";
                break;
            case Figure.QUEEN:
                img += "queen_";
                break;
            case Figure.ROOK:
                img += "rook_";
                break;
        }
        if (fig.isWhite()) {
            img += "white.png";
        } else {
            img += "black.png";
        }
        return img;
    }

    public void removeFigure(Figure fig) {
        String name = "";
        switch (fig.getType()) {
            case Figure.BISHOP:
                name = "bishop";
                break;
            case Figure.KING:
                name = "king";
                break;
            case Figure.KNIGHT:
                name = "knight";
                break;
            case Figure.PAWN:
                name = "pawn";
                break;
            case Figure.QUEEN:
                name = "queen";
                break;
            case Figure.ROOK:
                name = "rook";
                break;
        }

        DataEntry d = new DataEntry(
                name + " has been removed",
                getImageForFigure(fig),
                Color.RED
        );
        this.turns.add(d);
        this.model.addElement(d);

        resizeHight();
    }

    public void addTurn(Figure fig, Point last) {
        DataEntry d = new DataEntry(
                Tool.pointToXX(last) + " -> " + Tool.pointToXX(fig.getPosition()),
                getImageForFigure(fig),
                Color.BLACK
        );
        this.turns.add(d);
        this.model.addElement(d);
        resizeHight();
    }

    private void resizeHight() {
        int height = this.turns.size() * 25;
        if (height > def_height) {
            component.setPreferredSize(new Dimension(component.getWidth(), height));
        }
    }

    public class DataEntry implements Cloneable {

        private final String title;
        private final String imagePath;
        private ImageIcon image;
        private final Color color;
        private String time;

        @Override
        public Object clone() throws CloneNotSupportedException {
            return (DataEntry) super.clone();
        }

        public DataEntry(String title, String imagePath, Color c) {
            this.title = title;
            this.imagePath = imagePath;
            this.color = c;
            Date d = new Date();
            this.time = title + " " + ((d.getHours() + "").length() == 1 ? 10 + d.getHours() : d.getHours())
                    + ":" + ((d.getMinutes() + "").length() == 1 ? 10 + d.getMinutes() : d.getMinutes())
                    + ":" + ((d.getSeconds() + "").length() == 1 ? 10 + d.getSeconds() : d.getSeconds());
        }

        public String getTitle() {
            return title;
        }

        public Color getColor() {
            return this.color;
        }

        public ImageIcon getImage() {
            if (image == null) {
                image = new ImageIcon(
                        Tool.createResizedCopy(
                                new ImageIcon(this.getClass().getResource(imagePath)).getImage(),
                                25,
                                25,
                                false
                        )
                );
            }
            return image;
        }

        public String toString() {
            return title;
        }
    }

    private class CellRenderer extends JLabel implements ListCellRenderer {

        private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

        public CellRenderer() {
            setOpaque(true);
            setIconTextGap(12);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            DataEntry entry = (DataEntry) value;
            setText(entry.getTitle());
            setIcon(entry.getImage());
            if (isSelected) {
                setBackground(HIGHLIGHT_COLOR);
                setForeground(Color.white);
            } else {
                setBackground(Color.white);
                setForeground(entry.getColor());
            }
            setToolTipText(entry.time);
            return this;
        }
    }

    public void saveGameAnimation(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file + ".ca"));
        for (DataEntry turn : this.turns) {
            writer.write(turn.getTitle());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

}
