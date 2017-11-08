package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class NapaRule {

    final int target;
    final List<NapaRuleItem> items;

    public NapaRule(final int target, final List<NapaRuleItem> items) {
        this.target = target;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public List<NapaRuleItem> getItems() {
        return items;
    }

    public int getTarget() {
        return target;
    }
}
