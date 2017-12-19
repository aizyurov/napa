package org.symqle.napa.parser;

import java.io.PrintStream;

/**
 * @author lvovich
 */
public class PrintingSyntaxErrorListener implements SyntaxErrorListener {

    private final PrintStream printStream;

    public PrintingSyntaxErrorListener(final PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void onError(final SyntaxError error) {
        printStream.print(error);
    }
}
