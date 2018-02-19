package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

/**
 * 2/4
 * Les différents types de blocs.
 * @author Léo Bouraux (257368)
 *
 */
public enum Block {
    FREE, 
    INDESTRUCTIBLE_WALL, 
    DESTRUCTIBLE_WALL, 
    CRUMBLING_WALL,
    BONUS_BOMB(Bonus.INC_BOMB), 
    BONUS_RANGE(Bonus.INC_RANGE);
    

 
    private Bonus maybeAssociatedBonus;

    
    private Block(Bonus maybeAssociatedBonus) {
        this.maybeAssociatedBonus = maybeAssociatedBonus;
    }

   
    private Block() {
        this(null);
    }

    
    /**
     * @return  vrai si et seulement si le bloc est libre
     */
    public boolean isFree() {
        return (FREE.equals(this));
    }
    
    
    /**
     * @return vrai si et seulement si le bloc peut héberger un joueur
     */
    public boolean canHostPlayer() {
        return (isFree() || isBonus());
    }
    
    
    /**
     * @return vrai si et seulement si le bloc 
     *     projette une ombre sur le plateau de jeu
     */
    public boolean castsShadow() {
        return (INDESTRUCTIBLE_WALL.equals(this) || DESTRUCTIBLE_WALL.equals(this) || CRUMBLING_WALL.equals(this));
    }   
    
    
    /**
     * @return vrai si et seulement si le bloc représente un bonus
     */
    public boolean isBonus() {
        return this.maybeAssociatedBonus != null;
    }
    
    
    /**
     * @return le bonus associé à ce bloc
     * @throws NoSuchElementException s'il n'y a pas de bonus
     */
    public Bonus associatedBonus() {
        if(maybeAssociatedBonus == null)
            throw new NoSuchElementException();
        else return this.maybeAssociatedBonus;
    }
    
}
