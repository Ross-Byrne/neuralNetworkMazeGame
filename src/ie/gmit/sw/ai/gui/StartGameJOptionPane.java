package ie.gmit.sw.ai.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Created by Ross Byrne on 21/04/17.
 *
 * Popup to decide if the game is player controlled or AI controlled.
 *
 */

public class StartGameJOptionPane extends JDialog {

    // display the dialog box
    public static int display() {

        int selected = 0;

        String[] items = {"AI Controlled", "Player Controlled"};
        JComboBox combo = new JComboBox(items);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("The game can be AI Controlled or Player Controlled."));
        panel.add(new JLabel("Choose from the drop down how you want the game to play!"));
        panel.add(new JLabel(""));
        panel.add(combo);
        panel.add(new JLabel(""));

        // show dialog box
        int result = JOptionPane.showConfirmDialog(null, panel, "The Maze Game",
                JOptionPane.DEFAULT_OPTION);

        // save selected option
        selected = combo.getSelectedIndex();

        return selected;
    } // display()

} // class
