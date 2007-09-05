package org.orbisgis.plugin.view3d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JComponent;

import org.orbisgis.plugin.view3d.controls.CameraHandler;

import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.LoggingSystem;
import com.jmex.awt.JMECanvas;

/**
 * The map control is responsible for displaying and refreshing the map. It
 * creates a canvas, a thread to control its refreshing and a SceneImplementor.
 * The canvas is only responsible for displaying the scene, which is implemented
 * by the SceneImplementor.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class MapControl3D extends JComponent {

	// This provide you a way to stop rendering
	public static boolean render = false;

	// This will greatly improve rendering speed (renders as fast as possible)
	public static final int quickRendering = 0;
	
	// This is for a normal controlled rendering speed (renders at maxfps)
	public static final int normalRendering = 1;
	
	// Set the mode to normalRendering
	public static int renderingMode = normalRendering;
	
	// Contains the 3D view
	private Canvas glCanvas;

	// Handles the camera movements
	private CameraHandler camhand = null;

	// Responsible for implementing the universe and refreshing it
	private SceneImplementor impl = null;

	// Size of the canvas
	private int width = 640, height = 480;

	// Frame limiter. Maximum is 1000. Tries to keep the frame rate to this. Not
	// widely tested...
	private static int maxfps = 25;

	public MapControl3D() {
		setLayout(new BorderLayout());

		// Set the logging system to warning only
		LoggingSystem.getLogger().setLevel(Level.WARNING);

		impl = new SceneImplementor(width, height);

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
				float gain = 0.1f;

				while (true) {
					if (render && isVisible())
						glCanvas.repaint();

					// Get the tpf measurment
					realTPF = 1000 * impl.getTimePerFrame();

					// Calculate the error
					error = Math.round(error + tpf_command - realTPF);

					// Error limiter
					if (error >= 500)
						error = 500;
					if (error < -500)
						error = -500;

					// Apply the gain
					sleepTime = Math.round((float) error * gain);

					// Generate command
					sleepTime = Math.max(sleepTime, 0);

					// Apply the command
					try {
						Thread.sleep(sleepTime*renderingMode);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// for debug :
					// System.out.println(error + " " + sleepTime);
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

	/**
	 * Retrieves or create the Canvas
	 * 
	 * @return
	 */
	private Canvas getGlCanvas() {
		if (glCanvas == null) {

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

			// Creates a camera handler which handles all camera movements
			// (zoom, rotation and translation)...
			camhand = new CameraHandler(impl);

			// ...and then register it.
			glCanvas.addMouseWheelListener(camhand);
			glCanvas.addMouseListener(camhand);
			glCanvas.addMouseMotionListener(camhand);

			// Important! Here is where we add the guts to the canvas:
			((JMECanvas) glCanvas).setImplementor(impl);

			// Nedded to display correctly...
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
