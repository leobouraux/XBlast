package ch.epfl.xblast.server.debug;

import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
//DEBUG

public final class GameStatePrinter {
    private GameStatePrinter() {}

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();

        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p: ps) {
                    if (p.position().containingCell().equals(c)) {
                        System.out.print(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                if (s.bombedCells().containsKey(c)) {
                    System.out.print("òò");
                }
                else {
                    if (s.blastedCells().contains(c)) {
                        System.out.print("**");
                    }
                    else {
                        Block b = board.blockAt(c);
                        System.out.print(stringForBlock(b));
                    }
                }
            }
        

        System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append(p.id().ordinal() + 1);
        switch (p.direction()) {
        case N: b.append('↑'); break;
        case E: b.append('→'); break;
        case S: b.append('↓'); break;
        case W: b.append('←'); break;
        }
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE: return "  ";
        case INDESTRUCTIBLE_WALL: return "##";
        case DESTRUCTIBLE_WALL: return "??";
        case CRUMBLING_WALL: return "¿¿";
        case BONUS_BOMB: return "+b";
        case BONUS_RANGE: return "+r";
        default: throw new Error();
        }
    }
}

/*
// CONSOLE

public final class GameStatePrinter {
    private GameStatePrinter() {}

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();

        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p: ps) {
                    if (p.position().containingCell().equals(c)) {
                        System.out.print(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                if (s.bombedCells().containsKey(c)) {
                    System.out.print("òò");
                }
                else {
                    if (s.blastedCells().contains(c)) {
                        System.out.print("**");
                    }
                    else {
                        Block b = board.blockAt(c);
                        System.out.print(stringForBlock(b));
                    }
                }
            }
        

        System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append(p.id().ordinal() + 1);
        switch (p.direction()) {
        case N: b.append('↑'); break;
        case E: b.append('→'); break;
        case S: b.append('↓'); break;
        case W: b.append('←'); break;
        }
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE: return "  ";
        case INDESTRUCTIBLE_WALL: return "##";
        case DESTRUCTIBLE_WALL: return "??";
        case CRUMBLING_WALL: return "¿¿";
        case BONUS_BOMB: return "+b";
        case BONUS_RANGE: return "+r";
        default: throw new Error();
        }
    }
}

*/


// TERMINAL
/*public final class GameStatePrinter {
    private GameStatePrinter() {}

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();
        //efface le game
        System.out.println("\033[H\033[2J");

        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p: ps) {
                    if (p.position().containingCell().equals(c)) {
                        System.out.print(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                if (s.bombedCells().containsKey(c)) {
                    System.out.print("\u001b[42mòò\u001b[m");
                }
                else {
                    if (s.blastedCells().contains(c) && board.blockAt(c).isFree()) {
                        System.out.print("\u001b[41m**\u001b[m");
                    }
                    else {
                        Block b = board.blockAt(c);
                        System.out.print(stringForBlock(b));
                    }
                }
            }
        

        System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append("\u001b[46m"+(p.id().ordinal() + 1));
        switch (p.direction()) {
        case N: b.append('↑'); break;
        case E: b.append('→'); break;
        case S: b.append('↓'); break;
        case W: b.append('←'); break;
        }
        b.append("\u001b[m");
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE: return "\u001b[47m  \001";
        case INDESTRUCTIBLE_WALL: return "\u001b[40m  \u001b[m";
        case DESTRUCTIBLE_WALL: return "\u001b[40m??\u001b[m";
        case CRUMBLING_WALL: return "\u001b[40m¿¿\u001b[m";
        case BONUS_BOMB: return "\u001b[45m+b\u001b[m";
        case BONUS_RANGE: return "\u001b[45m+r\u001b[m";
        default: throw new Error();
        }
    }
}*/
