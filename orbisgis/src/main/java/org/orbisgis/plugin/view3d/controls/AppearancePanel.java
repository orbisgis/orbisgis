package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view3d.SimpleCanvas3D;

public class AppearancePanel extends JPanel {

	private SimpleCanvas3D simpleCanvas = null;

	private JCheckBox wirestateCheckBox = null;

	public AppearancePanel(SimpleCanvas3D simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;

		add(getWirestateCheckBox());

	}

	private JCheckBox getWirestateCheckBox() {
		if (wirestateCheckBox == null) {
			wirestateCheckBox = new JCheckBox("Wire aspect", false);
			wirestateCheckBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					boolean newState = !simpleCanvas.getWireState().isEnabled();
					simpleCanvas.getWireState().setEnabled(newState);
					simpleCanvas.getRootNode().updateRenderState();
					wirestateCheckBox.setSelected(newState);
				}

			});
		}
		return wirestateCheckBox;
	}
}
