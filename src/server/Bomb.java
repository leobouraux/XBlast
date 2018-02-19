package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

/**
 * 3
 * Une bombe.
 * @author Léo Bouraux (257368)
 *
 */

public final class Bomb {
    
    private final PlayerID ownerId;
    private final Cell position;
    private final Sq<Integer> fuseLengths;
    private final int range;


    /**
     * @param ownerId, position, fuseLengths, range
     *          (Le propriétaire, la position, la séquence de longueurs de mèche, et la portée de la bombe)
     *          
     * Construit une bombe
     */
    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths, int range) {
        
        this.ownerId=Objects.requireNonNull(ownerId);
        this.position=Objects.requireNonNull(position);
        this.fuseLengths=Objects.requireNonNull(fuseLengths);
        if (fuseLengths.isEmpty())
            throw new IllegalArgumentException();
        this.range=ArgumentChecker.requireNonNegative(range);
    }
    
  
    /**
     * @param ownerId, position, fuseLengths, range
     * 
     *  * Construit une bombe avec le propriétaire, la position, la séquence 
     * de longueurs de mèche et la portée de la bombe donnés
     */
    public Bomb(PlayerID ownerId, Cell position, int fuseLength, int range) {
        
        this(ownerId,
                position,
                Sq.iterate(fuseLength, u->u-1).limit(ArgumentChecker.requireNonNegative(fuseLength)),
                range);    
    }
    
    
   /**
     * @return l'identité du propriétaire de la bombe
     */
    public PlayerID ownerId() {
        return ownerId;
        
    }
    
 
    /**
     * @return la position de la bombe
     */
    public Cell position() {
        return position;
        
    }
    

    /**
     * @return la séquence des longueurs de mèche de la bombe (présente et futures)
     */
    public Sq<Integer> fuseLengths() {
        return fuseLengths;
        
    }
    
 
    /**
     * @return la longueur de mèche actuelle
     */
    public int fuseLength() {
        return fuseLengths().head();
        
    }
    
    
    /**
     * @return  la portée de la bombe
     */
    public int range() {
        return range;
        
    }

    
    /**
     * @param dir (Une direction)
     *          
     * @return le bras de l'explosion se dirigeant dans la direction donnée
     */
    private Sq<Sq<Cell>> explosionArmTowards(Direction dir) {    
        Sq<Cell> s1= Sq.iterate(position, c -> c.neighbor(dir)).limit(range);
        Sq<Sq<Cell>> s11 = Sq.repeat(Ticks.EXPLOSION_TICKS, s1);
        return s11;
       
    }
    

    /**
     * @return retourne l'explosion correspondant à la bombe
     *        (sous la forme d'un tableau de 4 éléments, chacun représentant un bras)
     */
    public List<Sq<Sq<Cell>>> explosion() {
        ArrayList<Sq<Sq<Cell>>> a = new ArrayList<Sq<Sq<Cell>>>();
        for(Direction dir : Direction.values()) {
            a.add(explosionArmTowards(dir));
        }
        return Collections.unmodifiableList(a);
    }

}

