package ch.epfl.xblast;

/**
 * 10
 * L'action qu'un joueur humain peut effectuer sur son joueur virtuel.
 * @author Léo Bouraux (257368)
 *
 */
public enum PlayerAction {

    /**
     * souhait de se joindre à une partie
     */
    JOIN_GAME, 
    
    /**
     * demande de déplacement vers le nord
     */
    MOVE_N,  
    
    /**
     * demande de déplacement vers l'est
     */
    MOVE_E, 
    
    /**
     * demande de déplacement vers le sud
     */
    MOVE_S,
    
    /**
     * demande de déplacement vers l'ouest
     */
    MOVE_W, 
    
    /**
     * demande d'arrêt du déplacement
     */
    STOP, 
    
    /**
     * demande de dépôt d'une bombe
     */
    DROP_BOMB;
    
}
