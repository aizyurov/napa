package org.symqle.epic.regexp.first;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
public class CharacterSetRegistry {

    private static Set<CharacterSet> allSets = new HashSet<>();

    public static void register(CharacterSet characterSet) {
        allSets.add(characterSet);
    }

    public static int size() {

        return allSets.size();
    }
}
