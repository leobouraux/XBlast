package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

/**
 * 1/5
 * Une case.
 * @author Léo Bouraux (257368)
 *
 */

public final class Cell {
    public static final int COLUMNS = 15;
    public static final int ROWS = 13;
    public static final int COUNT = COLUMNS*ROWS;
    private final int x;
    private final int y;
    
    /**
     *  La méthode unmodifiableList de Collections permet de rendre le
     *  tableau dynamique retourné par rowMajorOrder non modifiable
     */
    public static final List <Cell> ROW_MAJOR_ORDER = Collections.unmodifiableList(rowMajorOrder());
    public static final List <Cell> SPIRAL_ORDER = Collections.unmodifiableList(spiralOrder());
    
    
    /**
     * @param x, y
     *      (La coordonnée x et y de la position de la cellule)
     * 
     * Construit une cellule
     */
    public Cell(int x, int y) {
        this.x= Math.floorMod(x, COLUMNS);
        this.y= Math.floorMod(y, ROWS);
    }
   
    
    /**
     * @return la coordonnée x normalisée de la case
     */
    public int x() {
        return x;
    }
    
    
    /**
     * @return la coordonnée y normalisée de la case
     */
    public int y() {
        return y;
    }
    
 
    /**
     * @return l'index de la case dans l'ordre de lecture
     */
    public int rowMajorIndex() {
       return y*COLUMNS+x; 
    }
    
    
    /**
     * @param dir (La direction courante)
     *          
     * @return la case voisine, en fontcion de la direction donnée
     */
    public Cell neighbor(Direction dir) {
        switch (dir) {
        case N : return new Cell (x, y-1);
        case S : return new Cell (x, y+1);
        case E : return new Cell (x+1, y);
        case W : return new Cell (x-1, y);
        default : return this;
        }
    }
    

    /**
     * @param that
     *          l'objet courant
     * @return vrai ssi l'objet that est une case et ses coordonnées 
     * normalisées sont identiques à celles de la case réceptrice
     * 
     * @see : Nouvelle utilisation de "floorMod" pour s'assurer que c'est
     * bien modulo si jamais une sous-classe override le constructeur 
     */ 
    @Override
    public boolean equals(Object that) {
        return (that instanceof Cell 
                && Math.floorMod(x-((Cell) that).x(), COLUMNS)==0 
                && Math.floorMod(y-((Cell) that).y(), ROWS)==0);
    }
    
   
    /**
     * @return une représentation textuelle de la case
     */
    @Override
    public String toString() {
        return "("+ x +","+ y +")";
    }
    
    
    /**
     * @return  une redéfinition de la méthode hashCode, qui soit compatible avec la redéfinition
     *   de la méthode equals de cette classe
     */
    @Override
    public int hashCode() {
        return this.rowMajorIndex();
    }

    
    private static ArrayList<Cell> rowMajorOrder() {
        ArrayList<Cell> RMOrder = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                RMOrder.add(new Cell(j, i));
            } 
        }
        return RMOrder;
    }

    
    private static ArrayList<Cell> spiralOrder() {
        ArrayList<Cell> SOrder = new ArrayList<Cell>();
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> iy = new ArrayList<Integer>();
        boolean horizontal = true;
        for (int i = 0; i < COLUMNS; i++) {
            ix.add(i);
        }
        for (int j = 0; j < ROWS; j++) {
            iy.add(j);
        }
        ArrayList<Integer> i1;
        ArrayList<Integer> i2;
        int c2;
        while (!ix.isEmpty() && !iy.isEmpty()) {
            if (horizontal) {
                i1 = ix;
                i2 = iy;
            } else {
                i1 = iy;
                i2 = ix;
            }
            c2 = i2.remove(0);
            for (Integer c1 : i1) {
                SOrder.add(horizontal ? new Cell(c1, c2) : new Cell(c2, c1));
            }
            Collections.reverse(i1);
            horizontal = !horizontal;
        }
        return SOrder;
    }


}
