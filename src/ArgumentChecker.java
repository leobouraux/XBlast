package ch.epfl.xblast;

/**
 * 3
 * Permet la  validation des arguments des méthodes. (utile dans les constructeurs)
 * @author Léo Bouraux (257368)
 * 
 */

public final class ArgumentChecker {

    private ArgumentChecker() {
        
    }
    
    /**
     * @param value
     *          Une valeur
     * @return la valeur donnée si elle est positive ou nulle
     * @throws IllegalArgumentException si la valeur donnée est négative
     */
    public static int requireNonNegative(int value) { 
        if (value<0) 
            throw new IllegalArgumentException();
        else return value;
    }
}
