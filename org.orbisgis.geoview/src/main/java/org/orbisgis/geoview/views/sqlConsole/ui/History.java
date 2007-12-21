package org.orbisgis.geoview.views.sqlConsole.ui;

public class History {
	private String[] history = new String[historySize];
	private final static int historySize = 5;

	// fixedPointer is the address of the new sqlCommand...
	private int fixedPointer = 0;

	// farFromFixedPointer is the "length" from the current sqlCommand to the
	// one that corresponds to the fixedPointer when scrolling in the stack
	// history...
	private int farFromFixedPointer = 0;

	private int start = 0;
	private int currentSize = 0;

	public History() {
		history = new String[historySize];
	}

	private int previous(final int idx) {
		return (0 == idx) ? historySize - 1 : idx - 1;
	}

	private int next(final int idx) {
		return (idx + 1) % historySize;
	}

	/**
	 * This method adds a new sqlCommand to the "stack" if and only if it is not
	 * null, not empty and not equal to the last sqlCommand that has been
	 * stored...
	 */
	public void push(final String sqlCommand) {
		if ((null != sqlCommand) && (0 < sqlCommand.length())
				&& (!sqlCommand.equals(history[previous(fixedPointer)]))) {
			// pointer is the address of the new sqlCommand...
			history[fixedPointer] = sqlCommand;
			// increment the fixedPointer
			fixedPointer = next(fixedPointer);
			// reset the farFromFixedPointer
			farFromFixedPointer = 0;

			if (currentSize < historySize) {
				currentSize++;
			} else {
				start = (start + 1) % historySize;
			}
		}
	}

	public String getPrevious() {
		if (isPreviousAvailable()) {
			farFromFixedPointer++;
			return history[(fixedPointer + historySize - farFromFixedPointer)
					% historySize];
		}
		return null;
	}

	public String getNext() {
		if (isNextAvailable()) {
			farFromFixedPointer--;
			return history[(fixedPointer + historySize - farFromFixedPointer)
					% historySize];
		}
		return null;
	}

	public boolean isPreviousAvailable() {
		return (farFromFixedPointer < currentSize);
	}

	public boolean isNextAvailable() {
		return (1 < farFromFixedPointer);
	}
}