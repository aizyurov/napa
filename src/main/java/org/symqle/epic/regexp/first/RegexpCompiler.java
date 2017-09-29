package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.second.Dfa;
import org.symqle.epic.regexp.second.NfaNode2;
import org.symqle.epic.regexp.second.SecondStep;
import org.symqle.epic.regexp.second.ThirdStep;

import java.util.Collection;
import java.util.List;

/**
 * @author lvovich
 */
public class RegexpCompiler {

    public void compile(List<String> tokens, List<String> ignored) {
        NfaNode1  nfaNode1 = new FirstStep(tokens, ignored).makeNfa();
        Collection<NfaNode2> nfa2 = new SecondStep().convert(nfaNode1);
        Dfa dfa = new ThirdStep().build(nfa2);

    }
}
