package org.orbisgis.plugin.view3d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JPanel;

import org.orbisgis.plugin.view3d.controls.CameraHandler;

import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.LoggingSystem;
import com.jmex.awt.JMECanvas;

/**
 * The map control is responsible for displaying the map and controlling it.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class MapControl3D extends JPanel {

	// Contains the 3D view
	private Canvas glCanvas;

	// Handles the camera movements
	private CameraHandler camhand = null;

	// Responsible for implementing the universe and refreshing it
	private SimpleCanvas3D impl = null;

	private int width = 640, height = 480;

	// Frame limiter. Maximum is 1000. Tries to keep the frame rate to this. Not
	// widely tested...
	private static int maxfps = 80;

	protected MapControl3D() {
		setLayout(new BorderLayout());

		// Set the logging system to warning only
		LoggingSystem.getLogger().setLevel(Level.WARNING);

		impl = new SimpleCanvas3D(width, height);

		// I tried to use another splitpane but it doesn't refresh well...
		// final JSplitPane splitPane = new JSplitPane(
		// JSplitPane.HORIZONTAL_SPLIT);
		// splitPane.setOneTouchExpandable(true);
		// splitPane.setRightComponent(getGlCanvas());
		// splitPane.setLeftComponent(new ToolsPanel());
		// add(splitPane, BorderLayout.CENTER);

		// Add and create the canvas
		add(getGlCanvas(), BorderLayout.CENTER);

		// Starts the loop !!
		// TODO : stop the loop if the window isn't displayed
		new Thread() {
			{
				setDaemon(true);
			}

			public void run() {
				/**
				 * Le code qui suit est un asservissement simple du nombre de
				 * frames par secondes. il a besoin d'être testé sur des
				 * configurations très différentes afin de bien le régler.
				 */
				// calculates the sleep time according to the max framerate
				// TODO : under test
				// This is the command requested by the user
				int tpf_command = Math.round((float) (1000 / maxfps));

				// This is the command applied
				int sleepTime = tpf_command;

				// This is the error calculated
				int error = 0;

				// This is the tpf measurement
				float realTPF = 0;

				// Corrector gain. Do not set too high or you will loose
				// control over framerate
				float gain = 0.04f;

				while (true) {
					if (isVisible())
						glCanvas.repaint();

					// Get the tpf measurment
					realTPF = 1000 * impl.getTimePerFrame();

					// Calculate the error
					error = Math.round(error + tpf_command - realTPF);

					// Error limiter
					if (error >= 500)
						error = 100;
					if (error < -500)
						error = -100;

					// Apply the gain
					sleepTime = Math.round((float) error * gain);

					// Generate command
					sleepTime = Math.max(sleepTime, 0);

					// Apply the command
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// for debug :
					System.out.println(error + " " + sleepTime);
				}
			}
		}.start();

	}

	private void doResize() {
		impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
		if (impl.getCamera() != null) {
			Callable<?> exe = new Callable() {
				public Object call() {
					impl.getCamera().setFrustumPerspective(
							45.0f,
							(float) glCanvas.getWidth()
									/ (float) glCanvas.getHeight(), 1, 10000);
					return null;
				}
			};
			GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
					.enqueue(exe);
		}
	}

	public void forceUpdateToSize() {
		// force a resize to ensure proper canvas size.
		glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
		glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);
	}

	private Canvas getGlCanvas() {
		if (glCanvas == null) {

			// -------------GL STUFF------------------

			// make the canvas:
			glCanvas = DisplaySystem.getDisplaySystem().createCanvas(width,
					height);
			glCanvas.setMinimumSize(new Dimension(100, 100));

			// add a listener... if window is resized, we can do something about
			// it.
			glCanvas.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent ce) {
					doResize();
				}
			});

			camhand = new CameraHandler(impl);

			glCanvas.addMouseWheelListener(camhand);
			glCanvas.addMouseListener(camhand);
			glCanvas.addMouseMotionListener(camhand);

			// Important! Here is where we add the guts to the canvas:
			((JMECanvas) glCanvas).setImplementor(impl);

			// -----------END OF GL STUFF-------------

			Callable<?> exe = new Callable() {
				public Object call() {
					forceUpdateToSize();
					return null;
				}
			};
			GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
					.enqueue(exe);
		}
		return glCanvas;
	}

}
