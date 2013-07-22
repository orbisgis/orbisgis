package org.orbisgis.legend.thematic.uom;

import org.orbisgis.core.renderer.se.common.Uom;

/**
 * This interface must be used on every legend that has an
 * inner UOM used to configure the symbol's dimensions.
 * @author Alexis Guéganno
 */
public interface SymbolUom {

    /**
     * Gets the unit of measure used to size the associated {@code Stroke}.
     * @return The current Uom
     */
    Uom getSymbolUom();

    /**
     * Sets the unit of measure used to size the associated {@code Stroke}.
     * @param u The new Uom
     */
     void setSymbolUom(Uom u);
}
