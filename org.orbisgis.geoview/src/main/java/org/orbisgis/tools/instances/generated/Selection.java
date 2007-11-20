
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

public abstract class Selection implements Automaton {

	private static Logger logger = Logger.getLogger(Selection.class.getName());

	private String status = "Standby";

	protected ViewContext ec;

	protected ToolManager tm;

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

	public void init(ViewContext ed, ToolManager tm) throws TransitionException, FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ed;
		this.tm = tm;
		status = "Standby";
		transitionTo_Standby();
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
					transitionTo_OnePoint();
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
					transitionTo_Selection();
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
					transitionTo_OnePointLeft();
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
					transitionTo_Standby();
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
					transitionTo_TwoPoints();
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
					transitionTo_Selection();
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
					transitionTo_Standby();
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
					transitionTo_PointWithSelection();
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
					transitionTo_Movement();
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
					transitionTo_OnePoint();
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
					transitionTo_MakeMove();
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
					transitionTo_Selection();
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
			transitionTo_Standby();
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
			drawIn_Standby(g);
		}
		
		if ("OnePoint".equals(status)) {
			drawIn_OnePoint(g);
		}
		
		if ("OnePointLeft".equals(status)) {
			drawIn_OnePointLeft(g);
		}
		
		if ("TwoPoints".equals(status)) {
			drawIn_TwoPoints(g);
		}
		
		if ("Selection".equals(status)) {
			drawIn_Selection(g);
		}
		
		if ("PointWithSelection".equals(status)) {
			drawIn_PointWithSelection(g);
		}
		
		if ("Movement".equals(status)) {
			drawIn_Movement(g);
		}
		
		if ("MakeMove".equals(status)) {
			drawIn_MakeMove(g);
		}
		
	}

	
	public abstract void transitionTo_Standby() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_OnePoint() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_OnePoint(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_OnePointLeft() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_OnePointLeft(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_TwoPoints() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_TwoPoints(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_Selection() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Selection(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_PointWithSelection() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_PointWithSelection(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_Movement() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Movement(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_MakeMove() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_MakeMove(Graphics g) throws DrawingException;
	

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

	public URL getMouseCursorURL() {
		
		return null;
		
	}

	public void toolFinished() throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		
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
