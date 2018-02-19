package ch.epfl.xblast.server;

import ch.epfl.xblast.Time;

/**
 * 2/4/8
 * Les durées en coups d'horloge.
 * @author Léo Bouraux (257368)
 *
 */

public interface Ticks {
    
    /**
     * La "durée de vie" des rectangles représentant le temps restant sur le plateau de jeu final
     */
    public final static int LED_DURATION_ON_FINAL_BOARD = 2;
    
    /**
     *  La durée de la période durant laquelle un joueur est mourant
     */
    public final static int PLAYER_DYING_TICKS = 8;
    
    
    /**
     *  La durée de la période d'invulnérabilité d'un joueur 
     */
    public final static int PLAYER_INVULNERABLE_TICKS = 64;
    
    
    /**
     * La durée de consommation de la mèche d'une bombe
     */
    public final static int BOMB_FUSE_TICKS = 100;
    
    
    /**
     * La durée d'explosion d'une bombe
     */
    public final static int EXPLOSION_TICKS = 30;
    
    
    /**
     * La durée d'écroulement d'un mur destructible 
     */
    public final static int WALL_CRUMBLING_TICKS = EXPLOSION_TICKS;
    
    
    /**
     * La durée de disparition d'un bonus atteint par une explosion
     */
    public final static int BONUS_DISAPPEARING_TICKS = EXPLOSION_TICKS;
    
    
    /**
     * Le nombre de coups d'horloge par seconde
     */
    public final static int TICKS_PER_SECOND = 20;
    
    
    /**
     * La durée, en nanosecondes, d'un coup d'horloge
     */
    public final static int TICK_NANOSECOND_DURATION = (Time.NS_PER_S)/TICKS_PER_SECOND;
            

    /**
     * Le nombre total de coups d'horloge d'une partie (dure 2 min)
     */
    public final static int TOTAL_TICKS = TICKS_PER_SECOND*(Time.S_PER_MIN)*2;
}
