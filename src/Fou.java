import javax.swing.*;

public class Fou extends Pieces {
    public Fou(Couleur couleur, int x, int y, ImageIcon image) {
        super("Fou", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {
        // Vérifier si les coordonnées sont valides
        if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
            return false;
        }

        // Vérifier si le déplacement est diagonal
        if (Math.abs(newX - x) != Math.abs(newY - y)) {
            return false;
        }

        int stepX = (newX > x) ? 1 : -1;
        int stepY = (newY > y) ? 1 : -1;

        int currentX = x + stepX;
        int currentY = y + stepY;

        // Vérifier si le chemin est libre
        while (currentX != newX || currentY != newY) {
            if (echiquier[currentX][currentY] != null) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        // Vérifier si la case de destination contient une pièce de la même couleur
        if (echiquier[newX][newY] != null && echiquier[newX][newY].getCouleur() == couleur) {
            return false;
        }

        return true;
    }
}

