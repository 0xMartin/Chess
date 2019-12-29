/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Marti
 */
public class main {

    public static Form form;
    public static GameManager gameManager;
    public static DataLogger dataLogger;
    public static Global_setting global_setting;
    public static FigureChanger figureChanger;
    public static Config config;
    public static final String version = "4.0";

    public main() {
        main.config = new Config();
        main.form = new Form();
        //set Wimdows design
        try {
            UIManager.setLookAndFeel(
                    UIManager.getInstalledLookAndFeels()[3].getClassName());
            SwingUtilities.updateComponentTreeUI(main.form);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        main.gameManager = new GameManager();
        main.dataLogger = new DataLogger(main.form.jList1);
        main.figureChanger = new FigureChanger();
    }

    private void init() {

        FileInputStream fin = null;
        try {
            //read config
            fin = new FileInputStream("config.data");
            ObjectInputStream ois = new ObjectInputStream(fin);
            main.config = (Config) ois.readObject();
            //global setting
            main.gameManager.player1.name = main.config.player_info_1.name;
            main.gameManager.player1.DEPTH = main.config.player_info_1.depth;
            main.gameManager.player1.isAIAllowed = main.config.player_info_1.isAIAllowed;
            main.gameManager.player1.AI_PATH = main.config.player_info_1.AI_PATH;
            main.gameManager.player2.name = main.config.player_info_2.name;
            main.gameManager.player2.DEPTH = main.config.player_info_2.depth;
            main.gameManager.player2.isAIAllowed = main.config.player_info_2.isAIAllowed;
            main.gameManager.player2.AI_PATH = main.config.player_info_2.AI_PATH;
            fin.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        main.global_setting = new Global_setting();

        //init form
        main.form.setVisible(true);
        this.form.init();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        main sachy = new main();
        sachy.init();
    }

}
