package org.orbisgis.plugin.view3d.controls;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.orbisgis.plugin.view.ui.workbench.FileChooser;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view3d.SimpleCanvas3D;

import com.jme.math.Vector3f;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.ImageBasedHeightMap;

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

						ImageBasedHeightMap heightMap = new ImageBasedHeightMap(
								new ImageIcon(fileToLoad.toURL()).getImage());
						Vector3f terrainScale = new Vector3f(10, 1, 10);
						heightMap.setHeightScale(0.001f);
						TerrainBlock tb = new TerrainBlock("Terrain", heightMap
								.getSize(), terrainScale, heightMap
								.getHeightMap(), new Vector3f(0, 0, 0), false);

						simpleCanvas.getRootNode().attachChild(tb);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});
		return button;
	}

}
