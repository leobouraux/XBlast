package ch.epfl.xblast.server;

import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

/**
 * 3
 * Un joueur.
 * @author Léo Bouraux (257368)
 *
 */
public final class Player {

    private final PlayerID id;
    private final Sq<LifeState> lifeStates;
    private final Sq<DirectedPosition> directedPos;
    private final int maxBombs;
    private final int bombRange;
    
    public final static int MAX_BOMBS = 9;
    public final static int MAX_RANGE = 9;
    
    
    
    /**
     * @param id (L'identité d'un joueur)
     * @param lifeStates (Sa séquence d'état de vie)
     * @param directedPos (Sa séquence de positions dirigées)
     * @param maxBombs (Le nombre max de bombes qu'il peut poser simultanément)
     * @param bombRange (La portée maximale de ses bombes)
     * 
     * Construit un joueur
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates, Sq<DirectedPosition>
                    directedPos, int maxBombs, int bombRange) {
        this.id=Objects.requireNonNull(id);
        this.lifeStates=Objects.requireNonNull(lifeStates);       
        this.directedPos=Objects.requireNonNull(directedPos);
        this.maxBombs=ArgumentChecker.requireNonNegative(maxBombs);
        this.bombRange=ArgumentChecker.requireNonNegative(bombRange);
    }
    
    
    /**
     * @param id (L'identité d'un joueur)
     * @param lives (Les vies restantes du joueur : mort si <1)
     * @param position (La position du joueur)
     * @param maxBombs (Le nombre max de bombes qu'il peut poser simultanément)
     * @param bombRange (La portée maximale de ses bombes)
     * 
     * Construit un joueur
     */
    public Player(PlayerID id, int lives, Cell position, int maxBombs, int bombRange) {
        this (id,
              stateAfterDeath(ArgumentChecker.requireNonNegative(lives)),
              DirectedPosition.stopped(new DirectedPosition(SubCell.centralSubCellOf(position), Direction.S)),
              maxBombs, 
              bombRange);
    }
    
    
    /**
     * @return l'identité du joueur
     */
    public PlayerID id() {
        return id;
    }
    
    
    /**
     * @return la séquence des couples (nombre de vies, état) du joueur
     */
    public Sq<LifeState> lifeStates() {
        return lifeStates;
    }
    
    
    /**
     * @return  le couple (nombre de vies, état) actuel du joueur
     */
    public LifeState lifeState() {
        return lifeStates.head();
    }

    
    /**
     * @return la séquence d'états pour la prochaine vie du joueur
     */
    public Sq<LifeState> statesForNextLife() {
        Sq<LifeState> temp = Sq.repeat(Ticks.PLAYER_DYING_TICKS, new LifeState(this.lives(), LifeState.State.DYING));
        return temp.concat(stateAfterDeath(lives()-1));
        
    }
    
    
    /**
     * @param lives (Les vies du joueurs)
     * 
     * @return l'état d'un joueur après une mort
     */
    private static Sq<LifeState> stateAfterDeath(int lives){
        if (lives>=1) {
            Sq<LifeState> s = Sq.repeat(Ticks.PLAYER_INVULNERABLE_TICKS, new LifeState(lives, LifeState.State.INVULNERABLE));
            return s.concat(Sq.constant(new LifeState(lives, LifeState.State.VULNERABLE)));
        }
        else return Sq.constant(new LifeState(0, LifeState.State.DEAD));
          
    }
    
    
    /**
     * @return le nombre de vie actuel du joueur
     */
    public int lives() {
        return lifeStates.head().lives();
    }
    
    
    /**
     * @return vrai si le joueur est vivant
     */
    public boolean isAlive() {
        return lives() > 0;
    }

    
    /**
     * @return la séquence des positions dirigées du joueur
     */
    public Sq<DirectedPosition> directedPositions() {
        return directedPos;
    }
    
    
    /**
     * @return la position actuelle du joueur (sous-case)
     *         sous-case --> case   use SOUSCASE.containingCell()
     */
    public SubCell position() {
        return directedPositions().head().position();
    }
    
    
    /**
     * @return la direction vers laquelle le joueur regarde actuellement
     */
    public Direction direction() {
        return directedPositions().head().direction();
    }
    
    
    /**
     * @return le nombre maximum de bombes que le joueur peut déposer
     */
    public int maxBombs() {
        return maxBombs;
    }
    
    
    /**
     * @param newMaxBombs (Nombre maximum de bombes)
     *          
     * @return un joueur identique à celui auquel on l'applique, si ce n'est
     *         que son nombre maximum de bombes est celui donné
     */
    public Player withMaxBombs(int newMaxBombs) {
        return new Player(id, lifeStates, directedPos, newMaxBombs, bombRange);
    }
    
    
    /**
     * @return la portée (en nombre de cases) des explosions
     *         produites par les bombes du joueur
     */
    public int bombRange() {
        return bombRange;
    }
    
    
    /**
     * @param newBombRange (La portée d'une bomme)
     *          
     * @return un joueur identique à celui auquel on l'applique, si ce n'est
     *         que la portée de ses bombes est celle donnée,
     */
    public Player withBombRange(int newBombRange) {
        return new Player(id, lifeStates, directedPos, maxBombs, newBombRange);
        
    }
    
    
    /**
     * @return une bombe positionnée sur la case sur laquelle le joueur se 
     *         trouve actuellement
     */
    public Bomb newBomb() {
        Cell position = this.position().containingCell();
        return new Bomb(id, position, Ticks.BOMB_FUSE_TICKS, bombRange);
    }
    

    
    
    
    
    
    
    
    
    
    
    
    /**
     * Un couple (nombre de vies, état) du joueur.
     *
     */
    public final static class LifeState {
       
