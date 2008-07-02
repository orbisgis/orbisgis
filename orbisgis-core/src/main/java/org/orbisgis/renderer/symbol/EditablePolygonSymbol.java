package org.orbisgis.renderer.symbol;

import java.awt.Color;

public interface EditablePolygonSymbol extends EditableLineSymbol {

	Color getFillColor();

	void setFillColor(Color fillColor);

}
