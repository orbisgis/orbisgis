
package org.orbisgis.plugin.view.tools.instances.generated;

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.plugin.view.tools.Automaton;
import org.orbisgis.plugin.view.tools.DrawingException;
import org.orbisgis.plugin.view.tools.EditionContext;
import org.orbisgis.plugin.view.tools.FinishedAutomatonException;
import org.orbisgis.plugin.view.tools.NoSuchTransitionException;
import org.orbisgis.plugin.view.tools.ToolManager;
import org.orbisgis.plugin.view.tools.TransitionException;

public abstract class Pan implements Automaton {

	private static Logger logger = Logger.getLogger(Pan.class.getName());

	private String status = "Standby";

	protected EditionContext ec;

	protected ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("RectangleDone".equals(status)) {
			
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
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("RectangleDone".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
			ret.add("esc");
			

		return ret.toArray(new String[0]);
	}

	public void init(EditionContext ed, ToolManager tm) throws TransitionException, FinishedAutomatonException {
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
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "RectangleDone";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_RectangleDone();
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
		
		if ("RectangleDone".equals(status)) {
			
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
		
		if ("Cancel".equals(status)) {
			
		}
		
		if ("esc".equals(code)) {
			status = "Cancel";
			transitionTo_Cancel();
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
		
		if ("OnePointLeft".equals(status)) {
			
				return false;
			
		}
		
		if ("RectangleDone".equals(status)) {
			
				return false;
			
		}
		
		if ("Cancel".equals(status)) {
			
				return true;
			
		}
		

		throw new RuntimeException("Invalid status: " + status);
	}


	public void draw(Graphics g) throws DrawingException {
		
		if ("Standby".equals(status)) {
			drawIn_Standby(g);
		}
		
		if ("OnePointLeft".equals(status)) {
			drawIn_OnePointLeft(g);
		}
		
		if ("RectangleDone".equals(status)) {
			drawIn_RectangleDone(g);
		}
		
		if ("Cancel".equals(status)) {
			drawIn_Cancel(g);
		}
		
	}

	
	public abstract void transitionTo_Standby() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_OnePointLeft() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_OnePointLeft(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_RectangleDone() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_RectangleDone(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_Cancel() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Cancel(Graphics g) throws DrawingException;
	

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "Pan";
	}

	public String getMessage() {
		
		if ("Standby".equals(status)) {
			return Messages.getString("pan_standby");
		}
		
		if ("OnePointLeft".equals(status)) {
			return Messages.getString("pan_onepointleft");
		}
		
		if ("RectangleDone".equals(status)) {
			return Messages.getString("");
		}
		
		if ("Cancel".equals(status)) {
			return Messages.getString("");
		}
		

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "pan";
	}

	public String getTooltip() {
		return Messages.getString("pan_tooltip");
	}

	public URL getIconURL() {
		return this.getClass().getResource("/org/orbisgis/plugin/view/tools/instances/generated/pan.png");
	}

	public URL getMouseCursorURL() {
		
		return this.getClass().getResource("/org/orbisgis/plugin/view/tools/instances/generated/pan.png");
		
	}

	public void toolFinished() throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("OnePointLeft".equals(status)) {
			
		}
		
		if ("RectangleDone".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
	}

}
