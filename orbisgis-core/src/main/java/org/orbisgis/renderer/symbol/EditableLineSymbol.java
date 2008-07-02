package org.orbisgis.renderer.symbol;

import java.awt.Color;

public interface EditableLineSymbol extends EditableSymbol {

	Color getOutlineColor();

	int getLineWidth();

	void setOutlineColor(Color color);

	void setLineWidth(int width);

}
