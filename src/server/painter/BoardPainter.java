package ch.epfl.xblast.server.painter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;

/**
 * 7
 * Un peintre du plateau de jeu.
 * @author Léo Bouraux (257368)
 *
 */
public final class BoardPainter {

    private final Map<Block, BlockImage> palette;
    private final BlockImage shadowForFreeB;
    // sert à ne pas recalculé à chaque fois le plateau de jeu
    public final static BoardPainter IRON_BOARD_PAINTER = BoardPainter.getIronBoardPainter();
    
    
    /**
     * @param palette (Une palette)
     * @param shadowForFreeB (L'image du block contenant une ombre)
     * 
     * Construit un peintre du plateau de jeu
     */
    public BoardPainter(Map<Block, BlockImage> palette, BlockImage shadowForFreeB) {
        this.shadowForFreeB = shadowForFreeB;
        this.palette = Collections.unmodifiableMap(new HashMap<Block, BlockImage>(palette));
    }
     
    
    /**
     * @param board, cell
     *          (Le plateau de jeu, une case)
     * @return l'octet (byte) identifiant l'image à utiliser pour la case "cell"
     */
    public byte byteForCell(Board board,Cell cell){
        Block block = board.blockAt(cell);
        if(block.isFree() && board.blockAt(cell.neighbor(Direction.W)).castsShadow()) {
            return (byte) shadowForFreeB.ordinal();
        }
        else return (byte) palette.get(block).ordinal();
    }
    
   
    /**
     * @return le boardpainter "fer", en faisant le lien entre les blocs et les images 
     */
    private static BoardPainter getIronBoardPainter(){
        Map<Block, BlockImage> palette = new HashMap<Block, BlockImage>();
        
        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
        palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
        BlockImage shadowForFreeB = BlockImage.IRON_FLOOR_S;
        return new BoardPainter(palette, shadowForFreeB);
    }
    

}
