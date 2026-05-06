import javax.swing.*;

public class Tours extends Pieces {
    public Tours(Couleur couleur, int x, int y, ImageIcon image) {
        super("Tour", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {

        if (newX != x && newY != y) {
            return false;
        }

        // Ici on vérifie que le "chemin" est dispo
        int stepX = (newX == x) ? 0 : (newX > x) ? 1 : -1;
        int stepY = (newY == y) ? 0 : (newY > y) ? 1 : -1;

        int currentX = x + stepX;
        int currentY = y + stepY;

        while (currentX != newX || currentY != newY) {
            if (echiquier[currentX][currentY] != null) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        if (echiquier[newX][newY] != null && echiquier[newX][newY].getCouleur() == couleur) {
            return false; // Case occupée par une pieces de la meme couleur donc impossible
        }

        return true;
    }
}
