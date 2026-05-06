import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;

public class Echiquier extends JPanel {
    private ImageIcon[][] cases = new ImageIcon[8][8];
    private ImageIcon caseBlanche;
    private ImageIcon caseNoire;
    private ImageIcon caseDisponible; // Image pour les cases disponibles
    private Pieces[][] pieces = new Pieces[8][8];
    private Pieces pieceSelectionnee = null;
    private int[] positionSelectionnee = {-1, -1};
    private Couleur tourActuel;
    private Couleur couleurJoueur;
    private JLabel tourLabel;

    public Echiquier() {
        setLayout(new BorderLayout());

        // Ajout d'un label pour afficher le tour actuel
        tourLabel = new JLabel("Tour actuel : Blancs", SwingConstants.CENTER);
        add(tourLabel, BorderLayout.NORTH);

        JPanel echiquierPanel = new JPanel(new GridLayout(8, 8));
        add(echiquierPanel, BorderLayout.CENTER);

        choisirCouleur();
        chargerImagesCases();
        chargerImagesDisponibles(); // Charger l'image pour les cases disponibles
        initialiserPieces();
        tourActuel = Couleur.BLANC; // Les blancs commencent toujours
        afficherEchiquier(echiquierPanel);
    }

    private void choisirCouleur() {
        String[] options = {"Blancs", "Noirs"};
        int choix = JOptionPane.showOptionDialog(
                this,
                "Choisissez votre couleur :",
                "Choix de la couleur",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        couleurJoueur = (choix == 0) ? Couleur.BLANC : Couleur.NOIR;
    }

    private void chargerImagesDisponibles() {
        // Créer une image pour les cases disponibles
        BufferedImage img = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(0, 255, 0, 100)); // Vert transparent
        g2d.fillRect(0, 0, 70, 70);
        g2d.dispose();
        caseDisponible = new ImageIcon(img);
    }

