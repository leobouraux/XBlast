package ch.epfl.xblast;


/**
 * 1
 * Une direction.
 * @author Léo Bouraux (257368)
 *
 */
public enum Direction {
    N,
    E,
    S,
    W ;
    

    /**
     * @return la direction opposée de 
     *  celle à laquelle on l'applique.
     *  
     *  @see: une énumération est équivalente à un int
     *  (pas besoin de equals)
     */
    public Direction opposite() {
        if (this == N)
            return S;
        if (this == E) 
            return W;
        if (this == S)
            return N;
        else return E;
    }
    
    
    /**
     * @return vrai si et seulement si la direction à
     * laquelle on l'applique est horizontale à l'écran.
     */
    public boolean isHorizontal() {
        return (this==E || this==W);
    }


    /**
     * @param that (La direction courante)
     *          
     * @return vrai si et seulement si la direction à laquelle on
     *  l'applique est parallèle à la direction passée en argument.
     */
    public boolean isParallelTo (Direction that) {
        return (this==that || this==that.opposite());
    }

}
