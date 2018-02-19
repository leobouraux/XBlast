package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.Time;
import ch.epfl.xblast.client.GameState.Player;


/**
 * 9
 * Un désérialiseur d'état de jeu. 
 * @author Léo Bouraux (257368)
 *
 */
public final class GameStateDeserializer {

    private final static int INDEX_FOR_PLAYER_1 = 0,
            INDEX_FOR_PLAYER_2 = 2,
            INDEX_FOR_PLAYER_3 = 4,
            INDEX_FOR_PLAYER_4 = 6,
            
            INDEX_FOR_ALIVE_PLAYER_IMAGE = 0,
            INDEX_FOR_DEAD_PLAYER_IMAGE = 1,
            
            NUMBER_OF_TILES_VOID = 8,
            BYTE_FOR_TEXT_MIDDLE = 10,
            BYTE_FOR_TEXT_RIGHT = 11,
            BYTE_FOR_TILE_VOID = 12,
            BYTE_FOR_LED_OFF = 20,
            BYTE_FOR_LED_ON = 21;


    
    
    
    public GameStateDeserializer() {}
    
    
    /**
     * @param serializedList (Liste d'octets représentant un état sérialisé)
     * @return une représentation visuelle de l'état du jeu
     */
    public static GameState deserializeGameState(List<Byte> serializedList) {
        
        // LE PLATEAU DE JEU
        
        int serializedBoardSize = Byte.toUnsignedInt(serializedList.get(0));
        int endOfBoard = serializedBoardSize+1;
        List<Image> boardImages = deserializedBoard(serializedList.subList(1, endOfBoard));
        
        // LES EXPLOSIONS ET BOMBES
        
        int serializedExplosionsSize = Byte.toUnsignedInt(serializedList.get(endOfBoard));
        int endOfExplosion = serializedBoardSize+serializedExplosionsSize+2;
        List<Image> bombsExplosionsImages = deserializedExplosions(serializedList.subList(endOfBoard+1, endOfExplosion));
        
        // LES 4 JOUEURS
        
        int bytesUsedForAllPlayers = 16;
        int endOfPlayers = endOfExplosion + bytesUsedForAllPlayers;
        List<Player> players = deserializedPlayers(serializedList.subList(endOfExplosion, endOfPlayers));
        
        // LA LIGNE DE SCORE
        
        List<Image> scoreImages = score(players);
        
        // LA LIGNE DU TEMPS
        
        int remainingTime = Byte.toUnsignedInt(serializedList.get(endOfPlayers));
        List<Image> timeImages = time(remainingTime);
        
        return new GameState(players, boardImages, bombsExplosionsImages, scoreImages, timeImages);
    }
    
    
    /**
     * @param serializedBoard (Une liste d'octets représentant le plateau de jeu sérialisé)
     * @return le plateau de jeu désérialisé
     */
    private static List<Image> deserializedBoard(List<Byte> serializedBoard) {
        List<Byte> decodedBoard = RunLengthEncoder.decode(serializedBoard);
        Image[] boardArray = new Image[Cell.COUNT];
        int index = 0;
        // passage du spiralOrder à rowMajorOrder
        for (Cell cell : Cell.SPIRAL_ORDER) {
            //on itère en même temps sur les deux différents plateaux 
            Byte cellImageByte = decodedBoard.get(index++);
            //rajout de l'image correspondante à la position
            boardArray[cell.rowMajorIndex()] = ImageCollection.BLOCKS_IMAGES.image(Byte.toUnsignedInt(cellImageByte));
        }
        return Collections.unmodifiableList(Arrays.asList(boardArray));
    }
    
    
    /**
     * @param serializedExplosions (Une liste d'octets représentant les explosions et bombes sérialisées)
     * @return les explosions et bombes désérialisées
     */
    private static List<Image> deserializedExplosions(List<Byte> serializedExplosions) {
        List<Byte> decodedExplosions = RunLengthEncoder.decode(serializedExplosions);
        List<Image> explosionsImages = new ArrayList<Image>();
         for (Byte b : decodedExplosions) {
            explosionsImages.add(ImageCollection.EXPLOSIONS_IMAGES.imageOrNull(Byte.toUnsignedInt(b)));
        }
        return Collections.unmodifiableList(explosionsImages);
    }
        
        
    /**
     * @param deserializedPlayers (Une liste d'octets représentant les joueurs sérialisés)
     * @return une liste de joueurs désérialisée
     */
    private static List<GameState.Player> deserializedPlayers(List<Byte> deserializedPlayers) {
        List<GameState.Player> playerList = new ArrayList<GameState.Player>();
        int nbOfPlayers = 4;
        int index = 0;
        for (int i = 0; i < nbOfPlayers; i++) {
            int lives = Byte.toUnsignedInt(deserializedPlayers.get((i * nbOfPlayers + index++)));
            int x = Byte.toUnsignedInt(deserializedPlayers.get((i * nbOfPlayers + index++)));
            int y = Byte.toUnsignedInt(deserializedPlayers.get((i * nbOfPlayers + index++)));
            int imageNb = Byte.toUnsignedInt(deserializedPlayers.get((i * nbOfPlayers + index++)));
            index = 0;
            
            GameState.Player player = 
                    new GameState.Player(PlayerID.getPlayerID(i+1),
                                         lives,
                                         new SubCell(x,y),
                                         ImageCollection.PLAYERS_IMAGES.imageOrNull(imageNb));
            playerList.add(player);
        }
        return Collections.unmodifiableList(playerList);
    }
    
    
    /**
     * @param playerList (La liste de joueurs)
     * @return la ligne des scores
     */
    private static List<Image> score(List<GameState.Player> playerList) {
        List<Image> scoreImages = new ArrayList<Image>();
        scoreImages.addAll(playerScore(playerList.get(0), INDEX_FOR_PLAYER_1));
        scoreImages.addAll(playerScore(playerList.get(1), INDEX_FOR_PLAYER_2));
        scoreImages.addAll(Collections.nCopies(NUMBER_OF_TILES_VOID, ImageCollection.SCORE_IMAGES.image(BYTE_FOR_TILE_VOID)));
        scoreImages.addAll(playerScore(playerList.get(2), INDEX_FOR_PLAYER_3));
        scoreImages.addAll(playerScore(playerList.get(3), INDEX_FOR_PLAYER_4));
        return Collections.unmodifiableList(scoreImages);
    }
    
    
    /**
     * @param player, indexForPlayer (Un joueur, un entier correspondant pour lui attribué son image)
     * @return le score pour un joueur
     */
    private static List<Image> playerScore(GameState.Player player, int indexForPlayer) {
        List<Image> playerScoreImages = new ArrayList<Image>();
        // ajoute le visage du joueur quand il est vivant ou mort
        if(player.isAlive())
            playerScoreImages.add(ImageCollection.SCORE_IMAGES.image(indexForPlayer + INDEX_FOR_ALIVE_PLAYER_IMAGE));
        else playerScoreImages.add(ImageCollection.SCORE_IMAGES.image(indexForPlayer + INDEX_FOR_DEAD_PLAYER_IMAGE));
        // ajoute la fin de la case
        playerScoreImages.add(ImageCollection.SCORE_IMAGES.image(BYTE_FOR_TEXT_MIDDLE));
        playerScoreImages.add(ImageCollection.SCORE_IMAGES.image(BYTE_FOR_TEXT_RIGHT));
        
        return Collections.unmodifiableList(playerScoreImages);
    }
    
    
    /**
     * @param remainingTime (Le temps restant avant la fin de la partie en
     *                       multiples entiers de 2 secondes, arrondi à l'entier supérieur)
     * @return la ligne du temps
     */
    private static List<Image> time(int remainingTime) {
        List<Image> timeImages = new ArrayList<Image>();
        
        timeImages.addAll(Collections.nCopies(
                remainingTime, ImageCollection.SCORE_IMAGES.image(BYTE_FOR_LED_ON)));
        
        timeImages.addAll(Collections.nCopies(
                Time.S_PER_MIN - remainingTime, ImageCollection.SCORE_IMAGES.image(BYTE_FOR_LED_OFF)));
        
        return Collections.unmodifiableList(timeImages);
    }

}
