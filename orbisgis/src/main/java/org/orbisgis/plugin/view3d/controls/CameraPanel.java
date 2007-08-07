package org.orbisgis.plugin.view3d.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;

public class CameraPanel extends JPanel {

	public CameraPanel() {
		super(new CRFlowLayout());
		add(new JLabel("Camera settings"));
	}

	private class ActionsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if ("NEWGV".equals(e.getActionCommand())) {
			}
		}

	}

}
