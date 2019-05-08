package org.symqle.napa.parser;

import java.util.List;

public interface Grammar {
    List<NapaRule> getNapaRules(int target);

    int findNonTerminalByName(String name);

    String nonTerminalName(int tag);
}
