package ch.epfl.xblast.server.painter;

import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * 7
 * Un peintre de joueur. 
 * @author Léo Bouraux (257368)
 *
 */
public final class PlayerPainter {

    private PlayerPainter() {}

    private final static int INDEX_FOR_PLAYER_1 = 0;
    private final static int INDEX_FOR_PLAYER_2 = 20;
    private final static int INDEX_FOR_PLAYER_3 = 40;
    private final static int INDEX_FOR_PLAYER_4 = 60;
    private final static int INDEX_FOR_INVULNERABLE_PLAYER = 80;
    
    private final static byte BYTE_FOR_LOSING_LIFE = 12;
    private final static byte BYTE_FOR_DYING = 13;
    
    private final static int INDEX_WHEN_REST_IS_1 = 1;
    private final static int INDEX_WHEN_REST_IS_3 = 2;
    private static final int NUMBER_OF_IMAGES_PER_DIRECTION_PER_PLAYER = 3;
    private static final byte BYTE_FOR_EMPTY = 100;
    
    
    /**
     * @param tick, player 
     *      (Un coup d'horloge, un joueur)
     * @return l'octet (byte) identifiant l'image à utiliser pour un joueur
     */
    public static byte byteForPlayer(int tick, Player player) {
        byte byteForPl = 0;
        
        //MORT
        if (!player.isAlive())
            return BYTE_FOR_EMPTY;
        
        //INVULNERABLE
        if (player.lifeState().state().equals(State.INVULNERABLE) && tick%2== 1) {
            byteForPl = INDEX_FOR_INVULNERABLE_PLAYER;
        } 
        
        //VUNERABLE
        else {
            switch (player.id()) {
            case PLAYER_1 : byteForPl = INDEX_FOR_PLAYER_1; break;
            case PLAYER_2 : byteForPl = INDEX_FOR_PLAYER_2; break;
            case PLAYER_3 : byteForPl = INDEX_FOR_PLAYER_3; break;
            case PLAYER_4 : byteForPl = INDEX_FOR_PLAYER_4; break;
            default : break;
            }
        }
        
        //MOURANT
        if (player.lifeState().state().equals(State.DYING)) {
            if (player.lives() > 1)
                return (byte) (byteForPl + BYTE_FOR_LOSING_LIFE);
            else
                return (byte) (byteForPl + BYTE_FOR_DYING);
        }

        //EN F° DE SA DIRECTION
        byteForPl +=  player.direction().ordinal()*NUMBER_OF_IMAGES_PER_DIRECTION_PER_PLAYER;

        //EN F° DE SA POSITION
        if (player.direction().isHorizontal()) {
            return forPlayerInItsDirection(byteForPl, player.position().x());
        }
        else {
            return forPlayerInItsDirection(byteForPl, player.position().y());
        }
    }



    /**
     * @param byteForPl : l'octet (byte) identifiant l'image à utiliser pour un joueur
     * @param player : le joueur
     * @param coordinate : la coordonée x si le joueur regarde vers l'ouest ou l'est, y sinon
     * @return l'octet (byte) identifiant l'image à utiliser pour un joueur en fonction de sa position
     * 
     * @see : limite la duplication de code
     */
    private static byte forPlayerInItsDirection(byte byteForPl, int coordinate) {
        switch (coordinate%4) {
        case 1 : return (byte)(byteForPl + INDEX_WHEN_REST_IS_1);
        case 3 : return (byte) (byteForPl + INDEX_WHEN_REST_IS_3);
        default : return byteForPl; //cas 0 ET 2
        }
    }
}