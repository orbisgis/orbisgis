package org.orbisgis.graphicModeler.tools;

public class InsertProcessTool extends AbstractInsertionTool {
	@Override
	protected String[] getAvailableElements() {
		// TODO Get the processes from somewhere
		return new String[] { "Process1", "Process2", "Process3", "Process4" };
	}

	@Override
	protected String getType() {
		return PROCESS_TYPE;
	}
}
