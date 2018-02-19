package ch.epfl.xblast;

import java.util.NoSuchElementException;

/**
 * 3/9/10
 * Les identités des 4 joueurs.
 * @author Léo Bouraux (257368)
 *
 */

public enum PlayerID {
    
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;
    
    
    /**
     * @param id (Un entier représentant l'identité du joueur)
     * @return l'identité du joueur correspondante
     */
    public static PlayerID getPlayerID(int id) {
        switch (id) {
        case 1: return PLAYER_1;
        case 2: return PLAYER_2;
        case 3: return PLAYER_3;
        case 4: return PLAYER_4;
        default: throw new NoSuchElementException();
        }
    }

    /**
     * @return l'entier correspondant à l'identité du joueur
     */
    public int getID() {
        switch (this) {
        case PLAYER_1: return 1;
        case PLAYER_2: return 2;
        case PLAYER_3: return 3;
        case PLAYER_4: return 4;
        default: throw new NoSuchElementException();
        }
    }
}
