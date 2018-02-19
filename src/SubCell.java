package ch.epfl.xblast;

/**
 * 1/5
 * Une sous-case.
 * @author Léo Bouraux (257368)
 *
 */

public final class SubCell {

    private final int x;
    private final int y;
    private static final int SUBCOLUMNS = 16;
    private static final int SUBROWS = 16;
 
    
    /**
     * @param x, y
     *      (La coordonnée x et y de la position de la cellule)
     * 
     * Construit une sous-cellule
     */
    public SubCell(int x, int y) {
        this.x= Math.floorMod(x, Cell.COLUMNS*SUBCOLUMNS);
        this.y= Math.floorMod(y, Cell.ROWS*SUBROWS);
    }
    
    
    /**
     * @param cell (La case courante)
     *          
     * @return  la sous-case centrale de la case donnée
     */
    public static SubCell centralSubCellOf (Cell cell) {
        int X;
        int Y;
        X=cell.x()*SUBCOLUMNS+SUBCOLUMNS/2;
        Y=cell.y()*SUBROWS+SUBROWS/2;
        return new SubCell(X, Y);
    }


    /**
     * @return la coordonnée x normalisée de la sous-case
     */
    public int x() {
        return x;
    }

    
    /**
     * @return la coordonnée y normalisée de la sous-case
     */
    public int y() {
        return y;
    }
    

    /**
     * @return le plus petit nombre de sous-cases qu'il faut parcourir
     * pour aller à la sous-case centrale la plus proche
     */
    public int distanceToCentral() {
        return Math.abs(this.x()-(centralSubCellOf(new Cell(this.x()/SUBCOLUMNS, this.y()/SUBROWS)).x()))
             + Math.abs(this.y()-(centralSubCellOf(new Cell(this.x()/SUBCOLUMNS, this.y()/SUBROWS)).y()));
    }
    
    
    /**
     * @return vrai si et seulement si la sous-case est une sous-case centrale
     */
    public  boolean isCentral() {
        return this.equals(centralSubCellOf(this.containingCell()));
    }

    
    /**
     * @param d (La direction courante)
     * 
     * @return la sous-case voisine
     */
    public SubCell neighbor(Direction d) {
        switch (d) {
        case N : return new SubCell (x, y-1);
        case S : return new SubCell (x, y+1);
        case E : return new SubCell (x+1, y);
        case W : return new SubCell (x-1, y);
        default : return this;
        }
    }
    
    
    /**
     * @return la case contenant cette sous-case
     */
    public Cell containingCell() {
        return new Cell (this.x()/SUBCOLUMNS, this.y()/SUBROWS);
    }
    
    
    /**
     * @param that (L'objet courant)
     *          
     * @return vrai ssi l'objet that est une sous-case et ses coordonnées 
     * normalisées sont identiques à celles de la sous-case réceptrice
     */ 
    @Override
    public boolean equals(Object that) {
        return (that instanceof SubCell 
                && x==((SubCell) that).x() 
                && y==((SubCell) that).y());
    }
    
    
    /**
     * @return une représentation textuelle de la case
     */
    @Override
    public String toString() {
        return "("+ x +","+ y +")";
    }
    
    
    /**
     * @return  une redéfinition de la méthode hashCode, qui est compatible avec la redéfinition
     *   de la méthode equals de cette classe
     */
    @Override
    public int hashCode() {
        return y*SUBCOLUMNS*Cell.COLUMNS + x; 
    }
    
    
}
