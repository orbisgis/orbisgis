package org.orbisgis.core.ui.editorViews.toc;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JTree;

import org.orbisgis.core.layerModel.ILayer;

public interface TOCRenderPanel{


	public void setNodeCosmetic(JTree tree, ILayer value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) ;

	public Component getJPanel();

	public void setNodeCosmetic(JTree tree, ILayer layer, int legendIndex,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus);

	public Rectangle getCheckBoxBounds();



}
