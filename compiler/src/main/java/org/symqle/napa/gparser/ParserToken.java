package org.symqle.napa.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public interface ParserToken {


    boolean matches(int tag);

    public List<String> preface();

    String text();

    int line();

    int pos();


}
