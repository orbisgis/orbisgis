/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.core.ui.editors.map.tools.generated;

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;

public abstract class Selection implements Automaton {

	private static Logger logger = Logger.getLogger(Selection.class.getName());

	private String status = "Standby";

	private MapContext ec;

	private ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("OnePoint".equals(status)) {
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("TwoPoints".equals(status)) {
			
		}
		
		if ("Selection".equals(status)) {
			
		}
		
		if ("PointWithSelection".equals(status)) {
			
		}
		
		if ("Movement".equals(status)) {
			
		}
		
		if ("MakeMove".equals(status)) {
			
		}
		
			ret.add(Messages.getString("cancel"));
			

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("OnePoint".equals(status)) {
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("TwoPoints".equals(status)) {
			
		}
		
		if ("Selection".equals(status)) {
			
		}
		
		if ("PointWithSelection".equals(status)) {
			
		}
		
		if ("Movement".equals(status)) {
			
		}
		
		if ("MakeMove".equals(status)) {
			
		}
		
			ret.add("esc");
			

		return ret.toArray(new String[0]);
	}

	public void init(MapContext ec, ToolManager tm) throws TransitionException, FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ec;
		this.tm = tm;
		status = "Standby";
		transitionTo_Standby(ec, tm);
		if (isFinished(status)){
			throw new FinishedAutomatonException();
		}
	}

	public void transition(String code) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);

		
		if ("Standby".equals(status)) {
			
			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "OnePoint";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_OnePoint(ec, tm);
					if (isFinished(status)){
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
			
			if ("selection".equals(code)) {
				String preStatus = status;
				try {
					status = "Selection";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Selection(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
			if ("no-selection".equals(code)) {
				String preStatus = status;
				try {
					status = "OnePointLeft";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_OnePointLeft(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
			if ("init".equals(code)) {
				String preStatus = status;
				try {
					status = "Standby";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Standby(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "TwoPoints";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_TwoPoints(ec, tm);
					if (isFinished(status)){
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
			
			if ("selection".equals(code)) {
				String preStatus = status;
				try {
					status = "Selection";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Selection(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
			if ("no-selection".equals(code)) {
				String preStatus = status;
				try {
					status = "Standby";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Standby(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("Selection".equals(status)) {
			
			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "PointWithSelection";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_PointWithSelection(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("PointWithSelection".equals(status)) {
			
			if ("in-handler".equals(code)) {
				String preStatus = status;
				try {
					status = "Movement";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Movement(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
			if ("out-handler".equals(code)) {
				String preStatus = status;
				try {
					status = "OnePoint";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_OnePoint(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("Movement".equals(status)) {
			
			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "MakeMove";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_MakeMove(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("MakeMove".equals(status)) {
			
			if ("empty".equals(code)) {
				String preStatus = status;
				try {
					status = "Selection";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Selection(ec, tm);
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			
		}
		
		if ("esc".equals(code)) {
			status = "Standby";
			transitionTo_Standby(ec, tm);
			if (isFinished(status)){
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
		
		if ("OnePointLeft".equals(status)) {
			
				return false;
			
		}
		
		if ("TwoPoints".equals(status)) {
			
				return false;
			
		}
		
		if ("Selection".equals(status)) {
			
				return false;
			
		}
		
		if ("PointWithSelection".equals(status)) {
			
				return false;
			
		}
		
		if ("Movement".equals(status)) {
			
				return false;
			
		}
		
		if ("MakeMove".equals(status)) {
			
				return false;
			
		}
		

		throw new RuntimeException("Invalid status: " + status);
	}


	public void draw(Graphics g) throws DrawingException {
		
		if ("Standby".equals(status)) {
			drawIn_Standby(g, ec, tm);
		}
		
		if ("OnePoint".equals(status)) {
			drawIn_OnePoint(g, ec, tm);
		}
		
		if ("OnePointLeft".equals(status)) {
			drawIn_OnePointLeft(g, ec, tm);
		}
		
		if ("TwoPoints".equals(status)) {
			drawIn_TwoPoints(g, ec, tm);
		}
		
		if ("Selection".equals(status)) {
			drawIn_Selection(g, ec, tm);
		}
		
		if ("PointWithSelection".equals(status)) {
			drawIn_PointWithSelection(g, ec, tm);
		}
		
		if ("Movement".equals(status)) {
			drawIn_Movement(g, ec, tm);
		}
		
		if ("MakeMove".equals(status)) {
			drawIn_MakeMove(g, ec, tm);
		}
		
	}

	
	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_OnePoint(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_OnePoint(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_OnePointLeft(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_OnePointLeft(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_TwoPoints(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_TwoPoints(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_Selection(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Selection(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_PointWithSelection(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_PointWithSelection(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_Movement(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Movement(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_MakeMove(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_MakeMove(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "Selection";
	}

	public String getMessage() {
		
		if ("Standby".equals(status)) {
			return Messages.getString("selection_standby");
		}
		
		if ("OnePoint".equals(status)) {
			return Messages.getString("");
		}
		
		if ("OnePointLeft".equals(status)) {
			return Messages.getString("selection_onepointleft");
		}
		
		if ("TwoPoints".equals(status)) {
			return Messages.getString("");
		}
		
		if ("Selection".equals(status)) {
			return Messages.getString("selection_selection");
		}
		
		if ("PointWithSelection".equals(status)) {
			return Messages.getString("");
		}
		
		if ("Movement".equals(status)) {
			return Messages.getString("selection_movement");
		}
		
		if ("MakeMove".equals(status)) {
			return Messages.getString("");
		}
		

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "select";
	}

	public String getTooltip() {
		return Messages.getString("selection_tooltip");
	}

	private String mouseCursor;

	public URL getMouseCursorURL() {
		if (mouseCursor != null) {
			return this.getClass().getResource(mouseCursor);
		} else {
			return null;
		}
	}

	public void setMouseCursor(String mouseCursor) {
		this.mouseCursor = mouseCursor;
	}

	public void toolFinished(MapContext vc, ToolManager tm) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("OnePoint".equals(status)) {
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("TwoPoints".equals(status)) {
			
		}
		
		if ("Selection".equals(status)) {
			
		}
		
		if ("PointWithSelection".equals(status)) {
			
		}
		
		if ("Movement".equals(status)) {
			
		}
		
		if ("MakeMove".equals(status)) {
			
		}
		
	}

	public java.awt.Point getHotSpotOffset() {
		
		return new java.awt.Point(8, 8);
		
	}

}
