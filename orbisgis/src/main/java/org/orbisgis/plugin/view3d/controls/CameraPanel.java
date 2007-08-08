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
import org.orbisgis.plugin.view3d.SimpleCanvas3D;
import com.jmex.editors.swing.widget.VectorPanel;

public class CameraPanel extends JPanel {

	private SimpleCanvas3D simpleCanvas = null;

	private VectorPanel cameraLocation = null;

	private VectorPanel cameraDirection = null;

	private JCheckBox parallelProjection = null;

	public CameraPanel(final SimpleCanvas3D simpleCanvas) {
		super(new CRFlowLayout());
		this.simpleCanvas = simpleCanvas;

		add(new JLabel("Location : "));
		add(getCameraLocation());

		add(new CarriageReturn());

		add(new JLabel("Direction : "));
		add(getCameraDirection());

		add(new CarriageReturn());

		add(getParallelProjectionCheckBox());

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (simpleCanvas.isSetup()) {
						cameraLocation.setValue(simpleCanvas.getCamera()
								.getLocation());
						cameraDirection.setValue(simpleCanvas.getCamera()
								.getDirection());

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
			cameraLocation = new VectorPanel(-10000000f, 10000000f, 1f);
			cameraLocation.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					simpleCanvas.getCamera().setLocation(
							cameraLocation.getValue());
				}
			});
		}
		return cameraLocation;
	}

	private VectorPanel getCameraDirection() {
		if (cameraDirection == null) {
			cameraDirection = new VectorPanel(-1f, 1f, 0.1f);
			cameraDirection.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					simpleCanvas.getCamera().setDirection(
							cameraDirection.getValue());
				}
			});
		}
		return cameraDirection;
	}

	private JCheckBox getParallelProjectionCheckBox() {
		if (parallelProjection == null) {
			parallelProjection = new JCheckBox("Parallel Projection ??", false);
			parallelProjection.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					boolean parallel = !simpleCanvas.getCamera()
							.isParallelProjection();
					simpleCanvas.getCamera().setParallelProjection(parallel);
					parallelProjection.setSelected(parallel);
				}
			});
		}
		return parallelProjection;
	}
}
