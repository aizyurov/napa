package org.symqle.epic.gparser;

import java.io.IOException;

/**
 * @author lvovich
 */
public interface ParserTokenizer {

    ParserToken nextToken() throws IOException;


}