        private final int lives;
        private final State state;

        
        /**
         * Décrit l'état de vie du joueur
         */
        public enum State { 
            INVULNERABLE,  VULNERABLE, DYING, DEAD;
        }
        
        
        public LifeState(int lives, State state) {
            this.lives=ArgumentChecker.requireNonNegative(lives);
            this.state=Objects.requireNonNull(state);
        }
        
        
        /**
         * @return le nombre de vies du couple
         */
        public int lives() {
            return lives;
       }


        /**
         * @return l'état
         */
        public State state() {
            return state;
        }

        
        /**
         * @return vrai si l'état permet au joueur de se déplacer (vulnérable ou invulnérable)
         */
        public boolean canMove() {
            switch (state) {
            case INVULNERABLE : 
            case VULNERABLE :
                return true;
            default : return false;
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * La position dirigée d'un joueur (la paire : "sous-case, direction").
     *
     */
    public final static class DirectedPosition {
        private final SubCell position;
        private final Direction direction;
        
        public DirectedPosition(SubCell position, Direction direction) {
            this.position=Objects.requireNonNull(position);
            this.direction=Objects.requireNonNull(direction);
        }
        
 
        /**
         * @param p (La position dirigée courante)
         *          
         * @return une séquence infinie composée uniquement de la position
         *          dirigée donnée d'un joueur arrêté dans cette position
         */
        public static Sq<DirectedPosition> stopped(DirectedPosition p) {
            return Sq.constant(p);
        }
        
   
        /**
         * @param p(La position dirigée courante)
         *          
         * @return une séquence infinie de positions dirigées d'un joueur
         *          se déplaçant dans la direction dans laquelle il regarde
         */
        public static Sq<DirectedPosition> moving(DirectedPosition p) {
            return Sq.iterate(p , p1 -> p1.withPosition(p1.position().neighbor(p1.direction())));
        }

        
        /**
         * @return la position
         */
        public SubCell position() {
            return position;
        }
        
  
        /**
         * @param newPosition
         *          Une position
         * @return une position dirigée dont la position est celle donnée
         *         (cette direction est identique à celle du récepteur)
         */
        public DirectedPosition withPosition(SubCell newPosition) {
            return new DirectedPosition(newPosition, direction);
        }
    
        
        /**
         * @return la direction de la position dirigée
         */
        public Direction direction() {
            return direction;
        }
        
    
        /**
         * @param newDirection (Une direction)
         *          
         * @return une position dirigée dont la direction est celle donnée
         *         (cette position est identique à celle du récepteur)
         */
        public DirectedPosition withDirection(Direction newDirection) {
            return new DirectedPosition(position, newDirection);
        }
        
    }
    
    
    
}