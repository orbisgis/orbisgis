package org.orbisgis.plugin.view3d.controls;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;

import org.orbisgis.plugin.view3d.SceneImplementor;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * This class handles all cam moves. There is a zoom controlled by the mouse's
 * wheel, a pan controlled by right clic and a rotation controlled by left clic.
 * 
 * @author Based on Joshua Slack's original Code
 * @author Samuel CHEMLA
 * 
 */
public class CameraHandler extends MouseAdapter implements MouseMotionListener,
		MouseWheelListener {

	// Used to remember las point
	private Point last = null;

	// TODO ! what's the use of focus ?
	private Vector3f focus = null;

	// TODO : what's the use of vector ?
	private Vector3f vector = null;

	private Quaternion rot = null;

	private SceneImplementor impl = null;

	public CameraHandler(SceneImplementor impl) {
		this.impl = impl;
		last = new Point(0, 0);
		focus = new Vector3f();
		vector = new Vector3f();
		rot = new Quaternion();
	}

	// Ok, mouse is dragged, let's determine which action to do...
	public void mouseDragged(final MouseEvent arg0) {
		Callable<?> exe = new Callable() {
			public Object call() {
				// Calculates how much do we move
				int difX = last.x - arg0.getX();
				int difY = last.y - arg0.getY();
				last.x = arg0.getX();
				last.y = arg0.getY();

				// If the user is pressing shift, we will move faster
				int mult = arg0.isShiftDown() ? 10 : 1;

				int mods = arg0.getModifiers();
				if ((mods & InputEvent.BUTTON1_MASK) != 0) {
					// Left clic : let's rotate the cam
					rotateCamera(impl.getRenderer().getCamera().getUp(),
							difX * 0.0025f);
					rotateCamera(impl.getRenderer().getCamera().getLeft(),
							-difY * 0.0025f);
				}
				if ((mods & InputEvent.BUTTON2_MASK) != 0 && difY != 0) {
					// middle button : let's zoom
					zoomCamera(difY * mult);
				}
				if ((mods & InputEvent.BUTTON3_MASK) != 0) {
					// Right clic : let's translate the cam
					panCamera(-difX, -difY);
				}
				return null;
			}
		};
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
				.enqueue(exe);
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		last.x = arg0.getX();
		last.y = arg0.getY();
	}

	/**
	 * Mouse wheel zoom. Press shift to increase the amount of zoom.
	 */
	public void mouseWheelMoved(final MouseWheelEvent arg0) {
		Callable<?> exe = new Callable() {
			public Object call() {
				zoomCamera(arg0.getWheelRotation()
						* (arg0.isShiftDown() ? -100 : -20));
				return null;
			}
		};
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
				.enqueue(exe);
	}

	/**
	 * TODO : test and comment
	 * 
	 */
	public void recenterCamera() {
		Callable<?> exe = new Callable() {
			public Object call() {
				Camera cam = impl.getRenderer().getCamera();
				Vector3f.ZERO.subtract(focus, vector);
				cam.getLocation().addLocal(vector);
				focus.addLocal(vector);
				cam.onFrameChange();
				return null;
			}
		};
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
				.enqueue(exe);
	}

	/**
	 * Rotate the camera. TODO : need to be improved bacause the cam movements
	 * are not so intuitive.
	 * 
	 * @param axis
	 * @param amount
	 */
	private void rotateCamera(Vector3f axis, float amount) {
		Camera cam = impl.getRenderer().getCamera();
		rot.fromAngleAxis(amount, axis);
		cam.getLocation().subtract(focus, vector);
		rot.mult(vector, vector);
		focus.add(vector, cam.getLocation());
		rot.mult(cam.getLeft(), cam.getLeft());
		rot.mult(cam.getUp(), cam.getUp());
		rot.mult(cam.getDirection(), cam.getDirection());
		cam.normalize();
		cam.onFrameChange();
	}

	/**
	 * Pan camera
	 * @param left
	 * @param up
	 */
	private void panCamera(float left, float up) {
		Camera cam = impl.getRenderer().getCamera();
		cam.getLeft().mult(left, vector);
		vector.scaleAdd(up, cam.getUp(), vector);
		cam.getLocation().addLocal(vector);
		focus.addLocal(vector);
		cam.onFrameChange();
	}

	/**
	 * Zoom camera
	 * @param amount
	 */
	private void zoomCamera(float amount) {
		Camera cam = impl.getRenderer().getCamera();
		float dist = cam.getLocation().distance(focus);
		amount = dist - Math.max(0f, dist - amount);
		cam.getLocation().scaleAdd(amount, cam.getDirection(),
				cam.getLocation());
		cam.onFrameChange();
	}
}
