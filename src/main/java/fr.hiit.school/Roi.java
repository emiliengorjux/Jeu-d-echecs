import javax.swing.*;

public class Roi extends Pieces {
    public Roi(Couleur couleur, int x, int y, ImageIcon image) {
        super("Roi", couleur, x, y, image);
    }

    @Override
    public boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier) {
        if (Math.abs(newX - x) > 1 || Math.abs(newY - y) > 1) {
            return false;
        }

        // Vérifier si la case de destination est sûre
        Pieces[][] piecesCopie = new Pieces[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                piecesCopie[col][row] = echiquier[col][row];
            }
        }

        // Simuler le déplacement
        piecesCopie[newX][newY] = piecesCopie[x][y];
        piecesCopie[x][y] = null;

        // Vérifier si le roi est encore en échec après le déplacement
        Echiquier echiquierTemp = new Echiquier();
        return !echiquierTemp.estEnEchec(couleur, piecesCopie);
    }
}
