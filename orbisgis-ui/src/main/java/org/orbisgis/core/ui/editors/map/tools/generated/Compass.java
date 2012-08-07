/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
