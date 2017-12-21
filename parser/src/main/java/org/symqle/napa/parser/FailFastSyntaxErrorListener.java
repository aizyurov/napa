package org.symqle.napa.parser;

/**
 * @author lvovich
 */
public class FailFastSyntaxErrorListener implements SyntaxErrorListener {

    @Override
    public void onError(final SyntaxError error) {
        String file = error.getFile();
        if (file == null) {
            file = "unnamed";
        }
        throw new GrammarException(error.toString());
    }
}
