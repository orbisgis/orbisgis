package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.CarriageReturn;
import org.orbisgis.plugin.view3d.SceneImplementor;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jmex.editors.swing.widget.VectorPanel;

public class LightPanel extends JPanel {

	SceneImplementor simpleCanvas = null;

	private ActionsListener actions = null;

	private JCheckBox lightCheckBox = null;

	private VectorPanel lightLocation = null;

	public LightPanel(SceneImplementor simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;
		actions = new ActionsListener();

		add(getLightCheckBox());
		add(new CarriageReturn());
		add(new JLabel("Light Position : "));
		add(getLightLocation());
	}

	private VectorPanel getLightLocation() {
		if (lightLocation == null) {
			lightLocation = new VectorPanel(-100000f, 100000f, 1f);
			lightLocation.setValue(new Vector3f(100f, 100f, 1000f));
			lightLocation.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					PointLight light = (PointLight) simpleCanvas.getLightState()
							.get(0);
					light.setLocation(lightLocation.getValue());
				}
			});
		}
		return lightLocation;
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

		public ActionsListener() {
			// OTHER POSSIBILITIES TO IMPLEMENT IN THE PANEL
			// light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
			// light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
			// light.setAttenuate(false);
			// light.setEnabled(true);

		}

		public void actionPerformed(ActionEvent e) {
			if ("ADDLIGHT".equals(e.getActionCommand())) {
				// Toogle light on or off
				boolean newState = !simpleCanvas.getLightState().isEnabled();
				simpleCanvas.getLightState().setEnabled(newState);
				simpleCanvas.getRootNode().updateRenderState();
				lightCheckBox.setSelected(newState);

			}
		}

	}

}
