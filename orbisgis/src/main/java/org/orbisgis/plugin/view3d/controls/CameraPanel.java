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
import com.jmex.editors.swing.widget.VectorPanel;

/**
 * TODO : recenter camera
 * 
 * @author Samuel CHEMLA
 * 
 */
public class CameraPanel extends JPanel {

	private SceneImplementor sceneImplementor = null;

	private VectorPanel cameraLocation = null;

	private VectorPanel cameraDirection = null;

	private JCheckBox parallelProjection = null;

	public CameraPanel(SceneImplementor simpleCanvas) {
		super(new CRFlowLayout());
		this.sceneImplementor = simpleCanvas;

		add(new JLabel("Location : "));
		add(getCameraLocation());

		add(new CarriageReturn());

		add(new JLabel("Direction : "));
		add(getCameraDirection());

		add(new CarriageReturn());

		add(getParallelProjectionCheckBox());

		// We need to keep camera parameters and camera panel synchronized. So
		// we create a thread to do this.
		CameraRefresh test = new CameraRefresh(sceneImplementor);
		new Thread(test).start();

	}

	private VectorPanel getCameraLocation() {
		if (cameraLocation == null) {
			cameraLocation = new VectorPanel(-10000000f, 10000000f, 1f);
			cameraLocation.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					sceneImplementor.getCamera().setLocation(
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
					sceneImplementor.getCamera().setDirection(
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
					boolean parallel = !sceneImplementor.getCamera()
							.isParallelProjection();
					sceneImplementor.getCamera()
							.setParallelProjection(parallel);
					parallelProjection.setSelected(parallel);
				}
			});
		}
		return parallelProjection;
	}

	/**
	 * This runnable class will keep camera settings and camera panel
	 * synchronized.
	 * 
	 * @author Samuel CHEMLA
	 * 
	 */
	private class CameraRefresh implements Runnable {

		private SceneImplementor implementor = null;

		public CameraRefresh(SceneImplementor implementor) {
			this.implementor = implementor;
		}

		public void run() {
			while (true) {
				if (implementor.isSetup()) {
					cameraLocation.setValue(implementor.getCamera()
							.getLocation());
					cameraDirection.setValue(implementor.getCamera()
							.getDirection());

				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}
}
