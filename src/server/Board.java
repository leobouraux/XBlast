package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;

/**
 * 2
 * Un plateau de jeu.
 * @author Léo Bouraux (257368)
 *
 */
public final class Board {
    
    private final List<Sq<Block>> board;
    
    /**
     * @param blocks (Des blocs)
     *          
     * Construit un plateau avec les séquences de blocs données
     * @throws IllegalArgumentException si la liste donnée n'est pas conforme
     * (si et seulement si elle ne contient pas 195 éléments)
     */
    public Board(List<Sq<Block>> blocks) {
        if (blocks==null || blocks.size()!= Cell.COUNT)
            throw new IllegalArgumentException();
        else
            this.board=Collections.unmodifiableList(new ArrayList<Sq<Block>>(blocks));
    }
    
    
    /**
     * @param matrix, rows, columns
     *          (Une "matrice", ses lignes, ses colonnes)
     *          
     * Vérifie si la matrice donnée est bien conforme
     * 
     * @throws IllegalArgumentException si la liste reçue n'est pas constituée
     *      de #rows éléments de #columns blocs chacune ou si elle est vide
     *      
     * @see : Cette méthode a été créée pour éviter la duplication de code
     */
    private static void checkBlockMatrix(List<List<Block>> matrix, int rows, int columns){
        if (matrix==null || matrix.size()!=rows){
            throw new IllegalArgumentException();
        }
        for (List<Block> list : matrix) {
            if (list==null || list.size()!=columns)
                throw new IllegalArgumentException();
        }
    }

    
    /**
     * @param rows (Les colonnes)
     * 
     * @return un plateau constant avec la matrice de blocs donnée
     */
    public static Board ofRows(List<List<Block>> rows) {
        checkBlockMatrix(rows, Cell.ROWS, Cell.COLUMNS);
        List<Sq<Block>> blocks = new ArrayList<Sq<Block>>();
        for (List<Block> row : rows) {
            row.forEach(block -> blocks.add(Sq.constant(block)));
        }
        return new Board(blocks);
    }
    
    
    /**
     * @param innerBlocks (Les blocs intérieurs)
     * 
     * @return un plateau muré
     * @see : *les dimensions de la matrice sont vérifiées par la méthode "checkBlockMatrix"
     *        *la ligne de commentaire est équvalente à la boucle for sous-jacente
     */
    public static Board ofInnerBlocksWalled(List<List<Block>> innerBlocks) {
        checkBlockMatrix(innerBlocks,Cell.ROWS-2,Cell.COLUMNS-2);
        List<Sq<Block>> blocks = new ArrayList<Sq<Block>>();
        
        blocks.addAll(Collections.nCopies(Cell.COLUMNS, Sq.constant(Block.INDESTRUCTIBLE_WALL)));
        
        for (List<Block> row : innerBlocks) {
            blocks.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
            row.forEach(block -> blocks.add(Sq.constant(block)));
            blocks.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
        }
        
        blocks.addAll(Collections.nCopies(Cell.COLUMNS, Sq.constant(Block.INDESTRUCTIBLE_WALL)));
        
        return new Board(new ArrayList<>(blocks));
    }

    
    
    /**
     * @param quadrantNWBlocks (Le cadre nord-ouest de blocs)
     * 
     * @return un plateau muré symétrique avec les blocs du quadrant nord-ouest donnés
     * @see : les dimensions de la matrice sont vérifiées par la méthode "checkBlockMatrix"
     */
    public static Board ofQuadrantNWBlocksWalled(List<List<Block>> quadrantNWBlocks) {
        checkBlockMatrix(quadrantNWBlocks, Cell.ROWS/2 ,Cell.COLUMNS/2); //6, 7

        // création d'une autre variable pour ne pas modifier le paramètre
        List<List<Block>> halfNBlocks = new ArrayList<>();

        // pour chaque ligne on fait et ajoute son miroir
        for (List<Block> row : quadrantNWBlocks) {
            halfNBlocks.add(Lists.mirrored(new ArrayList<>(row)));
        }
        return ofInnerBlocksWalled(Lists.mirrored(new ArrayList<>(halfNBlocks)));
    }

        

    /**
     * @param c (Une case)
     *          
     * @return la séquence des blocs pour la case donnée
     */
    public Sq<Block> blocksAt(Cell c) { 
        return this.board.get(c.rowMajorIndex());
    }

    
    /**
     * @param c (Une case)
     *
     * @return le premier bloc pour la case donnée (la tête de la séquence de blocs)
     */
    public Block blockAt(Cell c) {
        return this.blocksAt(c).head();
    }
    

}

