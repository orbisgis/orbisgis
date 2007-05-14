
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

public abstract class VertexAdition implements Automaton {

	private static Logger logger = Logger.getLogger(VertexAdition.class.getName());

	private String status = "Standby";

	protected EditionContext ec;

	protected ToolManager tm;

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
					status = "Done";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Done();
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
			drawIn_Standby(g);
		}
		
		if ("Done".equals(status)) {
			drawIn_Done(g);
		}
		
		if ("Cancel".equals(status)) {
			drawIn_Cancel(g);
		}
		
	}

	
	public abstract void transitionTo_Standby() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Standby(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_Done() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Done(Graphics g) throws DrawingException;
	
	public abstract void transitionTo_Cancel() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_Cancel(Graphics g) throws DrawingException;
	

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

	public URL getIconURL() {
		return this.getClass().getResource("/org/orbisgis/plugin/view/tools/instances/generated/vertexAdition.png");
	}

	public URL getMouseCursorURL() {
		
		return null;
		
	}

	public void toolFinished() throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		
		if ("Standby".equals(status)) {
			
		}
		
		if ("Done".equals(status)) {
			
		}
		
		if ("Cancel".equals(status)) {
			
		}
		
	}

}
