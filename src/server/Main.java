package ch.epfl.xblast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;
import ch.epfl.xblast.server.painter.BoardPainter;

public final class Main {

    private static final int MAX_NB_OF_PLAYERS = 4, MIN_NB_OF_PLAYERS = 1;
    private static final int PORT = 2016;
    private static ByteBuffer actionBuffer = ByteBuffer.allocate(1);

    private static DatagramChannel channel;
    private static GameState gState = Level.DEFAULT_LEVEL.getInitialGameState();


    /**
     * Premièrement, attends d'avoir recu toute les confirmations de participations au jeu
     * Deuxièmement, envoie l'état de jeu courant à tous les clients et recoit des feedback des joueurs
     * Pour finir, il calcul l'état de jeu suivant
     * @param args : Le nombre de joueur requis pour débuter une partie (par défaut 4)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException  {


        //VÉRIFICATION DE L'ARGUMENT
        if(args.length > 1)
            throw new IllegalArgumentException("le serveur ne doit prendre qu'un seul argument (optionnel)");


        int nbOfClients = MAX_NB_OF_PLAYERS;
        if(args.length == 1) {
            nbOfClients = Integer.parseInt(args[0]);
        }
        if (nbOfClients > MAX_NB_OF_PLAYERS || nbOfClients < MIN_NB_OF_PLAYERS){
            throw new Error("Le nombre de client ne convient pas");
        }


        //PREMIÈRE PHASE

        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(PORT));

        SocketAddress receivedAddress = null;
        Set<SocketAddress> playersAdresses = new HashSet<SocketAddress>();

        //on attend que le nombre requis de clients se joigne à la partie
        while(playersAdresses.size() != nbOfClients) {
            receivedAddress = channel.receive(actionBuffer);
            actionBuffer.flip();
            if(actionBuffer.get() == PlayerAction.JOIN_GAME.ordinal()){
                //les adresses ne sont pas rajoutées 2 fois car playersAdresses est un ensemble
                playersAdresses.add(receivedAddress);
            }
        }
        actionBuffer.clear();

        Map<SocketAddress, PlayerID> playersAd = new HashMap<SocketAddress, PlayerID>();
        int i = 1;
        for (SocketAddress socketAddress : playersAdresses) {
            playersAd.put(socketAddress, PlayerID.getPlayerID(i++));
        }

        //DEUXIÈME PHASE

        BoardPainter boardPainter = BoardPainter.IRON_BOARD_PAINTER;
        ByteBuffer srlzdGstateSenderBuffer = ByteBuffer.allocate(1 + GameStateSerializer.MAX_NB_OF_BYTES);


        channel.configureBlocking(false);

        long startTime = System.nanoTime();
        long nextStepTime = startTime;
        do {
            sendingOfSerializedGState(playersAd, boardPainter, srlzdGstateSenderBuffer);

            Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<PlayerID, Optional<Direction>>();
            Set<PlayerID> bombDropEvents = new HashSet<PlayerID>();

            nextStepTime = nextStepTime + Ticks.TICK_NANOSECOND_DURATION;
            long totalWaitTime = (nextStepTime - System.nanoTime());
            if(totalWaitTime>0){
                try {
                    int waitTimenano = (int)(totalWaitTime % Time.NS_PER_MS);
                    long waitTimemilli = (long) ((totalWaitTime - waitTimenano)/(double)Time.NS_PER_MS);
                    Thread.sleep(waitTimemilli, waitTimenano);
                } catch (InterruptedException e) {
                    throw new Error("Erreur durant l'utilisation du sleep", e);
                }
            }

            //le serveur consulte la totalité des messages reçus des clients durant son « sommeil »
            SocketAddress senderAddress = null;

            do{
                actionBuffer.clear();
                senderAddress = channel.receive(actionBuffer);

                if (actionBuffer.hasArray() && playersAd.containsKey(senderAddress)) {
                    Byte b = actionBuffer.get(0);

                    if (b.intValue() == 6)
                        bombDropEvents.add(playersAd.get(senderAddress));
                    else if (b.intValue() == 5)
                        speedChangeEvents.put(playersAd.get(senderAddress), Optional.empty());
                    else
                        speedChangeEvents.put(playersAd.get(senderAddress),Optional.of(Direction.values()[b.intValue()-1]));
                }

            } while(senderAddress != null);

            gState = gState.next(speedChangeEvents, bombDropEvents);

        } while (!gState.isGameOver());

        sendingOfSerializedGState(playersAd, boardPainter, srlzdGstateSenderBuffer);

        if(gState.winner().isPresent())
            System.out.println("Winner is player " + gState.winner().get().getID());
        else System.out.println("No winner");
    }


    /**
     * @param playersAd : l'adresse des joueurs humains
     * @param boardPainter : le peintre du plateau de jeu
     * @param srlzdGstateSenderBuffer : le tampon envoyant létat de jeu sérialisé
     */
    private static void sendingOfSerializedGState(
            Map<SocketAddress, PlayerID> playersAd, BoardPainter boardPainter,
            ByteBuffer srlzdGstateSenderBuffer) {
        List<Byte> serializedState = GameStateSerializer.serialize(boardPainter, gState);

        byte[] bytes = new byte[serializedState.size()+1];
        int j = 1;
        for (byte b : serializedState) {
            bytes[j++] = b;
        }
        playersAd.forEach((address, playerID) -> {
            //le serveur envoie au client son ID suivi du gameState sérialisé
            bytes[0] = (byte) (playerID.getID()-1);
            srlzdGstateSenderBuffer.put(bytes);
            srlzdGstateSenderBuffer.flip();

            try {
                channel.send(srlzdGstateSenderBuffer, address);
            } catch (Exception e) {
                throw new Error("Erreur durant l'envoi de l'adresse de "+playersAd.get(address));
            }
            srlzdGstateSenderBuffer.clear();
        });
    }
}