package org.symqle.napa.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class CollectingSyntaxErrorListener implements SyntaxErrorListener {

    private final List<SyntaxError> errors = new ArrayList<>();

    @Override
    public void onError(final SyntaxError error) {
        errors.add(error);
    }

    public List<SyntaxError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
