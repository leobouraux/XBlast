package ch.epfl.xblast.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ch.epfl.xblast.PlayerAction;


/**
 * 10
 * Un auditeur d'événements clavier.
 * @author Léo Bouraux (257368)
 *
 */
public class KeyboardEventHandler extends KeyAdapter implements KeyListener {

    private final Map<Integer, PlayerAction> m;
    private final Consumer<PlayerAction> c;

    /**
     * Construit l'auditeur d'événements clavier
     */
    public KeyboardEventHandler(Map<Integer, PlayerAction> m, Consumer<PlayerAction> c) {
        this.m = Collections.unmodifiableMap(new HashMap<>(m));
        this.c = c;
    }
    
    /** 
     * @param KeyEvent e : Un événement inquant qu'une touche du clavier à été utilisée
     *
     * Associe une action sur le clavier, à l'action d'un joueur du jeu
     */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        PlayerAction action = m.get(e.getKeyCode());
        if (action != null)
            c.accept(action);
    }
    
    
}


