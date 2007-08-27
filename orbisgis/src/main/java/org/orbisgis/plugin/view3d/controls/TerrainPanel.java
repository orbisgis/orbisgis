package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.CarriageReturn;
import org.orbisgis.plugin.view3d.SceneImplementor;
import org.orbisgis.plugin.view3d.geometries.TerrainBlock3D;

import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.editors.swing.widget.ValuePanel;

public class TerrainPanel extends JPanel {

	SceneImplementor simpleCanvas = null;

	public TerrainPanel(SceneImplementor simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;
		add(getLoadTerrain());
		add(new CarriageReturn());
		add(getLoadTexture());
		add(new CarriageReturn());
		add(getSetSize());
	}

	private JButton getLoadTerrain() {
		JButton button = new JButton("Load Terrain");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser("png", "*.png");
				if (fc.showOpenDialog(TerrainPanel.this) == JFileChooser.APPROVE_OPTION) {
					File fileToLoad = fc.getSelectedFile();

					if (simpleCanvas.getRootNode().getChild("Terrain") != null) {
						simpleCanvas.getRootNode().detachChildNamed("Terrain");
					}

					try {
						TerrainBlock3D tb = new TerrainBlock3D(new ImageIcon(
								fileToLoad.toURI().toURL()).getImage());
						tb.setName("Terrain");
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

	private JButton getLoadTexture() {
		JButton button = new JButton("Load Texture");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (simpleCanvas.getRootNode().getChild("Terrain") != null) {
					FileChooser fc = new FileChooser("png", "*.png");
					if (fc.showOpenDialog(TerrainPanel.this) == JFileChooser.APPROVE_OPTION) {
						File fileToLoad = fc.getSelectedFile();
						try {

							TextureState ts = simpleCanvas.getRenderer()
									.createTextureState();
							ts.setTexture(TextureManager.loadTexture(
									new ImageIcon(fileToLoad.toURI().toURL())
											.getImage(),
									Texture.MM_LINEAR_LINEAR,
									Texture.FM_LINEAR, true));
							TerrainBlock3D tb = (TerrainBlock3D) simpleCanvas
									.getRootNode().getChild("Terrain");

							tb.setRenderState(ts);
							tb.updateRenderState();

						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		return button;
	}

	private JPanel getSetSize() {
		final ValuePanel size = new ValuePanel("Z scale", "m", 1f, 10f, 0.5f);
		size.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (simpleCanvas.getRootNode().getChild("Terrain") != null) {
					TerrainBlock3D terrain = (TerrainBlock3D) simpleCanvas
							.getRootNode().getChild("Terrain");
					terrain.setHeightMap(size.getFloatValue());
				}
			}
		});
		return size;
	}

}