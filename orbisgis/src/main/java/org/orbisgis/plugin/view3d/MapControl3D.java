package org.orbisgis.plugin.view3d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.view3d.controls.CameraHandler;
import org.orbisgis.plugin.view3d.controls.ToolsPanel;

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

	int width = 640, height = 480;

	// Frame limiter. Maximum is 1000.
	// It provides no guarantee for the frame rate : just a max limit...
	// Doesn't work so good at least...
	private static int maxfps = 1000;

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
		new Thread() {
			{
				setDaemon(true);
			}

			public void run() {
				try {
					// calculates the sleep time according to the max framerate
					int sleepTime = Math.round((float) (1000 / maxfps));
					while (true) {
						if (isVisible())
							glCanvas.repaint();
						Thread.sleep(sleepTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	protected void doResize() {
		if (impl != null) {
			impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
			if (impl.getCamera() != null) {
				Callable<?> exe = new Callable() {
					public Object call() {
						impl.getCamera().setFrustumPerspective(
								45.0f,
								(float) glCanvas.getWidth()
										/ (float) glCanvas.getHeight(), 1,
								10000);
						return null;
					}
				};
				GameTaskQueueManager.getManager()
						.getQueue(GameTaskQueue.RENDER).enqueue(exe);
			}
		}
	}

	public void forceUpdateToSize() {
		// force a resize to ensure proper canvas size.
		glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
		glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);
	}

	protected Canvas getGlCanvas() {
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
