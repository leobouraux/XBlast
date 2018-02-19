



package ch.epfl.xblast;

/**
 * 4
 * Constantes entières de type int liées au temps.
 * @author Léo Bouraux (257368)
 *
 */
public interface Time {
    
    /**
     * Le nombre de secondes par minute
     */
    public final static int S_PER_MIN = 60; 
    
    /**
     * Le nombre de milisecondes par seconde
     */
    public final static int MS_PER_S = 1000;
    
    /**
     * Le nombre de microsecondes par seconde
     */
    public final static int US_PER_S = MS_PER_S*1000;
    
    /**
     * Le nombre de nanosecondes par seconde
     */
    public final static int NS_PER_S = US_PER_S*1000;

    public final static int NS_PER_MS = NS_PER_S/MS_PER_S;
    
}
