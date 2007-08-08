package org.orbisgis.plugin.view3d.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.orbisgis.plugin.view3d.SimpleCanvas3D;

public class ToolsPanel extends JPanel {

	private SimpleCanvas3D simpleCanvas = null;

	private JTabbedPane tabbedPane = null;

	private LightPanel lightPanel = null;

	private CameraPanel camPanel = null;
	
	private AppearancePanel appearancePanel = null;

	public ToolsPanel(SimpleCanvas3D simpleCanvas) {
		super(new BorderLayout());

		this.simpleCanvas = simpleCanvas;

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.add(getLightPanel(), "Lights");
		tabbedPane.add(getCamPanel(), "Camera");
		tabbedPane.add(getAppearancePanel(), "Appearance");

		//tabbedPane.setPreferredSize(new Dimension(250, 600));

		add(tabbedPane, BorderLayout.CENTER);
	}

	private JPanel getCamPanel() {
		if (camPanel == null) {
			camPanel = new CameraPanel(simpleCanvas);
		}
		return camPanel;
	}

	private JPanel getLightPanel() {
		if (lightPanel == null) {
			lightPanel = new LightPanel(simpleCanvas);
		}
		return lightPanel;
	}
	
	private JPanel getAppearancePanel() {
		if (appearancePanel == null) {
			appearancePanel = new AppearancePanel(simpleCanvas);
		}
		return appearancePanel;
	}
}
