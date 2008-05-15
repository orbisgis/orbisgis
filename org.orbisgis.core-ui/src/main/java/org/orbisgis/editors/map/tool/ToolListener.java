package org.orbisgis.editors.map.tool;

public interface ToolListener {

	/**
	 * The state of the current tool has changed
	 *
	 * @param toolManager
	 */
	void stateChanged(ToolManager toolManager);

	/**
	 * The status change in the current tool has failed
	 *
	 * @param toolManager
	 * @param e
	 */
	void transitionException(ToolManager toolManager, TransitionException e);

	/**
	 * The current tool has changed
	 *
	 * @param previous
	 * @param toolManager
	 */
	void currentToolChanged(Automaton previous, ToolManager toolManager);

}
