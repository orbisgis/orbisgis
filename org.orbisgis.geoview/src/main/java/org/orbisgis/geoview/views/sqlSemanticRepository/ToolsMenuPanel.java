package org.orbisgis.geoview.views.sqlSemanticRepository;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBException;

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
}