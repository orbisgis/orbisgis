package org.orbisgis.core.map.projection;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.orbisgis.core.ui.geocatalog.newSourceWizards.SourceRenderer;
import org.orbisgis.core.ui.plugins.views.geocatalog.SourceListRenderer;

public class ProjectionTab extends JPanel {
	
	private ProjectionList projectionList;
	private ProjectionListener projectionListener;
	private ProjectionModel projectionModel;
	
	public ProjectionTab() {
		projectionList = new ProjectionList();
		projectionListener = new ProjectionListener();
		projectionList.addMouseListener(projectionListener);
		projectionModel = new ProjectionModel();
		projectionList.setModel(projectionModel);
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		add(new JScrollPane(projectionList), BorderLayout.CENTER);
		
		ProjectionListRenderer cellRenderer = new ProjectionListRenderer();
		cellRenderer.setRenderers(new ProjectionRenderer[0]);
		projectionList.setCellRenderer(cellRenderer);
	}

}
