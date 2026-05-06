import javax.swing.*;

public class Cavaliers extends Pieces {
    public Cavaliers(Couleur couleur, int x, int y, ImageIcon image) {
        super("Cavalier", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {

        int deltaX = Math.abs(newX - x);
        int deltaY = Math.abs(newY - y);

        // Vérifie si le déplacement est en "L"
        if ((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)) {
            // Vérifie si la case ciblé est dispo ou occupé par une pieces de meme couleurs
            if (echiquier[newX][newY] == null || echiquier[newX][newY].getCouleur() != couleur) {
                return true;
            }
        }
        return false;
    }
}
