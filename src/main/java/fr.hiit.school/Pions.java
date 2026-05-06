import javax.swing.*;

public class Pions extends Pieces {
    public Pions(Couleur couleur, int x, int y, ImageIcon image) {
        super("Pion", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {
        int direction = (couleur == Couleur.BLANC) ? -1 : 1; // Les blancs montent, les noirs descendent

        // Déplacement vers l'avant d'une case
        if (newX == x && newY == y + direction && echiquier[newX][newY] == null) {
            return true;
        }

        // Double déplacement initial
        if ((y == 6 && couleur == Couleur.BLANC) || (y == 1 && couleur == Couleur.NOIR)) {
            if (newX == x && newY == y + 2 * direction && echiquier[newX][newY] == null && echiquier[newX][y + direction] == null) {
                return true;
            }
        }

        // Capture en diagonale
        if (Math.abs(newX - x) == 1 && newY == y + direction && echiquier[newX][newY] != null && echiquier[newX][newY].getCouleur() != couleur) {
            return true;
        }

        return false;
    }
}
