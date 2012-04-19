/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.util.List;
import org.orbisgis.legend.Legend;

/**
 * Common interface to all the {@code Legend} instances that represent a unique
 * symbol.
 * @author alexis
 */
public interface UniqueSymbol extends Legend {

        /**
         * As unique symbols are configurable, this method lets the caller
         * retrieve the needed parameters.
         * @return
         * A map where the key is a description of the parameter, and the value
         * is the current value of the parameter.
         */
        List<USParameter<?>> getParameters();

}
