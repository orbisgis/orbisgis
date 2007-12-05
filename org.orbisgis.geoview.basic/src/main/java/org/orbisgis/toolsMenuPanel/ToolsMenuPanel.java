package org.orbisgis.toolsMenuPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.GeoView2D;

public class ToolsMenuPanel extends JPanel {
	private FunctionsPanel functionsPanel;

	public ToolsMenuPanel(GeoView2D geoview) throws JAXBException {
		setLayout(new BorderLayout());

		final JScrollPane jScrollPane = new JScrollPane();

		final JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		final DescriptionScrollPane descriptionScrollPane = new DescriptionScrollPane(
				geoview);

		splitPanel.setLeftComponent(jScrollPane);
		splitPanel.setRightComponent(descriptionScrollPane);
		splitPanel.setOneTouchExpandable(true);
		splitPanel.setResizeWeight(1);
		splitPanel.setContinuousLayout(true);
		splitPanel.setPreferredSize(new Dimension(400, 140));

		functionsPanel = new FunctionsPanel(geoview, descriptionScrollPane);
		jScrollPane.setViewportView(functionsPanel);
		jScrollPane.setPreferredSize(new Dimension(150, 250));

		add(splitPanel, BorderLayout.CENTER);
	}

	public FunctionsPanel getFunctionsPanel() {
		return functionsPanel;
	}
}