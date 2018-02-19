package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2/4
 * Contient des méthodes travaillant sur les listes.
 * @author Léo Bouraux (257368)
 *
 */
public final class Lists {

    private Lists () {
        
    }
   
    
    /**
     * @param l
     *      une liste 
     * @return une version symétrique de la liste donnée
     * @throws IllegalArgumentException si l est vide
     */
    public static <T> List<T> mirrored(List<T> l) {
        List<T> list = new ArrayList<T>(l);
        if (list.isEmpty()) { 
            throw new IllegalArgumentException();
        }

        List<T> l2 = new ArrayList<>();
        l2.addAll(list);
        List<T> l3 = list.subList(0, list.size()-1);
        Collections.reverse(l3);
        l2.addAll(l3);
        return Collections.unmodifiableList(l2);
        
    }
    
    
    /**
     * @param l (Une liste)
     *          
     * @return les permutations de la liste donnée en argument, dans un ordre quelconque
     */
    public static <T> List<List<T>> permutations(List<T> l) {
        List<T> list = new ArrayList<T>(l);
        List<List<T>> res = new ArrayList<List<T>>();
        if(list.size()==0){
            res.add(new ArrayList<T>());
            return Collections.unmodifiableList(res);
        }
        //appel récursif en enlevant le premier élément
        List<List<T>> rec = permutations(list.subList(1, list.size()));
        for (List<T> perm : rec) {
            for (int i = 0; i <= perm.size(); i++) {
                List<T> l1 = new ArrayList<T>(perm);
                l1.add(i, list.get(0));
                res.add(l1);
            }
        }
        return Collections.unmodifiableList(res);
        
    }
    
}
