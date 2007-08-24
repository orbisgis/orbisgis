package org.orbisgis.plugin.view3d.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.orbisgis.plugin.view3d.SceneImplementor;

public class ToolsPanel extends JPanel {

	private SceneImplementor simpleCanvas = null;

	private JTabbedPane tabbedPane = null;

	private LightPanel lightPanel = null;

	private CameraPanel camPanel = null;
	
	private AppearancePanel appearancePanel = null;
	
	private TerrainPanel terrainPanel = null;

	public ToolsPanel(SceneImplementor simpleCanvas) {
		super(new BorderLayout());

		this.simpleCanvas = simpleCanvas;

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.add(getLightPanel(), "Lights");
		tabbedPane.add(getCamPanel(), "Camera");
		tabbedPane.add(getAppearancePanel(), "Appearance");
		tabbedPane.add(getTerrainPanel(), "Terrain");

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
	
	private JPanel getTerrainPanel() {
		if (terrainPanel == null) {
			terrainPanel = new TerrainPanel(simpleCanvas);
		}
		return terrainPanel;
	}
}
