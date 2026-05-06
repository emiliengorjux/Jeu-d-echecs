import javax.swing.*;

public class Reine extends Pieces {
    public Reine(Couleur couleur, int x, int y, ImageIcon image) {
        super("Reine", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {

        if (newX != x && newY != y && Math.abs(newX - x) != Math.abs(newY - y)) {
            return false; // Ni ligne droite ni diagonale
        }


        int stepX = (newX == x) ? 0 : (newX > x) ? 1 : -1;
        int stepY = (newY == y) ? 0 : (newY > y) ? 1 : -1;

        int currentX = x + stepX;
        int currentY = y + stepY;

        while (currentX != newX || currentY != newY) {
            if (echiquier[currentX][currentY] != null) {
                return false; // Obstacle
            }
            currentX += stepX;
            currentY += stepY;
        }

        if (echiquier[newX][newY] != null && echiquier[newX][newY].getCouleur() == couleur) {
            return false; // Cases occupée par pieces alliées
        }

        return true;
    }
}
