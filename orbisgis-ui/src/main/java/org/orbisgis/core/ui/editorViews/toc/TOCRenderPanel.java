package org.orbisgis.core.ui.editorViews.toc;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JTree;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Style;

public interface TOCRenderPanel {

	public void setNodeCosmetic(JTree tree, ILayer value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus);

	public Component getJPanel();

	public void setNodeCosmetic(JTree tree, ILayer layer, Style s,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus);

	public Rectangle getCheckBoxBounds();

}
