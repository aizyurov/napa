package org.symqle.napa.gparser;

import org.symqle.napa.parser.NapaChartNode;
import org.symqle.napa.parser.RawSyntaxNode;

import java.util.List;

/**
 * @author lvovich
 */
public class ProcessingResult {
    private final List<NapaChartNode> noShift;
    private final List<NapaChartNode> shiftCandidates;
    private final List<RawSyntaxNode> accepted;

    public ProcessingResult(final List<NapaChartNode> noShift, final List<NapaChartNode> shiftCandidates, final List<RawSyntaxNode> accepted) {
        this.noShift = noShift;
        this.shiftCandidates = shiftCandidates;
        this.accepted = accepted;
    }

    public List<NapaChartNode> getNoShift() {
        return noShift;
    }

    public List<NapaChartNode> getShiftCandidates() {
        return shiftCandidates;
    }

    public List<RawSyntaxNode> getAccepted() {
        return accepted;
    }
}
