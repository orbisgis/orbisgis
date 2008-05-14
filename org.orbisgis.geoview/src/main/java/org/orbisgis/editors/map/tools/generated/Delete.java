
package org.orbisgis.editors.map.tools.generated;

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.editors.map.tool.Automaton;
import org.orbisgis.editors.map.tool.DrawingException;
import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.layerModel.MapContext;

public abstract class Delete implements Automaton {

	private static Logger logger = Logger.getLogger(Delete.class.getName());

	private String status = "Standby";

	private MapContext ec;

	private ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		

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
			
		}
		

		throw new NoSuchTransitionException(code);
	}

	public boolean isFinished(String status) {

		
		if ("Standby".equals(status)) {
			
				return true;
			
		}
		

		throw new RuntimeException("Invalid status: " + status);
	}


	public void draw(Graphics g) throws DrawingException {
		
		if ("Standby".equals(status)) {
			drawIn_Standby(g, ec, tm);
		}
		
	}

	
	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm) throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) throws DrawingException;
	

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "Delete";
	}

	public String getMessage() {
		
		if ("Standby".equals(status)) {
			return Messages.getString("");
		}
		

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "delete";
	}

	public String getTooltip() {
		return Messages.getString("delete_tooltip");
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
		
	}

	public java.awt.Point getHotSpotOffset() {
		
		return new java.awt.Point(8, 8);
		
	}

}
