package org.orbisgis.plugin.view3d.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;

public class ToolsPanel extends JPanel {

	private JTabbedPane tabbedPane = null;

	private JPanel lightPanel = null;

	private JPanel camPanel = null;

	public ToolsPanel() {
		super(new BorderLayout());
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.add(getLightPanel(), "Lights");
		tabbedPane.add(getCamPanel(), "Camera");

		tabbedPane.setPreferredSize(new Dimension(300, 200));
		
		add(tabbedPane, BorderLayout.CENTER);
	}

	private JPanel getCamPanel() {
		if (camPanel == null) {
			camPanel = new JPanel(new CRFlowLayout());
			camPanel.add(new JLabel("Camera settings"));
		}
		return camPanel;
	}

	private JPanel getLightPanel() {
		if (lightPanel == null) {
			lightPanel = new JPanel(new CRFlowLayout());
			lightPanel.add(new JCheckBox("Lights enabled", true));
		}
		return lightPanel;
	}
}
