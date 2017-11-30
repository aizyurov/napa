package org.symqle.epic.gparser;

import java.util.Map;

/**
 * @author lvovich
 */
public abstract class AbstractRuleItem  implements RuleItem {
    public NapaRuleItem toNapaRuleItem(final CompiledGrammar grammar, final Map<RuleItem, NapaRuleItem> cache) {
        NapaRuleItem cached = cache.get(this);
        if (cached == null) {
            cached = createNapaRuleItem(grammar, cache);
            cache.put(this, cached);
        }
        return cached;
    }

    protected abstract NapaRuleItem createNapaRuleItem(CompiledGrammar grammar, Map<RuleItem, NapaRuleItem> cache);
}
