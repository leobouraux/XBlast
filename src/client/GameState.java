package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

/**
 * 9
 * L'état du jeu du point de vue du client. 
 * @author Léo Bouraux (257368)
 *
 */
public final class GameState {

    private final List<GameState.Player> players;
    private final List<Image> boardImages;
    private final List<Image> bombsExplosionsImages;
    private final List<Image> scoreImages;
    private final List<Image> timeImages;
    
    
    /**
     * @param players : La liste de 4 joueurs
     * @param boardImages : La liste d'images représentant la plateau de jeu
     * @param bombsExplosionsImages : La liste d'images représentant les bombes/explosions
     * @param scoreImages : La liste d'images représentant la ligne de score
     * @param timeImages : La liste d'images représentant le ligne du temps
     *     Toutes les images sont représentées en rowMajorOrder
     * 
     * Construit l'état du jeu du point de vue du client
     */
    public GameState(List<GameState.Player> players, List<Image> boardImages, List<Image> bombsExplosionsImages, List<Image> scoreImages, List<Image> timeImages) {
        if (players.size() != 4 || boardImages.size() != Cell.COUNT || bombsExplosionsImages.size() != Cell.COUNT)
            throw new IllegalArgumentException();
            
        this.players = Collections.unmodifiableList(new ArrayList<GameState.Player>(players));
        this.boardImages = Collections.unmodifiableList(new ArrayList<Image>(boardImages));
        this.bombsExplosionsImages = Collections.unmodifiableList(new ArrayList<Image>(bombsExplosionsImages));
        this.scoreImages = Collections.unmodifiableList(new ArrayList<Image>(scoreImages));
        this.timeImages = Collections.unmodifiableList(new ArrayList<Image>(timeImages));
    }
    
    
    /**
     * @return les joueurs courants
     */
    public List<GameState.Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
    
    
    /**
     * @return les images du plateau de jeu courantes
     */
    public List<Image> getBoardImages() {
        return Collections.unmodifiableList(boardImages);
    }


    /**
     * @return les images des explosions et bombes courantes 
     */
    public List<Image> getBombsExplosionsImages() {
        return Collections.unmodifiableList(bombsExplosionsImages);
    }


    /**
     * @return les images du score
     */
    public List<Image> getScoreImages() {
        return Collections.unmodifiableList(scoreImages);
    }


    /**
     * @return les images du temps restant
     */
    public List<Image> getTimeImages() {
        return Collections.unmodifiableList(timeImages);
    }



    
    
    

    /**
     * Un joueur du point de vue du client.
     *
     */
    public final static class Player {    
        private final PlayerID pID;
        private final int lives;
        private final SubCell position;
        private final Image image;

        
        /**
         * @param pID, lives, position, image
         *      (L'identité d'un joueur, son nombre de vie, sa position, son image)
         */
        public Player(PlayerID pID, int lives, SubCell position, Image image) {
            this.pID= Objects.requireNonNull(pID);
            this.lives=ArgumentChecker.requireNonNegative(lives);
            this.position=Objects.requireNonNull(position);
            this.image = image; 
        }
        
        
        /**
         * @return vrai si le joueur (du point de vue du client) est vivant
         */
        public boolean isAlive() {
            return lives > 0;
        }
        

        /**
         * @return le nombre de vie actuel du joueur (du point de vue du client)
         */
        public Integer lives() {
            return lives;
        }
        
        
        /**
         * @return l'identité du joueur (du point de vue du client)
         */
        public PlayerID id() {
            return pID;
        }
        
        /**
         * @return la position du joueur
         */
        public SubCell position() {
            return position;
        }


        /**
         * @return l'image associée au joueur
         */
        public Image getImage() {
            return this.image;
        }

        
    }




}
