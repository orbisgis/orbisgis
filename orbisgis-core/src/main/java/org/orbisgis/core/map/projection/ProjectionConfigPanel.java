package org.orbisgis.core.map.projection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.orbisgis.core.sif.AbstractUIPanel;

public class ProjectionConfigPanel extends AbstractUIPanel {
	
	private JPanel projectionPanel;
	private JTabbedPane projectionTabbedPane;
	private ProjectionTab projectionTab;
	private WKTTab wktTab;
	
	public ProjectionConfigPanel() {
		projectionPanel = new JPanel();
		projectionTabbedPane = new JTabbedPane();
		projectionTab = new ProjectionTab();
		wktTab = new WKTTab();
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		projectionTabbedPane.addTab("Projections list", null, projectionTab,null);
		projectionTabbedPane.addTab("WKT projection", null, wktTab,null);
		projectionTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		projectionPanel.setLayout(new GridLayout(1, 1));
		projectionPanel.add(projectionTabbedPane, BorderLayout.CENTER);				
	}

	@Override
	public Component getComponent() {
		return projectionPanel;
	}

	@Override
	public String getTitle() {
		return "Projection configuration";
	}

	@Override
	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

}
