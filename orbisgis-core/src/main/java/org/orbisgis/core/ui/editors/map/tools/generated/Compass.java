package org.orbisgis.core.ui.editors.map.tools.generated;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.utils.I18N;

public abstract class Compass implements Automaton {

	private static Logger logger = Logger.getLogger(Compass.class.getName());

	private String status = "Standby";

	private MapContext mc;

	private ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();

		if ("Standby".equals(status)) {

		}

		if ("OnePoint".equals(status)) {

		}

		if ("TwoPoints".equals(status)) {

		}

		if ("ThreePoints".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

		ret.add(I18N.getString("orbisgis.core.ui.editors.map.tool.cancel"));

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();

		if ("Standby".equals(status)) {

		}

		if ("OnePoint".equals(status)) {

		}

		if ("TwoPoints".equals(status)) {

		}

		if ("ThreePoints".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

		ret.add("esc");

		return ret.toArray(new String[0]);
	}

	public void init(MapContext mc, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		logger.info("status: " + status);
		this.mc = mc;
		this.tm = tm;
		status = "Standby";
		transitionTo_Standby(mc, tm);
		if (isFinished(status)) {
			throw new FinishedAutomatonException();
		}
	}

	public void transition(String code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);

		if ("Standby".equals(status)) {

			if ("press".equals(code)) {
				String preStatus = status;
				try {
					status = "OnePoint";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_OnePoint(mc, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("OnePoint".equals(status)) {

			if ("press".equals(code)) {
				String preStatus = status;
				try {
					status = "TwoPoints";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_TwoPoints(mc, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("TwoPoints".equals(status)) {

			if ("press".equals(code)) {
				String preStatus = status;
				try {
					status = "ThreePoints";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_ThreePoints(mc, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("ThreePoints".equals(status)) {

			if ("press".equals(code)) {
				String preStatus = status;
				try {
					status = "ThreePoints";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_ThreePoints(mc, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("Cancel".equals(status)) {

		}

		if ("esc".equals(code)) {
			status = "Cancel";
			transitionTo_Cancel(mc, tm);
			if (isFinished(status)) {
				throw new FinishedAutomatonException();
			}
			return;
		}

		throw new NoSuchTransitionException(code);
	}

	public boolean isFinished(String status) {

		if ("Standby".equals(status)) {

			return false;

		}

		if ("OnePoint".equals(status)) {

			return false;

		}

		if ("TwoPoints".equals(status)) {

			return false;

		}

		if ("ThreePoints".equals(status)) {

			return false;

		}

		if ("Cancel".equals(status)) {

			return true;

		}

		throw new RuntimeException("Invalid status: " + status);
	}

	public void draw(Graphics g) throws DrawingException {

		if ("Standby".equals(status)) {
			drawIn_Standby(g, mc, tm);
		}

		if ("OnePoint".equals(status)) {
			drawIn_OnePoint(g, mc, tm);
		}

		if ("TwoPoints".equals(status)) {
			drawIn_TwoPoints(g, mc, tm);
		}

		if ("ThreePoints".equals(status)) {
			drawIn_ThreePoints(g, mc, tm);
		}

		if ("Cancel".equals(status)) {
			drawIn_Cancel(g, mc, tm);
		}

	}

	public abstract void transitionTo_Standby(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Standby(Graphics g, MapContext mc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_OnePoint(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_OnePoint(Graphics g, MapContext mc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_TwoPoints(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_TwoPoints(Graphics g, MapContext mc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_ThreePoints(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_ThreePoints(Graphics g, MapContext mc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_Cancel(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Cancel(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException;

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "Compass";
	}

	public String getMessage() {

		if ("Standby".equals(status)) {
			return "";
		}

		if ("OnePoint".equals(status)) {
			return "";
		}

		if ("TwoPoints".equals(status)) {
			return "";
		}

		if ("ThreePoints".equals(status)) {
			return "";
		}

		if ("Cancel".equals(status)) {
			return "";
		}

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "compass";
	}

	public String getTooltip() {
		return I18N
				.getString("orbisgis.core.ui.editors.map.tool.compass_tooltip");
	}

	private ImageIcon mouseCursor;

	public ImageIcon getImageIcon() {
		if (mouseCursor != null) {
			return mouseCursor;
		} else {
			return null;
		}
	}

	public void setMouseCursor(ImageIcon mouseCursor) {
		this.mouseCursor = mouseCursor;
	}

	public void toolFinished(MapContext mc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {

		if ("Standby".equals(status)) {

		}

		if ("OnePoint".equals(status)) {

		}

		if ("TwoPoints".equals(status)) {

		}

		if ("ThreePoints".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

	}

	public java.awt.Point getHotSpotOffset() {

		return new java.awt.Point(8, 8);

	}

}
