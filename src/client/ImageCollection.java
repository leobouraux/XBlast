package ch.epfl.xblast.client;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

/**
 * 9
 * Une collection d'images provenant d'un répertoire et indexées par un entier.
 * But : permettre d'obtenir les images des différents éléments
 * du jeu en fonction de l'entier (octet) qui leur correspond.
 * 
 * @author Léo Bouraux (257368)
 *
 */
public final class ImageCollection {

    private File dir;
    private final Map<Integer, BufferedImage> images; //key = le préfixe de 3 chiffres d'une image
    
    
    
    /**
     * @param dirName (Le nom d'un répertoire)
     * 
     * Construit une collection d'image, en liant le nom (int) d'une image à son répertoire
     */
    public ImageCollection(String dirName) {
        images = new HashMap<Integer, BufferedImage>();
        try {
            this.dir = new File(ImageCollection.class
                    .getClassLoader()
                    .getResource(dirName)
                    .toURI());

            for (File file : dir.listFiles()) {
                int key = Integer.parseInt(file.getName().substring(0, 3));
                BufferedImage value; 
                try {
                    value = ImageIO.read(file);
                    images.put(key, value);
                } catch (IOException e) {
                    System.err.println("Erreur au chargement de l'image "+file.getName());
                }
            }
        } catch (URISyntaxException e1) {
            System.err.println("Erreur au chargement du répertoire "+dir.getName());
        }

    }
    
    
    /**
     * @param index (Le numéro de l'image)
     * @return une image d'index donné
     * @throws NoSuchElementException si aucune image ne correspond à l'index
     */
    public Image image(int index) {
        Image i = imageOrNull(index);
        if (i==null)
            throw new NoSuchElementException();
        else return i;
    }
    
    
    /**
     * @param index (Le numéro de l'image)
     * @return une image d'index donné ou null si aucune image ne correspond à l'index
     */
    public Image imageOrNull(int index) {
        return images.get(index);
    }

    
    /**
     * Collection d'images représentant les joueurs
     */
    public static ImageCollection PLAYERS_IMAGES = new ImageCollection("player");
    
    
    /**
     * Collection d'images représentant les blocs
     */
    public static ImageCollection BLOCKS_IMAGES = new ImageCollection("block");
    
    
    /**
     * Collection d'images représentant les explosions et bombes
     */
    public static ImageCollection EXPLOSIONS_IMAGES = new ImageCollection("explosion");
    
    
    /**
     * Collection d'images représentant les scores et le temps
     */
    public static ImageCollection SCORE_IMAGES = new ImageCollection("score");
    
    
    
}
