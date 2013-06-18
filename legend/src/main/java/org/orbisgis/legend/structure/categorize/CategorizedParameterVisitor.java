package org.orbisgis.legend.structure.categorize;

import org.orbisgis.legend.structure.parameter.ParameterVisitor;

/**
 * This visitor is dedicated to the processing of {@link CategorizedLegend} instances.
 * @author Alexis Guéganno
 */
public interface CategorizedParameterVisitor extends ParameterVisitor {

    /**
     * Visit the given {@link CategorizedLegend}. It's the intelligent part of the visitor implementation.
     * @param legend
     */
    void visit(CategorizedLegend legend);
}
