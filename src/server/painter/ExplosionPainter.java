package ch.epfl.xblast.server.painter;

import ch.epfl.xblast.server.Bomb;

/**
 * 7
 * Un peintre de bombes et d'explosions.
 * @author Léo Bouraux (257368)
 *
 */
public final class ExplosionPainter {

    /**
     * L'octet pour les cases sans de particule d'explosion
     */
    public final static byte BYTE_FOR_EMPTY = 16;
    public final static byte BYTE_FOR_BLACK_BOMB = 20;
    public final static byte BYTE_FOR_WHITE_BOMB = 21;
    
    public final static byte BYTE_FOR_NORTH_BLAST = 0b1000;
    public final static byte BYTE_FOR_EAST_BLAST  = 0b0100;
    public final static byte BYTE_FOR_SOUTH_BLAST = 0b0010;
    public final static byte BYTE_FOR_WEST_BLAST  = 0b0001;
    public final static byte BYTE_FOR_NO_BLAST    = 0b0000;
    
    


    
    private ExplosionPainter() {}
    
    
    /**
     * @param bomb
     *          (Une bombe)
     * @return l'octet (byte) identifiant l'image à utiliser pour la bombe
     */
    public static byte byteForBomb(Bomb bomb) {
        if (Integer.bitCount(bomb.fuseLength())==1)
            return BYTE_FOR_WHITE_BOMB;
        else return BYTE_FOR_BLACK_BOMB;
    }


    /**
     * @param N, E, S, W
     *          (vrai si la case N,E,S,W est occupée par une particule d'explosion)
     * @return l'octet (byte) identifiant l'image à utiliser pour une particule d'explosion
     */
    public static byte byteForBlast(boolean N, boolean E, boolean S, boolean W) {
        int n = (N ? BYTE_FOR_NORTH_BLAST : BYTE_FOR_NO_BLAST);
        int e = (E ? BYTE_FOR_EAST_BLAST  : BYTE_FOR_NO_BLAST);
        int s = (S ? BYTE_FOR_SOUTH_BLAST : BYTE_FOR_NO_BLAST);
        int w = (W ? BYTE_FOR_WEST_BLAST  : BYTE_FOR_NO_BLAST);
        return (byte) (n+e+s+w);
    }
    
}
