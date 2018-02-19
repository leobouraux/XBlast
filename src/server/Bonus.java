package ch.epfl.xblast.server;

/**
 * 4
 * Les différents bonus disponibles dans le jeu.
 * @author Léo Bouraux (257368)
 *
 */

public enum Bonus {
    
    
    INC_BOMB {
        @Override
        public Player applyTo(Player player) { 
            return player.withMaxBombs(Math.min(player.maxBombs()+1, Player.MAX_BOMBS));
        }
            
    },

    INC_RANGE {
        @Override
        public Player applyTo(Player player) {
            return player.withBombRange(Math.min(player.bombRange()+1, Player.MAX_RANGE));         
        }
    };   
    

    
    /**
     * @param player (Un joueur)
     *          
     * @return applique le bonus au joueur donné, et retourne le joueur affecté
     */
    abstract public Player applyTo(Player player);
}
