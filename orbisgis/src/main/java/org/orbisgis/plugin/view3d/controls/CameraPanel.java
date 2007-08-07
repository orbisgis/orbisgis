package org.orbisgis.plugin.view3d.controls;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.plugin.view.ui.workbench.geocatalog.CRFlowLayout;
import org.orbisgis.plugin.view3d.SimpleCanvas3D;

import com.jme.math.Vector3f;
import com.jmex.editors.swing.widget.VectorPanel;

public class CameraPanel extends JPanel {

	private SimpleCanvas3D simpleCanvas = null;

	private VectorPanel cameraLocation = null;

	public CameraPanel(final SimpleCanvas3D simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;

		add(new JLabel("Location : "));
		add(getCameraLocation());

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (simpleCanvas.isSetup()) {
						cameraLocation.setValue(simpleCanvas.getCamera()
								.getLocation());
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private VectorPanel getCameraLocation() {
		if (cameraLocation == null) {
			cameraLocation = new VectorPanel(-100000f, 100000f, 1f);
			cameraLocation.setValue(new Vector3f(100f, 100f, 100f));
			cameraLocation.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					simpleCanvas.getCamera().setLocation(
							cameraLocation.getValue());
				}
			});
		}
		return cameraLocation;
	}

	// private class ActionsListener implements ActionListener {
	//
	// public void actionPerformed(ActionEvent e) {
	// // TODO Auto-generated method stub
	// if ("action".equals(e.getActionCommand())) {
	// }
	// }
	//
	// }

}
