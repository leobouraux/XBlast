package ch.epfl.xblast.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameState.Player;

/**
 * 10
 * Un composant Swing affichant l'état d'une partie de XBlast.
 * @author Léo Bouraux (257368)
 *
 */
@SuppressWarnings("serial")
public final class XBlastComponent extends JComponent{

    private GameState gState;
    
    private final static int PREFERED_WIDTH = 960,
            PREFERED_HEIGHT = 688,
            FONT_SIZE_FOR_LIVES = 25,
            COORDINATE_FOR_LIVES_Y = 659,
            COORDINATE_FOR_LIVES_X1 = 96,
            COORDINATE_FOR_LIVES_X2 = 240,
            COORDINATE_FOR_LIVES_X3 = 768,
            COORDINATE_FOR_LIVES_X4 = 912,
            COORDINATE_FOR_PLAYER_IMAGES_X = 24,
            COORDINATE_FOR_PLAYER_IMAGES_Y = 52;
            
    
    
    /** (non-Javadoc)
     * @return la taille idéale du composant (ici 960x688)
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREFERED_WIDTH, PREFERED_HEIGHT);
    }
    
    
    /**
     * Méthode appelée par Swing pour (re)dessiner le contenu du composant
     * Dessine le plateau de jeu, les bombes, les joueurs, le score et le temps restant
     * @see : Height ↑,    Width →
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D)g0;
        
        if(this.gState == null){
            return;
        }
       
        
        int i = 0;
        int BoardImageWidth = this.gState.getBoardImages().get(0).getWidth(null);
        int BoardImageHeight = this.gState.getBoardImages().get(0).getHeight(null);

         //DESSIN DU PLATEAU DE JEU
        
        for (Image img : this.gState.getBoardImages()) {
            g.drawImage(img, i%Cell.COLUMNS*BoardImageWidth, i/Cell.COLUMNS*BoardImageHeight, null);
            i++;
        }
        
        //DESSIN DES BOMBES/EXPLOSIONS 
        
        i = 0;
        for (Image img : this.gState.getBombsExplosionsImages()) {
            g.drawImage(img, i%Cell.COLUMNS*BoardImageWidth, i/Cell.COLUMNS*BoardImageHeight, null);
            i++;
        }
        
        //DESSIN DU SCORE
        
        int x = 0;
        int scoreY = BoardImageHeight * Cell.ROWS;
        for (Image img : this.gState.getScoreImages()) {
            g.drawImage(img, x, scoreY, null);
            //on décale à chaque fois x de la largeur de l'image précédente
            x += img.getWidth(null);
        }
        
        //DESSIN DU NOMBRE DE VIES
        
        Font font = new Font("Arial", Font.BOLD, FONT_SIZE_FOR_LIVES);
        g.setColor(Color.WHITE);
        g.setFont(font);
        Player player1 = gState.getPlayers().get(0);
        Player player2 = gState.getPlayers().get(1);
        Player player3 = gState.getPlayers().get(2);
        Player player4 = gState.getPlayers().get(3);
        
        g.drawString(player1.lives().toString(), COORDINATE_FOR_LIVES_X1, COORDINATE_FOR_LIVES_Y);
        g.drawString(player2.lives().toString(), COORDINATE_FOR_LIVES_X2, COORDINATE_FOR_LIVES_Y);
        g.drawString(player3.lives().toString(), COORDINATE_FOR_LIVES_X3, COORDINATE_FOR_LIVES_Y);
        g.drawString(player4.lives().toString(), COORDINATE_FOR_LIVES_X4, COORDINATE_FOR_LIVES_Y);
       
        //DESSIN DU TEMPS RESTANT
        
        x=0;
        int timeY = scoreY + this.gState.getScoreImages().get(0).getHeight(null);
        for (Image img : this.gState.getTimeImages()) {
            g.drawImage(img, x, timeY, null);
            x+=img.getWidth(null);
        }
        
        //DESSIN DES JOUEURS
        
        List<Player> list = new ArrayList<Player>(gState.getPlayers());
        //trie la liste en utilisant les comparateurs plus bas
        Collections.sort(list, comp1.thenComparing(comp2));
        for (Player player : list) {
            int Xs = 4*player.position().x() - COORDINATE_FOR_PLAYER_IMAGES_X;
            int Ys = 3*player.position().y() - COORDINATE_FOR_PLAYER_IMAGES_Y;
            g.drawImage(player.getImage(), Xs, Ys, null);

        }
        
        
    }
    
    
    /**
     * @param gState, plID (L'état de jeu, l'identité d'un joueur)
     * 
     * Permet de changer l'état du jeu affiché par le composant
     */
    public void setGameState(GameState gState, PlayerID plID) {
        this.gState = gState;
        this.comp2 = new Comparator<GameState.Player>() {
            @Override
            public int compare(Player o1, Player o2) {
               int nbOfPlayers = 4;
               return (o1.id().getID()-plID.getID()+3)%nbOfPlayers - (o2.id().getID()-plID.getID()+3)%nbOfPlayers;
            }
        }; 
        //appel indirect à paintComponent
        this.repaint();
    }
    
    private Comparator<Player> comp1 = new Comparator<GameState.Player>() {
        @Override
        public int compare(Player o1, Player o2) {
           return o1.position().y()-o2.position().y();
        }
    };
    
    private Comparator<Player> comp2;
    
    
}
