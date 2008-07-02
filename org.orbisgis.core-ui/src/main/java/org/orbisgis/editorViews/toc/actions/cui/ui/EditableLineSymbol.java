package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.Color;

public interface EditableLineSymbol extends EditableSymbol {

	Color getOutlineColor();

	int getLineWidth();

	void setOutlineColor(Color background);

	void setLineWidth(int value);

}
