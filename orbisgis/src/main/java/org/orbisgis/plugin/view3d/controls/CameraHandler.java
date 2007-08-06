package org.orbisgis.plugin.view3d.controls;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;

import org.orbisgis.plugin.view3d.MyImplementor;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * This class handles all cam moves.
 * 
 * @author Based on Joshua Slack's original Code
 * @author Samuel CHEMLA
 * 
 */
public class CameraHandler extends MouseAdapter implements MouseMotionListener,
		MouseWheelListener {

	private Point last = null;

	private Vector3f focus = null;

	private Vector3f vector = null;

	private Quaternion rot = null;

	private MyImplementor impl = null;

	public CameraHandler(MyImplementor impl) {
		this.impl = impl;
		last = new Point(0, 0);
		focus = new Vector3f();
		vector = new Vector3f();
		rot = new Quaternion();
	}

	public void mouseDragged(final MouseEvent arg0) {
		Callable<?> exe = new Callable() {
			public Object call() {
				int difX = last.x - arg0.getX();
				int difY = last.y - arg0.getY();
				int mult = arg0.isShiftDown() ? 10 : 1;
				last.x = arg0.getX();
				last.y = arg0.getY();

				int mods = arg0.getModifiers();
				if ((mods & InputEvent.BUTTON1_MASK) != 0) {
					rotateCamera(impl.getRenderer().getCamera().getUp(), difX * 0.0025f);
					rotateCamera(impl.getRenderer().getCamera().getLeft(),
							-difY * 0.0025f);
				}
				if ((mods & InputEvent.BUTTON2_MASK) != 0 && difY != 0) {
					zoomCamera(difY * mult);
				}
				if ((mods & InputEvent.BUTTON3_MASK) != 0) {
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

	private void panCamera(float left, float up) {
		Camera cam = impl.getRenderer().getCamera();
		cam.getLeft().mult(left, vector);
		vector.scaleAdd(up, cam.getUp(), vector);
		cam.getLocation().addLocal(vector);
		focus.addLocal(vector);
		cam.onFrameChange();
	}

	private void zoomCamera(float amount) {
		Camera cam = impl.getRenderer().getCamera();
		float dist = cam.getLocation().distance(focus);
		amount = dist - Math.max(0f, dist - amount);
		cam.getLocation().scaleAdd(amount, cam.getDirection(),
				cam.getLocation());
		cam.onFrameChange();
	}
}
