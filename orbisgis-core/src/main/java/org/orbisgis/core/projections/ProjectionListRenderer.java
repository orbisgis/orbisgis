package org.orbisgis.core.projections;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.orbisgis.core.ui.geocatalog.newSourceWizards.SourceRenderer;

public class ProjectionListRenderer implements ListCellRenderer {

	private SourceRenderer[] renderers;
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRenderers(ProjectionRenderer[] projectionRenderers) {
		this.renderers = renderers;		
	}

}
