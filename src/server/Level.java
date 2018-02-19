package ch.epfl.xblast.server;

import java.util.Arrays;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.painter.BoardPainter;


/**
 * 8
 * Un niveau de jeu XBlast.
 * @author Léo Bouraux (257368)
 *
 */
public final class Level {

    private static final Block __ = Block.FREE;
    private static final Block XX = Block.INDESTRUCTIBLE_WALL;
    private static final Block xx = Block.DESTRUCTIBLE_WALL;
    private static final int NUMBER_OF_LIVES = 3,
            MAX_BOMBS = 2,
            BOMB_RANGE = 3;
    private static final Cell NW_CELL = new Cell(1, 1),
            NE_CELL = new Cell(13, 1),
            SE_CELL = new Cell(13, 11),
            SW_CELL = new Cell(1, 11);
    
    
    
    /**
     * Le niveau par défaut (au début d'une partie)
     */
    public static final Level DEFAULT_LEVEL = new Level(BoardPainter.IRON_BOARD_PAINTER,
                                                        new GameState(Board.ofQuadrantNWBlocksWalled(
                                                                Arrays.asList(
                                                                        Arrays.asList(__, __, __, __, __, xx, __),
                                                                        Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                                                                        Arrays.asList(__, xx, __, __, __, xx, __),
                                                                        Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                                                                        Arrays.asList(__, xx, __, xx, __, __, __),
                                                                        Arrays.asList(xx, XX, xx, XX, xx, XX, __))),
                                                                Arrays.asList(
                                                                        new Player(PlayerID.PLAYER_1, NUMBER_OF_LIVES, NW_CELL, MAX_BOMBS, BOMB_RANGE),
                                                                        new Player(PlayerID.PLAYER_2, NUMBER_OF_LIVES, NE_CELL, MAX_BOMBS, BOMB_RANGE),
                                                                        new Player(PlayerID.PLAYER_3, NUMBER_OF_LIVES, SE_CELL, MAX_BOMBS, BOMB_RANGE),
                                                                        new Player(PlayerID.PLAYER_4, NUMBER_OF_LIVES, SW_CELL, MAX_BOMBS, BOMB_RANGE))));
    private final BoardPainter boardPainter;
    private final GameState initialGameState;
    

    
    /**
     * @param boardPainter (Un peintre du plateau de jeu)
     * @param initialGameState (L'état de jeu initial)
     * 
     * Construit un niveau de jeu en fonction d'un peintre du plateau de jeu, et de l'état de jeu)
     */
    public Level(BoardPainter boardPainter, GameState initialGameState) {
        this.boardPainter=boardPainter;
        this.initialGameState=initialGameState;
    }
    
    
    /**
     * @return un peintre du plateau de jeu
     */
    public BoardPainter getBoardPainter() {
        return boardPainter;
    }
    
    
    /**
     * @return l'état de jeu initial
     */
    public GameState getInitialGameState() {
        return initialGameState;
    }
    
}
