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

	public ToolsPanel(SimpleCanvas3D simpleCanvas) {
		super(new BorderLayout());

		this.simpleCanvas = simpleCanvas;
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.add(getLightPanel(), "Lights");
		tabbedPane.add(getCamPanel(), "Camera");

		tabbedPane.setPreferredSize(new Dimension(300, 200));

		add(tabbedPane, BorderLayout.CENTER);
	}

	private JPanel getCamPanel() {
		if (camPanel == null) {
			camPanel = new CameraPanel();
		}
		return camPanel;
	}

	private JPanel getLightPanel() {
		if (lightPanel == null) {
			lightPanel = new LightPanel(simpleCanvas);
		}
		return lightPanel;
	}
}
