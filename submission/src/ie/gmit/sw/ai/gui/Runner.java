package ie.gmit.sw.ai.gui;

/**
 * Created by Ross Byrne on 21/04/17.
 *
 * Runner to test GUI popups
 */
public class Runner {

    // testing the dialogs
    public static void main(String[] args) {

        System.out.println(StartGameJOptionPane.display());

        GameOverJOptionPane.display("Player Died! Game Over!");

        GameOverJOptionPane.display("Boss Defeated! Game Over!");

    } // main()
} // class
