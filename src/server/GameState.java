package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;

/**
 * 4/5/6
 * L'état d'une partie pour un moment(tick) donné.
 * @author Léo Bouraux (257368)
 *
 */
public final class GameState {

    private static int ticks;
    private final Board board;
    private final List<Player> players;
    private final List<Bomb> bombs;
    private final List<Sq<Sq<Cell>>> explosions;
    private final List<Sq<Cell>> blasts;

    private static List<List<PlayerID>> Permutations = Collections.unmodifiableList(
            new ArrayList<>(Lists.permutations(Arrays.asList(PlayerID.values()))));
    
    private final static Random RANDOM = new Random(2016);

    
    /**
     * @param ticks, board, players, bombs, explosions, blasts
     *      (Un tick, le plateau de jeu, 4 joueurs, leurs bombes, leurs explosions et leurs particules)
     * 
     * Construit un état de jeu
     */
    public GameState(int ticks, Board board, List<Player> players,
            List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions, List<Sq<Cell>> blasts) {
        
        if (players.size()!=4)
            throw new IllegalArgumentException(); 
        GameState.ticks = ArgumentChecker.requireNonNegative(ticks);
        this.board = Objects.requireNonNull(board);
        this.players = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<Player> (players)));
        this.bombs = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<Bomb>(bombs)));
        this.explosions = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<Sq<Sq<Cell>>>(explosions)));
        this.blasts = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<Sq<Cell>>(blasts)));

    }
    
    
    /**
     * @param board, players
     *      (Un plateau de jeu, des joueurs)
     *      
     * Construit un état de jeu
     */
    public GameState(Board board, List<Player> players) {
        this(0,
                board,
                players,
                new ArrayList<Bomb>(),
                new ArrayList<Sq<Sq<Cell>>>(),
                new ArrayList<Sq<Cell>>());
    }


    /**
     * @return l'ordre de l'identité des joueurs lors d'un conflit, pour chaque coup d'horloge
     */
    private static List<PlayerID> currentIDPermutation() {
        List<PlayerID> currentPerm = Permutations.get(ticks%Permutations.size());
        return Collections.unmodifiableList(currentPerm);     
    }


    /**
     * @param currentPlayerID, players
     *      (L'identité d'un joueur, une liste de joueurs)
     * @return le player correspondant au playerID
     */
    private static Player IdToPlayer(PlayerID currentPlayerID, List<Player> players) {
        Player currentPlayer = null;
        for (Player pl : players) {
            if (pl.id().equals(currentPlayerID))
                currentPlayer = pl;
        }
        return Objects.requireNonNull(currentPlayer);
    }


    /**
     * @return le coup d'horloge correspondant à l'état de jeu
     */ 
    public int ticks() {
        return ticks;
    }


    /**
     * @return vrai si et seulement si l'état correspond à une partie terminée
     *         (temps écoulé ou plus qu'un survivant)
     */
    public boolean isGameOver() {
        return ticks > Ticks.TOTAL_TICKS || alivePlayers().size()<2;
    }


    /**
     * @return le temps restant dans la partie (secondes)
     */
    public double remainingTime() {
        return ((double)Ticks.TOTAL_TICKS - ticks)/Ticks.TICKS_PER_SECOND;
    }


    /**
     * @return l'identité du vainqueur de cette partie s'il y en a un, 
     *      sinon la valeur optionnelle vide
     */
    public Optional<PlayerID> winner() {
        if (alivePlayers().size()==1) {
            return Optional.of(alivePlayers().get(0).id());
        }
        else return Optional.empty();
    }


    /**
     * @return le plateau de jeu
     */
    public Board board() {
        return board;
    }


    /**
     * @return retourne tous les joueurs (morts ou vivants) --> 4
     */
    public List<Player> players() {
        return players;
    }


    /**
     * @return retourne les joueurs vivants
     */
    public List<Player> alivePlayers() {
        List<Player> alive = new ArrayList<Player>();
        for (Player player : players) {
            if (player.isAlive())
                alive.add(player);
        }
        return alive;        
    }


    /**
     * @return retourne une table associant les bombes (Values) aux cases (Keys) qu'elles occupent
     *         Map<K, V>
     */
    public Map<Cell, Bomb> bombedCells() {
        return nextBombedCells(this.bombs);
    }


    /**
     * @return l'ensemble des cases sur lesquelles se trouve au moins une particule d'explosion
     */
    public Set<Cell> blastedCells() {
        return nextBlastedCells(this.blasts);
    } 


    /**
     * @param bombs1 (La future liste de bombes)
     * 
     * @return retourne une table associant les bombes (Values) aux cases (Keys) qu'elles occupent au prochain état
     * @see : on ne fait pas de copie de bombs1 car la méthode a un champs privé
     */
    private static Map<Cell, Bomb> nextBombedCells(List<Bomb> bombs1) {
        Map<Cell, Bomb> bombedCells = new HashMap<Cell, Bomb>(bombs1.size());
        bombs1.forEach(bomb -> bombedCells.put(bomb.position(), bomb));
        return Collections.unmodifiableMap(bombedCells);
    }


    /**
     * @param blasts1 (La future séquence de particules d'explosion)
     * 
     * @return l'ensemble des cases sur lesquelles se trouve au moins une particule d'explosion au prochain état
     * @see : on ne fait pas de copie de blasts1 car la méthode a un champs privé
     */
    private static Set<Cell> nextBlastedCells(List<Sq<Cell>> blasts1) {
        Set<Cell> blastedCells = new HashSet<Cell>();
        blasts1.forEach(cell -> blastedCells.add(cell.head()));
        return Collections.unmodifiableSet(blastedCells);
    }


    /**
     * @return l'apparition aléatoire d'un bonus ou d'un block FREE
     */
    private static Block BonusApparition() {
        switch(RANDOM.nextInt(3)) {
        case 0 : return Block.BONUS_BOMB;
        case 1 : return Block.BONUS_RANGE;
        default : return Block.FREE;
        }
    }


    /**
     * @param board0 (Le plateau de jeu courant)
     * @param consumedBonuses (L'ensemble des bonus consommés par les joueurs)
     * @param blastedCells1 (Les nouvelles particules d'explosion)
     * 
     * @return le prochain état du plateau
     */
    private static Board nextBoard(Board board0, Set<Cell> consumedBonuses, Set<Cell> blastedCells1) {
        List<Sq<Block>> blocks1 = new ArrayList<Sq<Block>>();
        for (Cell cell : Cell.ROW_MAJOR_ORDER) {
            if(consumedBonuses.contains(cell)) {
                blocks1.add(Sq.constant(Block.FREE));
            }
            else if(blastedCells1.contains(cell)) {
                if (board0.blockAt(cell).equals(Block.DESTRUCTIBLE_WALL)) {
                    Sq<Block> newBlock = Sq.repeat(Ticks.WALL_CRUMBLING_TICKS, Block.CRUMBLING_WALL);
                    blocks1.add(newBlock.concat(Sq.constant(BonusApparition())));
                }
                else if(board0.blockAt(cell).isBonus()){
                    /*Dans les 2 cas le cas : si un bonus est atteint par une blast alors
                     *  qu'il disparait déjà  OU si il ne disparaît pas, alors il disparait */
                    Sq<Block> newBlock = board0.blocksAt(cell).tail()
                            .limit(Ticks.BONUS_DISAPPEARING_TICKS)
                            .concat(Sq.constant(Block.FREE));
                    blocks1.add(newBlock);
                }
                else blocks1.add(board0.blocksAt(cell).tail());
            }
            else blocks1.add(board0.blocksAt(cell).tail());
        }
        return new Board(blocks1);
    }



    /**
     * @param explosions0 (Les explosions courantes)
     * 
     * @return les futures explosions 
     */
    private static List<Sq<Sq<Cell>>> nextExplosions(List<Sq<Sq<Cell>>> explosions0) {
        List<Sq<Sq<Cell>>> explosions1 = new ArrayList<Sq<Sq<Cell>>>();
        for (Sq<Sq<Cell>> explosion : explosions0) {
            if (!explosion.tail().isEmpty())
                explosions1.add(explosion.tail());
        }
        return Collections.unmodifiableList(explosions1);

    }



    /**
     * @param blasts0 (Les particules d'explosions courantes)
     * @param board0 (Le pateau de jeu courant)
     * @param explosions0 (Les explosions courantes)
     * 
     * @return l'état prochain des particules d'explosions
     */
    private static List<Sq<Cell>> nextBlasts(List<Sq<Cell>> blasts0, Board board0, List<Sq<Sq<Cell>>> explosions0) {
        List<Sq<Cell>> blasts1 = new ArrayList<Sq<Cell>>();
        for (Sq<Cell> blast : blasts0) {
            if (board0.blockAt(blast.head()).isFree() && !blast.tail().isEmpty()){
                blasts1.add(blast.tail());
            }
            //sinon la tail disparait
        }
        for (Sq<Sq<Cell>> explosion : explosions0) {
            if (!explosion.isEmpty()) 
                blasts1.add(explosion.head());
        }
        return Collections.unmodifiableList(blasts1);
    }



    /**
     * @param players0 (Les joueurs courants)
     * @param bombDropEvents (Les événements de dépôt de bombes)
     * @param bombs0 (Les bombes actuelles)
     * 
     * @return la liste des bombes nouvellement posées par les joueurs
     */
    private static List<Bomb> newlyDroppedBombs(List<Player> players0, Set<PlayerID> bombDropEvents, List<Bomb> bombs0) {
        List<Bomb> newbombs1 = new ArrayList<Bomb>();
        for (PlayerID currentPlayerID : currentIDPermutation()) {

            // Calcul des players correspondants aux players ID
            Player currentPlayer = IdToPlayer(currentPlayerID, players0);

            // Calcul du nombre de bombes posées
            int currentPlayerBombs = 0;
            for (Bomb bomb : bombs0) {
                if(bomb.ownerId().equals(currentPlayerID))
                    currentPlayerBombs++;
            }

            // Calcul si une bombe a déjà été déposée à cet endroit
            boolean foundBomb = false;
            // Cas du conflit entre 2 joueurs
            for (Bomb bomb : newbombs1) {
                if(bomb.position().equals(currentPlayer.position().containingCell())) {
                    foundBomb = true; 
                    break;
                }
            }
            for (Bomb bomb : bombs0) {
                if(bomb.position().equals(currentPlayer.position().containingCell())) {
                    foundBomb = true;
                    break;
                }
            }
            
            /* si il ne veut pas poser de bombe
             *   OU mort 
             *   OU a déjà posé trop de bombes
             *   OU une bombe existe déjà là où il veut en poser
             */
            if(!bombDropEvents.contains(currentPlayerID)
                    || !currentPlayer.isAlive() 
                    || currentPlayerBombs >= currentPlayer.maxBombs()
                    || foundBomb){
            }
            else newbombs1.add(currentPlayer.newBomb());
        }
        return Collections.unmodifiableList(newbombs1);

    }



    /**
     * @param players0          (Liste de joueur à l'état courant)
     * @param playerBonuses     (Table associative associant à chaque ID le bonus obtenu)
     * @param bombedCells1      (L'ensemble des cases de l'état futur ayant une bombe)
     * @param board1            (Le tableau à l'état futur)
     * @param blastedCells1     (L'ensemble des cases de l'état future ayant une blast)
     * @param speedChangeEvents (Table associative associant à chaque ID les changements de direction :
     *                               (N, E, S ou W), ou la valeur optionnelle vide si le joueur veut s'arrêter)
     * @return le prochain état des joueurs
     */
    private static List<Player> nextPlayers(List<Player> players0, Map<PlayerID, 
            Bonus> playerBonuses, Set<Cell> bombedCells1, Board board1,
            Set<Cell> blastedCells1, Map<PlayerID, Optional<Direction>> speedChangeEvents) {

        List<Player> players1 = new ArrayList<Player>();
        for (Player player : players0) {

            // EVOLUTION DE LA POSITION

            Optional<Direction> nextOptDirection = speedChangeEvents.get(player.id());
            DirectedPosition directedPosition0 = new DirectedPosition(player.position(), player.direction());
            Sq<DirectedPosition> directedPositions = null;
            DirectedPosition dpAtNextCentral = player.directedPositions().findFirst(p -> p.position().isCentral());
            SubCell nextCentral = dpAtNextCentral.position();
            Sq<DirectedPosition> travelWhileNotCentral = DirectedPosition.moving(directedPosition0)
                    .takeWhile(dp -> !dp.position().isCentral());

            // il ne veut pas changer de dir
            if (! speedChangeEvents.containsKey(player.id())) {
                directedPositions = player.directedPositions();
            }
            // il veut changer de dir
            else {

                if(nextOptDirection.isPresent()) {
                    Sq<DirectedPosition> travelAfterFoundCentral = DirectedPosition
                            .moving(new DirectedPosition (nextCentral, nextOptDirection.get()));
                    // il veut aller dans une direction parallèle
                    if (nextOptDirection.get().isParallelTo(player.direction())) {
                        directedPositions = DirectedPosition
                                .moving(new DirectedPosition(player.position(), nextOptDirection.get()));
                    }
                    else { // direction perpendiculaire
                        directedPositions = travelWhileNotCentral.concat(travelAfterFoundCentral);
                    }
                }
                else{
                    // il veut s'arrêter
                    directedPositions = travelWhileNotCentral.concat(DirectedPosition
                            .stopped(new DirectedPosition(nextCentral,
                                    dpAtNextCentral.direction())
                                    ));
                }
            }

            // BLOQUAGE 
            
            boolean blockingSubcellWhenBomb = player.position().distanceToCentral() == 6;
            
            if (player.lifeState().canMove()) {
                Optional<Cell> nextCell;
                if(nextOptDirection == null) {
                    nextCell = Optional.of(player.position().containingCell()
                            .neighbor(directedPositions.head().direction()));
                }
                else {
                    if(nextOptDirection.isPresent()){
                        nextCell = Optional.of(player.position().containingCell()
                                .neighbor(nextOptDirection.get()));
                    }
                    else nextCell = Optional.empty();
                }

                Cell cellOfNextCentralSubCell = directedPositions
                        .findFirst(dp -> dp.position().isCentral()).position().containingCell();

                if ((player.position().isCentral() 
                        && nextCell.isPresent() 
                        && ! board1.blockAt(nextCell.get()).canHostPlayer()) 
                        ||
                        (bombedCells1.contains(player.position().containingCell())
                                && blockingSubcellWhenBomb
                                && cellOfNextCentralSubCell.equals(player.position().containingCell()))) {
                }
                else
                    directedPositions = directedPositions.tail();
            }

            // COUPLE (NBR DE VIE, ETAT)

            Sq<LifeState> nextLifeStates;
            if (blastedCells1.contains(directedPositions.head().position().containingCell())
                    && player.lifeState().state().equals(Player.LifeState.State.VULNERABLE)) {

                Sq<LifeState> lifeStateAfterBlast = player.statesForNextLife();
                nextLifeStates = lifeStateAfterBlast;
            } 
            else nextLifeStates = player.lifeStates().tail();

            // CAPACITÉS

            Player playersAfterBonus;
            if (playerBonuses.containsKey(player.id()))
                playersAfterBonus = playerBonuses.get(player.id()).applyTo(player);
            else playersAfterBonus = player;

            players1.add(new Player(player.id(),
                    nextLifeStates,
                    directedPositions,
                    playersAfterBonus.maxBombs(),
                    playersAfterBonus.bombRange()));
        }

        return Collections.unmodifiableList(players1);
    }



    /**
     * @param speedChangeEvents (Les évênements de changement de direction correspondant à chaque joueurs)
     * @param bombDropEvents (Les évênements de dépôt de bombe correspondant à chaque joueurs)
     * 
     * @return l'état du jeu futur 
     */
    public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents, Set<PlayerID> bombDropEvents) {

        // BONUS
        Map<PlayerID, Bonus> playerBonuses = new HashMap<PlayerID, Bonus>();
        Set<Cell> consumedBonuses = new HashSet<Cell>();
        for (PlayerID currentPlayerID : currentIDPermutation()) {

            Player currentPlayer = IdToPlayer(currentPlayerID, players);

            Cell cell = currentPlayer.position().containingCell();
            if (board.blockAt(cell).isBonus() && !consumedBonuses.contains(cell) && currentPlayer.position().isCentral()) {
                consumedBonuses.add(cell);
                // ajoute à playerBonuses les bonus consommés par chacun des joueurs
                playerBonuses.put(currentPlayerID, board.blockAt(cell).associatedBonus());
            }
        }

        // BLASTS
        final List<Sq<Cell>> blasts1 = nextBlasts(blasts, board, explosions);
        final Set<Cell> blastedCells1 = nextBlastedCells(blasts1);

        // BOARD
        final Board board1 = nextBoard(board, consumedBonuses, blastedCells1);

        // EXPLOSIONS
        List<Sq<Sq<Cell>>> explosions1temp = new ArrayList<Sq<Sq<Cell>>>(nextExplosions(explosions));

        // BOMBES
        List<Bomb> bombs0 = new ArrayList<Bomb>(bombs);
        List<Bomb> bombs1temp = new ArrayList<Bomb>();
        bombs0.addAll(newlyDroppedBombs(players, bombDropEvents, bombs));

        for (Bomb bomb : bombs0) {
            if (blastedCells1.contains(bomb.position())) {
                explosions1temp.addAll(bomb.explosion());
            } else {
                Sq<Integer> reducedFuse = bomb.fuseLengths().tail();
                if (reducedFuse.isEmpty()) {
                    explosions1temp.addAll(bomb.explosion());
                } else {
                    bombs1temp.add(new Bomb(bomb.ownerId(), bomb.position(), reducedFuse, bomb.range()));
                }
            }
        }

        final List<Bomb> bombs1 = bombs1temp;

        final List<Sq<Sq<Cell>>> explosions1 = explosions1temp;

        final Set<Cell> bombedCells1 = nextBombedCells(bombs1).keySet();

        // JOUEURS
        final List<Player> players1 = 
                nextPlayers(players, playerBonuses, bombedCells1 , board1, blastedCells1, speedChangeEvents);

        return new GameState(ticks+1, board1, players1, bombs1, explosions1, blasts1);

    }

}
