package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view3d.SimpleCanvas3D;
import org.orbisgis.plugin.view3d.geometries.TerrainBlock3D;

public class TerrainPanel extends JPanel {

	SimpleCanvas3D simpleCanvas = null;

	public TerrainPanel(SimpleCanvas3D simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;
		add(getLoadTerrain());
	}

	private JButton getLoadTerrain() {
		JButton button = new JButton("Load Terrain");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser("png", "*.png");
				if (fc.showOpenDialog(TerrainPanel.this) == JFileChooser.APPROVE_OPTION) {
					File fileToLoad = fc.getSelectedFile();
					try {

						TerrainBlock3D tb = new TerrainBlock3D(new ImageIcon(
								fileToLoad.toURI().toURL()).getImage());

						simpleCanvas.getRootNode().attachChild(tb);
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		return button;
	}

}
