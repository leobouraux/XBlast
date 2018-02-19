package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Ticks;
import ch.epfl.xblast.server.painter.BoardPainter;
import ch.epfl.xblast.server.painter.ExplosionPainter;
import ch.epfl.xblast.server.painter.PlayerPainter;


/**
 * 8
 * Un sérialiseur d'état de jeu. 
 * @author Léo Bouraux (257368)
 *
 */
public final class GameStateSerializer {

    public static final int MAX_NB_OF_BYTES = 409;

    private GameStateSerializer() {}
    
    
    /**
     * @param boardPainter (Un peintre de plateau)
     * @param gState (Un état de jeu)
     * @return la version sérialisée de l'état
     */
    public static List <Byte> serialize(BoardPainter boardPainter, GameState gState) {
        List<Byte> serializedGS = new ArrayList<>();
        
        // LE PLATEAU DE JEU COMPRESSÉ
        List<Byte> serializedBoard = new ArrayList<Byte>(Cell.COUNT);
        for (Cell c : Cell.SPIRAL_ORDER) {
            serializedBoard.add(boardPainter.byteForCell(gState.board(), c));
        }
        
        List<Byte> compressedBoard = RunLengthEncoder.encode(serializedBoard);
        // la taille en octet de la séquence compressée qui suit
        serializedGS.add((byte)compressedBoard.size());
        // tous les octets signés composant la séquence compressée
        serializedGS.addAll(compressedBoard);
        
        // LES EXPLOSIONS ET BOMBES COMPRESSÉES
        List<Byte> serializedBombs = new ArrayList<Byte>(Cell.COUNT);
        for (Cell c : Cell.ROW_MAJOR_ORDER) {
            if(gState.bombedCells().containsKey(c)){
                serializedBombs.add(ExplosionPainter.byteForBomb(gState.bombedCells().get(c)));
            }
            else if(gState.blastedCells().contains(c) && gState.board().blockAt(c).isFree()) {
                serializedBombs.add(ExplosionPainter.byteForBlast(
                        gState.blastedCells().contains(c.neighbor(Direction.N)),
                        gState.blastedCells().contains(c.neighbor(Direction.E)),
                        gState.blastedCells().contains(c.neighbor(Direction.S)),
                        gState.blastedCells().contains(c.neighbor(Direction.W))
                        ));
            }
            else {
                serializedBombs.add(ExplosionPainter.BYTE_FOR_EMPTY);
            }
        }
        
        List<Byte> compressedBombs = RunLengthEncoder.encode(serializedBombs);
        serializedGS.add((byte)compressedBombs.size());
        serializedGS.addAll(compressedBombs);

        // L'ÉTAT DES 4 JOUEURS NON COMPRESSÉ
        for (Player player : gState.players()) {
            serializedGS.add((byte) player.lives());
            serializedGS.add((byte) player.position().x());
            serializedGS.add((byte) player.position().y());
            serializedGS.add(PlayerPainter.byteForPlayer(gState.ticks(), player));
        }

        // LE TEMPS RESTANT
        serializedGS.add((byte) Math.ceil(((double)Ticks.TOTAL_TICKS-gState.ticks())/Ticks.TICKS_PER_SECOND/Ticks.LED_DURATION_ON_FINAL_BOARD));
        
        return Collections.unmodifiableList(serializedGS);
    }
}
