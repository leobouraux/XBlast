package ch.epfl.xblast.client;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;
import ch.epfl.xblast.server.GameStateSerializer;

public final class Main {
    private final static int PORT = 2016;
    private static final XBlastComponent XBLAST_COMPONENT = new XBlastComponent();
    private static ByteBuffer actionSenderBuffer = ByteBuffer.allocate(1);
    private static DatagramChannel channel;
    
    /**
     * Premièrement, envoie un signal à l'adresse du serveur confirmant la volonté de rejoindre le serveur
     * Deuxièmement, quand le jeu commence, recoit les états de jeu encoded du serveur et les affiche
     * Ensuite, il renvoie les SpeedChangeEvents au serveur.
     * @param args : l'adresse IP du serveur (par défaut localhost)
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

       //VÉRIFICATION DE L'ARGUMENT
        if(args.length > 1){
            throw new IllegalArgumentException("le serveur ne doit prendre qu'un seul argument (optionnel)");
        }
        String server = "localhost";
        if(args.length == 1){
            server = args[0];
        }
 
        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        SocketAddress hostAddress = new InetSocketAddress(server, PORT);

        //construction de l'interface graphique
        try {
            SwingUtilities.invokeAndWait( () -> createUI(hostAddress));
        } catch (InvocationTargetException | InterruptedException e3) {
            throw new Error("Erreur à la construction de l'interface graphique par le fil Swing", e3);
        }
        
        //PREMIÈRE PHASE
        //cummunication avec le serveur
        actionSenderBuffer.put((byte)PlayerAction.JOIN_GAME.ordinal());
        actionSenderBuffer.flip();

        //inBuffer correspond au srlzdGstateSenderBuffer envoyé depuis le serveur
        ByteBuffer inBuffer = ByteBuffer.allocate(GameStateSerializer.MAX_NB_OF_BYTES+1);

        
        SocketAddress gameStateAddress = null;
        do{
            channel.send(actionSenderBuffer, hostAddress);
            Thread.sleep(Time.MS_PER_S);
            gameStateAddress = channel.receive(inBuffer);
        }
        while(gameStateAddress == null);
        

        //DEUXIÈME PHASE
    
        channel.configureBlocking(true);
        
        //updateState(inBuffer);
        do {
            inBuffer.clear();
            gameStateAddress = channel.receive(inBuffer) ;
            updateState(inBuffer);
        } while (gameStateAddress!= null);
        channel.close();
    }
    
    
    
    
    /**
     * @param inBuffer : un tampon entrant
     * @param xbBlastComponent : le composant XBLAST
     * Le fil Swing affiche l'état de jeu mis à jour
     */
    private static void updateState(ByteBuffer inBuffer){
        List<Byte> inBufferList = new ArrayList<Byte>();
        for(byte b : inBuffer.array()){
            inBufferList.add(b);
        }
        PlayerID playerId = PlayerID.getPlayerID(inBufferList.get(0)+1);
        GameState gState = GameStateDeserializer.deserializeGameState(inBufferList.subList(1, inBufferList.size()));
        XBLAST_COMPONENT.setGameState(gState, playerId);
    }
    
    
    
    /**
     * @param xbBlastComponent : le composant XBLAST
     * @param channel : un canal de communication serveur/client
     * @param actionBuffer : le tampon gérant les actions des joueurs
     * @param address : adresse 
     * 
     * Interface utilisateur
     */
    private static void createUI(SocketAddress hostAddress) {
        
    assert SwingUtilities.isEventDispatchThread();    
    JFrame frame = new JFrame("XBlast");
    
    Map<Integer, PlayerAction> kb = new HashMap<Integer, PlayerAction>();
    kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
    kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
    kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
    kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
    kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
    kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);

    
    Consumer<PlayerAction> c = (playerAction) -> {
        actionSenderBuffer.clear();
        actionSenderBuffer.put((byte) playerAction.ordinal());
        actionSenderBuffer.flip();
         
        try {
            channel.send(actionSenderBuffer, hostAddress); 
        } catch (IOException e) {
            throw new Error("Erreur durant l'envoi de l'action", e);
        }

    };

    frame.add(XBLAST_COMPONENT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);
    XBLAST_COMPONENT.addKeyListener(new KeyboardEventHandler(kb, c));
    XBLAST_COMPONENT.requestFocusInWindow();
    frame.pack();

    }

}
