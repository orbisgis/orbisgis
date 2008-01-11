/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.tools.instances.generated;

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.tools.Automaton;
import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.NoSuchTransitionException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;

public abstract class VertexAdition implements Automaton {

	private static Logger logger = Logger.getLogger(VertexAdition.class.getName());

	private String status = "Standby";

	private ViewContext ec;

	private ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("Done".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
			ret.add(Messages.getString("cancel"));
			

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("Done".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
			ret.add("esc");
			

		return ret.toArray(new String[0]);
	}

	public void init(ViewContext ec, ToolManager tm) throws TransitionException, FinishedAutomatonException {
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
					status = "Done";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Done(ec, tm);
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
		
		if ("Done".equals(status)) {
			
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
		
		if ("Cancel".equals(status)) {
			
		}
		
		if ("esc".equals(code)) {
			status = "Cancel";
			transitionTo_Cancel(ec, tm);
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
		
		if ("Done".equals(status)) {
			
				return false;
			
		}
		
		if ("Cancel".equals(status)) {
			
				return true;
			
		}
		

		throw new RuntimeException("Invalid status: " + status);
	}


	public void draw(Graphics g) throws DrawingException {
		
		if ("Standby".equals(status)) {
			drawIn_Standby(g, ec, tm);
		}
		
		if ("Done".equals(status)) {
			drawIn_Done(g, ec, tm);
		}
		
		if ("Cancel".equals(status)) {
			drawIn_Cancel(g, ec, tm);
		}
		
	}

	
	public abstract void transitionTo_Standby(ViewContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_Done(ViewContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException;
	
	public abstract void transitionTo_Cancel(ViewContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Cancel(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException;
	

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "VertexAdition";
	}

	public String getMessage() {
		
		if ("Standby".equals(status)) {
			return Messages.getString("vertexAdition_standby");
		}
		
		if ("Done".equals(status)) {
			return Messages.getString("");
		}
		
		if ("Cancel".equals(status)) {
			return Messages.getString("");
		}
		

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "vertexAdition";
	}

	public String getTooltip() {
		return Messages.getString("vertexAdition_tooltip");
	}

	public URL getMouseCursorURL() {
		
		return null;
		
	}

	public void toolFinished(ViewContext vc, ToolManager tm) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("Done".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
	}

	public java.awt.Point getHotSpotOffset() {
		
		return new java.awt.Point(8, 8);
		
	}

}
