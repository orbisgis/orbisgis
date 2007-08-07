package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view3d.SimpleCanvas3D;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public class LightPanel extends JPanel {

	private ActionsListener actions = null;

	private JCheckBox lightCheckBox = null;

	public LightPanel(SimpleCanvas3D simpleCanvas) {
		super(new CRFlowLayout());
		actions = new ActionsListener(simpleCanvas);
		add(getLightCheckBox());
	}

	private JCheckBox getLightCheckBox() {
		if (lightCheckBox == null) {
			lightCheckBox = new JCheckBox("Lights enabled", true);
			lightCheckBox.setActionCommand("ADDLIGHT");
			lightCheckBox.addActionListener(actions);
		}
		return lightCheckBox;
	}

	private class ActionsListener implements ActionListener {

		private SimpleCanvas3D simpleCanvas = null;

		private PointLight light = null;

		public ActionsListener(final SimpleCanvas3D simpleCanvas) {
			this.simpleCanvas = simpleCanvas;

			/** Set up a basic, default light. */
			light = new PointLight();
			light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
			light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
			light.setLocation(new Vector3f(100, 100, 100));
			light.setAttenuate(false);
			light.setEnabled(true);

			/**
			 * Attach the light to a lightState and the lightState to rootNode.
			 */

		}

		public void actionPerformed(ActionEvent e) {
			if ("ADDLIGHT".equals(e.getActionCommand())) {
				// Toogle light on or off
				boolean newState = !simpleCanvas.lightState.isEnabled();
				simpleCanvas.lightState.setEnabled(newState);
				simpleCanvas.getRootNode().updateRenderState();
				lightCheckBox.setSelected(newState);

			}
		}

	}

}
