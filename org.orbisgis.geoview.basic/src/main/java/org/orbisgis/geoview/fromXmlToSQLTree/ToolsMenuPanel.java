package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.GeoView2D;

public class ToolsMenuPanel extends JPanel {
	private FunctionsPanel functionsPanel;

	public ToolsMenuPanel() throws JAXBException {
		setLayout(new BorderLayout());

		final JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		final DescriptionScrollPane descriptionScrollPane = new DescriptionScrollPane();

		functionsPanel = new FunctionsPanel(descriptionScrollPane);
		splitPanel.setLeftComponent(functionsPanel);
		splitPanel.setRightComponent(descriptionScrollPane);
		splitPanel.setOneTouchExpandable(true);
		splitPanel.setResizeWeight(1);
		splitPanel.setContinuousLayout(true);
		splitPanel.setPreferredSize(new Dimension(400, 140));

		add(splitPanel, BorderLayout.CENTER);
	}
	
	public void refresh() {
		functionsPanel.refresh();
	}
}