    private void changerTour() {
        tourActuel = (tourActuel == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
        tourLabel.setText("Tour actuel : " + (tourActuel == Couleur.BLANC ? "Blancs" : "Noirs"));

        // Vérifier si le roi est en échec
        if (estEnEchec(tourActuel)) {
            tourLabel.setText("ÉCHEC ! Tour actuel : " + (tourActuel == Couleur.BLANC ? "Blancs" : "Noirs"));
        }
    }

    private void chargerImagesCases() {
        caseBlanche = chargerImage("/CaseBlanche.png");
        caseNoire = chargerImage("/CaseNoir.png");

        if (caseBlanche == null || caseNoire == null) {
            JOptionPane.showMessageDialog(this, "Impossible de charger les images des cases.", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Initialiser les cases
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cases[col][row] = ((row + col) % 2 == 0) ? caseBlanche : caseNoire;
            }
        }
    }

    private ImageIcon chargerImage(String chemin) {
        URL url = getClass().getResource(chemin);
        if (url != null) {
            System.out.println("Image trouvée : " + chemin);
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } else {
            System.err.println("Fichier non trouvé : " + chemin);
            return null;
        }
    }

    private ImageIcon chargerImagePiece(String nomFichier) {
        String chemin = "/Pieces/" + nomFichier;
        ImageIcon icon = chargerImage(chemin);
        if (icon == null) {
            System.err.println("Échec du chargement de l'image : " + chemin);
            return null;
        }
        return icon;
    }

    private void montrerDeplacementsPossibles(int col, int row) {
        Pieces piece = pieces[col][row];
        if (piece != null) {
            for (int newRow = 0; newRow < 8; newRow++) {
                for (int newCol = 0; newCol < 8; newCol++) {
                    if (piece.peutSeDeplacer(newCol, newRow, pieces)) {
                        boolean roiEnEchec = estEnEchec(piece.getCouleur());
                        if (!roiEnEchec || sortDuEchec(col, row, newCol, newRow)) {
                            cases[newCol][newRow] = caseDisponible;
                        }
                    }
                }
            }
        }
    }


    private void reinitialiserCases() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cases[col][row] = ((row + col) % 2 == 0) ? caseBlanche : caseNoire;
            }
        }
    }

    // Méthode pour vérifier si le roi est en échec
    private boolean estEnEchec(Couleur couleur) {
        return estEnEchec(couleur, pieces);
    }

    // Méthode statique pour vérifier si le roi est en échec avec un tableau de pièces donné
    public static boolean estEnEchec(Couleur couleur, Pieces[][] piecesCopie) {
        int roiX = -1, roiY = -1;
        // Trouver la position du roi
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (piecesCopie[col][row] != null && piecesCopie[col][row].getType().equals("Roi") && piecesCopie[col][row].getCouleur() == couleur) {
                    roiX = col;
                    roiY = row;
                    break;
                }
            }
        }

        // Vérifier si le roi est en échec
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (piecesCopie[col][row] != null && piecesCopie[col][row].getCouleur() != couleur) {
                    if (piecesCopie[col][row].peutSeDeplacer(roiX, roiY, piecesCopie)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Méthode pour vérifier si un déplacement sort le roi de l'échec
    private boolean sortDuEchec(int xDepart, int yDepart, int xArrivee, int yArrivee) {
        Pieces[][] piecesCopie = new Pieces[8][8];
        // Copier l'état actuel des pièces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                piecesCopie[col][row] = pieces[col][row];
            }
        }

        // Simuler le déplacement
        piecesCopie[xArrivee][yArrivee] = piecesCopie[xDepart][yDepart];
        piecesCopie[xDepart][yDepart] = null;

        // Vérifier si le roi est encore en échec après le déplacement
        return !estEnEchec(piecesCopie[xArrivee][yArrivee].getCouleur(), piecesCopie);
    }

    // Méthode pour vérifier si le roi est en échec et mat
    private boolean estEchecEtMat(Couleur couleur) {
        if (!estEnEchec(couleur)) {
            return false;
        }

        // Vérifier si un déplacement peut sortir le roi de l'échec
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[col][row] != null && pieces[col][row].getCouleur() == couleur) {
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            if (pieces[col][row].peutSeDeplacer(newCol, newRow, pieces)) {
                                if (sortDuEchec(col, row, newCol, newRow)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // Méthode pour vérifier si un déplacement est valide en tenant compte de l'échec
    private boolean estDeplacementValide(int xDepart, int yDepart, int xArrivee, int yArrivee) {
        Pieces piece = pieces[xDepart][yDepart];
        if (piece == null || !piece.peutSeDeplacer(xArrivee, yArrivee, pieces)) {
            return false;
        }

        boolean roiEnEchec = estEnEchec(piece.getCouleur());
        if (!roiEnEchec) {
            return true;
        }

        return sortDuEchec(xDepart, yDepart, xArrivee, yArrivee);
    }

    private void initialiserPieces() {
        // Pions blancs
        for (int col = 0; col < 8; col++) {
            pieces[col][6] = new Pions(Couleur.BLANC, col, 6, chargerImagePiece("w-berserker-pawn.png"));
        }
        // Pions noirs
        for (int col = 0; col < 8; col++) {
            pieces[col][1] = new Pions(Couleur.NOIR, col, 1, chargerImagePiece("b-berserker-pawn.png"));
        }
        // Tours blanches
        pieces[0][7] = new Tours(Couleur.BLANC, 0, 7, chargerImagePiece("w-berserker-rook.png"));
        pieces[7][7] = new Tours(Couleur.BLANC, 7, 7, chargerImagePiece("w-berserker-rook.png"));
        // Tours noires
        pieces[0][0] = new Tours(Couleur.NOIR, 0, 0, chargerImagePiece("b-berserker-rook.png"));
        pieces[7][0] = new Tours(Couleur.NOIR, 7, 0, chargerImagePiece("b-berserker-rook.png"));
        // Cavaliers blancs
        pieces[1][7] = new Cavaliers(Couleur.BLANC, 1, 7, chargerImagePiece("w-berserker-knight.png"));
        pieces[6][7] = new Cavaliers(Couleur.BLANC, 6, 7, chargerImagePiece("w-berserker-knight.png"));
        // Cavaliers noirs
        pieces[1][0] = new Cavaliers(Couleur.NOIR, 1, 0, chargerImagePiece("b-berserker-knight.png"));
        pieces[6][0] = new Cavaliers(Couleur.NOIR, 6, 0, chargerImagePiece("b-berserker-knight.png"));
        // Fous blancs
        pieces[2][7] = new Fou(Couleur.BLANC, 2, 7, chargerImagePiece("w-berserker-bishop.png"));
        pieces[5][7] = new Fou(Couleur.BLANC, 5, 7, chargerImagePiece("w-berserker-bishop.png"));
        // Fous noirs
        pieces[2][0] = new Fou(Couleur.NOIR, 2, 0, chargerImagePiece("b-berserker-bishop.png"));
        pieces[5][0] = new Fou(Couleur.NOIR, 5, 0, chargerImagePiece("b-berserker-bishop.png"));
        // Dames
        pieces[3][7] = new Reine(Couleur.BLANC, 3, 7, chargerImagePiece("w-berserker-queen.png"));
        pieces[3][0] = new Reine(Couleur.NOIR, 3, 0, chargerImagePiece("b-berserker-queen.png"));
        // Rois
        pieces[4][7] = new Roi(Couleur.BLANC, 4, 7, chargerImagePiece("w-berserker-king.png"));
        pieces[4][0] = new Roi(Couleur.NOIR, 4, 0, chargerImagePiece("b-berserker-king.png"));
    }

    private void afficherEchiquier(JPanel echiquierPanel) {
        echiquierPanel.removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLayeredPane layeredPane = new JLayeredPane();
                layeredPane.setPreferredSize(new Dimension(70, 70));

                // Ajouter la case
                JLabel caseLabel = new JLabel(cases[col][row]);
                caseLabel.setBounds(0, 0, 70, 70);
                layeredPane.add(caseLabel, JLayeredPane.DEFAULT_LAYER);

                // Ajouter la pièce si elle existe
                if (pieces[col][row] != null && pieces[col][row].getImage() != null) {
                    JLabel pieceLabel = new JLabel(pieces[col][row].getImage());
                    pieceLabel.setBounds(0, 0, 70, 70);
                    layeredPane.add(pieceLabel, JLayeredPane.PALETTE_LAYER);
                }

                int finalCol = col;
                int finalRow = row;
                layeredPane.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        gererClic(finalCol, finalRow);
                    }
                });

                echiquierPanel.add(layeredPane);
            }
        }
        echiquierPanel.revalidate();
        echiquierPanel.repaint();
    }

    private void gererClic(int col, int row) {
        if (pieceSelectionnee == null) {
            if (col >= 0 && col < 8 && row >= 0 && row < 8 && pieces[col][row] != null && pieces[col][row].getCouleur() == tourActuel) {
                pieceSelectionnee = pieces[col][row];
                positionSelectionnee = new int[]{col, row};
                System.out.println("Pièce sélectionnée : " + pieceSelectionnee.getType() + " en (" + col + ", " + row + ")");

                // Montrer les déplacements possibles
                montrerDeplacementsPossibles(col, row);
                afficherEchiquier((JPanel) getComponent(1));
            }
        } else {
            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                if (estDeplacementValide(positionSelectionnee[0], positionSelectionnee[1], col, row)) {
                    pieces[col][row] = pieceSelectionnee;
                    pieces[positionSelectionnee[0]][positionSelectionnee[1]] = null;
                    pieceSelectionnee.deplacer(col, row);

                    // Vérifier si le roi adverse est en échec et mat
                    Couleur couleurAdverse = (tourActuel == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
                    if (estEchecEtMat(couleurAdverse)) {
                        JOptionPane.showMessageDialog(this, "Échec et mat ! " + (tourActuel == Couleur.BLANC ? "Blancs" : "Noirs") + " gagnent !", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }

                    System.out.println("Pièce déplacée vers (" + col + ", " + row + ")");
                    changerTour();
                } else {
                    System.out.println("Déplacement invalide.");
                }
            }

            // Réinitialiser les cases
            reinitialiserCases();
            pieceSelectionnee = null;
            positionSelectionnee = new int[]{-1, -1};
            afficherEchiquier((JPanel) getComponent(1));
        }
    }


    public boolean deplacerPiece(int xDepart, int yDepart, int xArrivee, int yArrivee) {
        Pieces piece = pieces[xDepart][yDepart];
        if (piece != null && estDeplacementValide(xDepart, yDepart, xArrivee, yArrivee)) {
            pieces[xArrivee][yArrivee] = piece;
            pieces[xDepart][yDepart] = null;
            piece.deplacer(xArrivee, yArrivee);

            // Vérifier si le roi adverse est en échec et mat
            Couleur couleurAdverse = (tourActuel == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
            if (estEchecEtMat(couleurAdverse)) {
                JOptionPane.showMessageDialog(this, "Échec et mat ! " + (tourActuel == Couleur.BLANC ? "Blancs" : "Noirs") + " gagnent !", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

            changerTour();
            afficherEchiquier((JPanel) getComponent(1));
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Échiquier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new Echiquier());
        frame.setVisible(true);
    }
}
