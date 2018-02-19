package ch.epfl.xblast.server.debug;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameStateDeserializer;
import ch.epfl.xblast.client.KeyboardEventHandler;
import ch.epfl.xblast.client.XBlastComponent;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.GameStateSerializer;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.painter.BoardPainter;

public class RandomGame {

    public static void main(String[] args) throws InterruptedException {
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        
        List<Player> players = new ArrayList<Player>();
        players.add(new Player(PlayerID.PLAYER_1, 20, new Cell(1, 1), 2, 3));
        players.add(new Player(PlayerID.PLAYER_2, 20, new Cell(13, 1), 2, 3));
        players.add(new Player(PlayerID.PLAYER_3, 3, new Cell(13, 11), 2, 3));
        players.add(new Player(PlayerID.PLAYER_4, 3, new Cell(1, 11), 2, 3));
        
        /*
         * Voir Si ca affiche le bon joueur lors de conflit d'affichage

         
        players.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 9));
        players.add(new Player(PlayerID.PLAYER_2, 3, new Cell(1, 1), 2, 9));
        players.add(new Player(PlayerID.PLAYER_3, 3, new Cell(1, 1), 2, 9));
        players.add(new Player(PlayerID.PLAYER_4, 3, new Cell(1, 1), 2, 9));
        */
        
        Board board = Board.ofQuadrantNWBlocksWalled(
                /*Arrays.asList(
                        Arrays.asList(__, XX, __, __, __, __, __),
                        Arrays.asList(XX, __, XX, XX, __, XX, __),
                        Arrays.asList(__, XX, XX, __, __, xx, __),
                        Arrays.asList(__, XX, __, XX, XX, XX, XX),
                        Arrays.asList(__, __, __, __, __, __, __),
                        Arrays.asList(__, XX, __, XX, __, XX, __)));*/
                Arrays.asList(
                        Arrays.asList(__, __, __, __, __, xx, __),
                        Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                        Arrays.asList(__, xx, __, __, __, xx, __),
                        Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                        Arrays.asList(__, xx, __, xx, __, __, __),
                        Arrays.asList(xx, XX, xx, XX, xx, XX, __)));

        GameState gState = new GameState(board, players);
        RandomEventGenerator rEventGenerator = new RandomEventGenerator(2016, 30, 50);


        BoardPainter boardPainter = BoardPainter.IRON_BOARD_PAINTER;
        XBlastComponent xbBlastComponent = new XBlastComponent();
        
        JFrame frame = new JFrame("RandomGame");
        
        Map<Integer, PlayerAction> kb = new HashMap<Integer, PlayerAction>();
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        Consumer<PlayerAction> c = System.out::println;

        frame.add(xbBlastComponent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        // après avoir ajouter les composants à la frame
        xbBlastComponent.addKeyListener(new KeyboardEventHandler(kb, c));
        xbBlastComponent.requestFocusInWindow();
        
        do {

            gState = gState.next(rEventGenerator.randomSpeedChangeEvents(), rEventGenerator.randomBombDropEvents());
            
            List<Byte> serializedList = GameStateSerializer.serialize(boardPainter, gState);
            ch.epfl.xblast.client.GameState clientGState = GameStateDeserializer.deserializeGameState(serializedList);
            
            // Étape intermédiaire : GameStatePrinter.printGameState(gState);
            
            xbBlastComponent.setGameState(clientGState, PlayerID.PLAYER_1);
            
            Thread.sleep(50);
            
        } while (!gState.isGameOver());                
    }

}
