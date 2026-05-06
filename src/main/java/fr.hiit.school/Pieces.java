import javax.swing.ImageIcon;

public abstract class Pieces {
    protected String type; //
    protected Couleur couleur;
    protected int x, y;
    protected ImageIcon image;

    public Pieces(String type, Couleur couleur, int x, int y, ImageIcon image) {
        this.type = type;
        this.couleur = couleur;
        this.x = x;
        this.y = y;
        this.image = image;
    }


    public abstract boolean peutSeDeplacer(int newX, int newY, Pieces[][] echiquier);


        public void deplacer(int newX, int newY) {
            this.x = newX;
            this.y = newY;
        }


        public String getType() {
            return type; }
        public Couleur getCouleur() {
            return couleur; }
        public int getX() {
            return x; }
        public int getY() {
            return y; }
        public ImageIcon getImage() {
            return image; }
    }


