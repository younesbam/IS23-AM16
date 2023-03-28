package it.polimi.ingsw;

import java.util.List;

/**
 * Utilities class used to keep most used methods that don't have a specific class
 */
public final class Utils {
    /**
     * Check if two lists are equal, based on the size and same elements
     * @param l1 first list
     * @param l2 second list
     * @return Boolean
     */
    public static Boolean equalLists(List<?> l1, List<?> l2){
        if(l1.size() != l2.size()){
            return false;
        }
        for(int i=0; i<l1.size(); i++){
            if(! l1.get(i).equals(l2.get(i)))
                return false;
        }
        return true;
    }
}
