package fr.hiit.school;

import javax.swing.*;

public class Application extends JFrame {

    public Application() {
        setTitle("Jeu d'Échecs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Application());
    }
}